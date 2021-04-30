package com.needayeah.elastic.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import com.needayeah.elastic.common.enums.TransferEnum;
import com.sun.istack.internal.NotNull;
import lombok.SneakyThrows;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import tk.mybatis.mapper.weekend.Fn;
import tk.mybatis.mapper.weekend.reflection.Reflections;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author Feinik
 * @Discription 实体属性转换工具
 * @Version 1.0.0
 */
public class BeanUtils {

    private static final String ENUM_GET_TYPE_METHOD = "getType";

    public enum TransformEnumType {
        ENUM_TO_VALUE,
        VALUE_TO_ENUM
    }

    /**
     * <pre>
     *     List<UserBean> userBeans = userDao.queryUsers();
     *     List<UserDTO> userDTOs = BeanUtil.batchTransform(UserDTO.class, userBeans);
     * </pre>
     */
    public static <T> List<T> batchTransform(final Class<T> clazz, List<? extends Object> srcList) {
        if (CollectionUtils.isEmpty(srcList)) {
            return Collections.emptyList();
        }

        List<T> result = new ArrayList<>(srcList.size());
        for (Object srcObject : srcList) {
            result.add(transform(clazz, srcObject));
        }
        return result;
    }


    /**
     * 对象属性值拷贝，支持将Long类型的字段值格式化为Date类型
     * 枚举转Integer
     *
     * @param targetClazz
     * @param source
     * @param <T>
     * @return
     */
    @SneakyThrows
    public static <T> T transform(@NotNull final Class<T> targetClazz, Object source, Function<Date, Long> fun) {
        Objects.requireNonNull(source);
        Objects.requireNonNull(targetClazz);

        T t = transform(targetClazz, source);

        BeanInfo sourceInfo = Introspector.getBeanInfo(source.getClass());
        BeanInfo targetInfo = Introspector.getBeanInfo(targetClazz);

        PropertyDescriptor[] sourceInfoPropertyDescriptors = sourceInfo.getPropertyDescriptors();
        PropertyDescriptor[] targetInfoPropertyDescriptors = targetInfo.getPropertyDescriptors();

        for (PropertyDescriptor propertyDescriptor : sourceInfoPropertyDescriptors) {
            Method readMethod = propertyDescriptor.getReadMethod();
            for (PropertyDescriptor targetInfoPropertyDescriptor : targetInfoPropertyDescriptors) {
                Method writeMethod = targetInfoPropertyDescriptor.getWriteMethod();
                Method tReadMethod = targetInfoPropertyDescriptor.getReadMethod();

                if (propertyDescriptor.getName().equals(targetInfoPropertyDescriptor.getName())
                        && tReadMethod.getName().equals(readMethod.getName())
                        && readMethod.getReturnType().equals(Date.class)) {

                    if (targetInfoPropertyDescriptor.getPropertyType().equals(Long.class)) {
                        Object invoke = readMethod.invoke(source);
                        writeMethod.invoke(t, fun.apply((Date) invoke));
                    }

                    if (targetInfoPropertyDescriptor.getPropertyType().equals(long.class)) {
                        Object invoke = readMethod.invoke(source);
                        long value = invoke == null ? 0L : fun.apply((Date) invoke);
                        writeMethod.invoke(t, value);
                    }
                }
            }
        }

        return t;
    }


    /**
     * 对象属性值拷贝，支持将Date类型的字段值格式化为String类型
     *
     * @param clazz
     * @param srcList
     * @param <T>
     * @return
     */
    public static <T> List<T> batchTransform(final Class<T> clazz, List<? extends Object> srcList, boolean dateToLong) {
        if (CollectionUtils.isEmpty(srcList)) {
            return Collections.emptyList();
        }

        List<T> result = new ArrayList<>(srcList.size());
        for (Object srcObject : srcList) {
            result.add(transform(clazz, srcObject, dateToLong));
        }
        return result;
    }

    /**
     * 批量对象属性值拷贝
     * 1、支持将Date类型的字段值格式化为yyyy-MM-dd HH:mm:ss 字符串类型
     * 2、支持将int或String转为枚举类型，或枚举类型转为int或String
     *
     * @param clazz
     * @param srcList
     * @param <T>
     * @return
     */
    public static <T> List<T> batchTransform(final Class<T> clazz, List<? extends Object> srcList, boolean dateToLong, TransformEnumType type) {
        if (CollectionUtils.isEmpty(srcList)) {
            return Collections.emptyList();
        }

        List<T> result = new ArrayList<>(srcList.size());
        for (Object srcObject : srcList) {
            result.add(transform(clazz, srcObject, dateToLong, type));
        }
        return result;
    }

    /**
     * 批量对象属性值拷贝
     * 1、支持将int或String转为枚举类型，或枚举类型转为int或String
     *
     * @param clazz
     * @param srcList
     * @param type
     * @param <T>
     * @return
     */
    public static <T> List<T> batchTransform(final Class<T> clazz, List<? extends Object> srcList, TransformEnumType type) {
        return batchTransform(clazz, srcList, false, type);
    }

    /**
     * 对象属性值拷贝
     * 1、支持将int或String转为枚举类型，或枚举类型转为int或String
     *
     * @param clazz
     * @param src
     * @param type
     * @param <T>
     * @return
     */
    public static <T> T transform(Class<T> clazz, Object src,
                                  TransformEnumType type) {
        return transform(clazz, src, false, type);
    }

    /**
     * 对象属性值拷贝
     * 1、支持将Date类型的字段值格式化为yyyy-MM-dd HH:mm:ss 字符串类型
     * 2、支持将int或String转为枚举类型，或枚举类型转为int或String
     *
     * @param clazz
     * @param src
     * @param dateToLong
     * @param type
     * @param <T>
     * @return
     */
    public static <T> T transform(Class<T> clazz, Object src,
                                  boolean dateToLong,
                                  TransformEnumType type) {
        T target = transform(clazz, src);
        if (target == null) {
            return null;
        }
        Map<String, PropertyDescriptor> targetPdMap = null;
        try {
            BeanWrapper srcBean = new BeanWrapperImpl(src);
            PropertyDescriptor[] srcPds = srcBean.getPropertyDescriptors();
            if (dateToLong || TransformEnumType.ENUM_TO_VALUE == type) {
                for (PropertyDescriptor srcPd : srcPds) {
                    String pdName = srcPd.getName();
                    Object srcPdVal = srcBean.getPropertyValue(pdName);
                    if (targetPdMap == null) {
                        targetPdMap = getPdMap(target);
                    }
                    if (srcPdVal instanceof Date && dateToLong) {
                        Date date = (Date) srcPdVal;
                        final Long dateLong = DateUtil.convertDateToLong(date);
                        PropertyDescriptor targetPd = targetPdMap.get(pdName);
                        if (targetPd != null && targetPd.getPropertyType().equals(Long.class)) {
                            //获取携带set的方法
                            targetPd.getWriteMethod().invoke(target, dateLong);
                        }
                    }
                    if (srcPdVal instanceof Long && dateToLong) {
                        Long date = (Long) srcPdVal;
                        PropertyDescriptor targetPd = targetPdMap.get(pdName);
                        if (targetPd != null && targetPd.getPropertyType().equals(Date.class)) {
                            //获取携带set的方法
                            targetPd.getWriteMethod().invoke(target, new Date(date));
                        }
                    }
                    if (srcPdVal instanceof TransferEnum && TransformEnumType.ENUM_TO_VALUE == type) {
                        PropertyDescriptor targetPd = targetPdMap.get(pdName);
                        if (Objects.nonNull(targetPd)) {
                            Object result = ((TransferEnum) srcPdVal).getValue();
                            targetPd.getWriteMethod().invoke(target, result);
                        }
                    }
                }
            }
            if (TransformEnumType.VALUE_TO_ENUM == type) {
                final BeanWrapper targetBean = new BeanWrapperImpl(target);
                PropertyDescriptor[] targetPds = targetBean.getPropertyDescriptors();
                for (PropertyDescriptor targetPd : targetPds) {
                    String pdName = targetPd.getName();
                    Class<?> targetPdType = targetPd.getPropertyType();
                    if (targetPdType.isEnum()) {
                        Object srcValue;
                        try {
                            srcValue = srcBean.getPropertyValue(pdName);
                        } catch (Exception e) {
                            srcValue = null;
                        }
                        if (Objects.isNull(srcValue) || srcValue.getClass().isEnum()) {
                            continue;
                        }
                        Object result = null;
                        if (srcValue instanceof Integer) {
                            Method getTypeMethod;
                            try {
                                getTypeMethod = targetPdType.getMethod(ENUM_GET_TYPE_METHOD, Integer.TYPE);
                            } catch (Exception e) {
                                getTypeMethod = targetPdType.getMethod(ENUM_GET_TYPE_METHOD, Integer.class);
                            }
                            result = getTypeMethod.invoke(targetPdType, srcValue);

                        } else if (srcValue instanceof String) {
                            try {
                                Method getTypeMethod = targetPdType.getMethod(ENUM_GET_TYPE_METHOD, String.class);
                                result = getTypeMethod.invoke(targetPdType, srcValue);
                            } catch (Exception e) {
                                Class<Enum> targetEnum = (Class<Enum>) targetPdType;
                                result = Enum.valueOf(targetEnum, (String) srcValue);
                            }

                        }
                        targetPd.getWriteMethod().invoke(target, result);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return target;
    }

    private static <T> Map<String, PropertyDescriptor> getPdMap(T obj) {
        BeanWrapper bean = new BeanWrapperImpl(obj);
        PropertyDescriptor[] pds = bean.getPropertyDescriptors();
        return Arrays.stream(pds).collect(Collectors.toMap(PropertyDescriptor::getName, p -> p));
    }

    /**
     * 封装{@link org.springframework.beans.BeanUtils}，惯用与直接将转换结果返回
     *
     * <pre>
     *      UserBean userBean = new UserBean("username");
     *      return BeanUtil.transform(UserDTO.class, userBean);
     * </pre>
     */
    public static <T> T transform(Class<T> clazz, Object src) {
        if (src == null) {
            return null;
        }
        T instance;
        try {
            instance = clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        org.springframework.beans.BeanUtils.copyProperties(src, instance, getNullPropertyNames(src));
        return instance;
    }

    public static <T> T transformInJson(Object src, TypeReference<?> reference) {
        if (src == null) {
            return null;
        }
        String instanceData = JSON.toJSONString(src);
        T obj = (T) JSON.parseObject(instanceData, reference);
        return obj;
    }

    /**
     * 对象属性值拷贝，支持将Date类型的字段值格式化为String类型
     *
     * @param clazz
     * @param src
     * @param dateToLong
     * @param <T>
     * @return
     */
    public static <T> T transform(Class<T> clazz, Object src, boolean dateToLong) {
        return transform(clazz, src, dateToLong, null);
    }

    private static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();
        for (PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                emptyNames.add(pd.getName());
            }
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    private static PropertyDescriptor getObjPropertyDescriptor(Object obj, String propertyName) {
        BeanWrapper src = new BeanWrapperImpl(obj);
        final PropertyDescriptor[] pds = src.getPropertyDescriptors();
        for (PropertyDescriptor pd : pds) {
            if (pd.getName().equals(propertyName)) {
                return pd;
            }
        }
        return null;
    }

    private static Field deepFindField(Class<? extends Object> clazz, String key) {
        Field field = null;
        while (!clazz.getName().equals(Object.class.getName())) {
            try {
                field = clazz.getDeclaredField(key);
                if (field != null) {
                    break;
                }
            } catch (Exception e) {
                clazz = clazz.getSuperclass();
            }
        }
        return field;
    }

    /**
     * 获取某个对象的某个属性
     */
    public static Object getProperty(Object obj, String fieldName) {
        try {
            Field field = deepFindField(obj.getClass(), fieldName);
            if (field != null) {
                field.setAccessible(true);
                return field.get(obj);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * 设置某个对象的某个属性
     */
    public static void setProperty(Object obj, String fieldName, Object value) {
        try {
            Field field = deepFindField(obj.getClass(), fieldName);
            if (field != null) {
                field.setAccessible(true);
                field.set(obj, value);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param source
     * @param target
     */
    public static void copyProperties(Object source, Object target, String... ignoreProperties) {
        org.springframework.beans.BeanUtils.copyProperties(source, target, ignoreProperties);
    }


    public static <T> T transformInJson(Object src, Class<T> clazz) {
        if (src == null) {
            return null;
        }
        String instanceData = JSON.toJSONString(src);
        return JSON.parseObject(instanceData, clazz);
    }

    public static <T> List<T> batchTransformInJson(List<?> src, Class<T> clazz) {
        if (src == null) {
            return null;
        }
        String instanceData = JSON.toJSONString(src);
        return JSON.parseArray(instanceData, clazz);
    }


    /**
     * The copy will ignore <em>BaseEntity</em> field
     *
     * @param source
     * @param target
     */
    public static void copyEntityProperties(Object source, Object target) {
        org.springframework.beans.BeanUtils.copyProperties(source, target);
    }


    private static Map<Class<?>, Supplier<Object>> typeEnum = new HashMap();

    static {
        typeEnum.put(String.class, () -> "");
        typeEnum.put(Byte.class, () -> 0);
        typeEnum.put(Short.class, () -> 0);
        typeEnum.put(Integer.class, () -> 0);
        typeEnum.put(Long.class, () -> 0L);
        typeEnum.put(Float.class, () -> 0.0f);
        typeEnum.put(Double.class, () -> 0.0);
        typeEnum.put(Boolean.class, () -> false);
        typeEnum.put(Date.class, Date::new);
    }

    ;

    /**
     * 将对象中为null的字段改为''
     *
     * @param vo
     * @return
     * @throws Exception
     */
    public static Object replaceIfNull(Object vo) {
        Field[] fields = vo.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            Type type = fields[i].getGenericType();
            if (typeEnum.containsKey(type)) {
                try {
                    Object value = fields[i].get(vo);
                    if (ObjectUtils.isEmpty(value)) {
                        fields[i].set(vo, typeEnum.get(type).get());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return vo;
    }

    public static <T> Collection<T> replaceIfNull(Collection<T> collection) {
        for (Object t : collection) {
            replaceIfNull(t);
        }
        return collection;
    }

    /**
     * Lang 转为 date
     * Enum 转为 Integer  适用于request
     *
     * @param targetClazz
     * @param source
     * @param <T>
     * @return
     */
    @SneakyThrows
    public static <T> T reqTransform(@NotNull final Class<T> targetClazz, Object source, Function<Object, Object> enumFunc) {
        Objects.requireNonNull(source);
        Objects.requireNonNull(targetClazz);
        T target = transform(targetClazz, source);

        BeanWrapper sourceInfo = new BeanWrapperImpl(source);
        BeanWrapper targetInfo = new BeanWrapperImpl(targetClazz);

        PropertyDescriptor[] srcPds = sourceInfo.getPropertyDescriptors();
        PropertyDescriptor[] tarPds = targetInfo.getPropertyDescriptors();

        for (PropertyDescriptor srcPd : srcPds) {
            Method readMethod = srcPd.getReadMethod();
            for (PropertyDescriptor tarPd : tarPds) {
                Method writeMethod = tarPd.getWriteMethod();
                Method tReadMethod = tarPd.getReadMethod();
                if (!ObjectUtils.isEmpty(readMethod.invoke(source)) & srcPd.getName().equals(tarPd.getName()) && tReadMethod.getName().equals(readMethod.getName())) {
                    if (readMethod.getReturnType().equals(Long.class)) {
                        if (tarPd.getPropertyType().getName().equals("java.util.Date")) {
                            Object result = readMethod.invoke(source);
                            writeMethod.invoke(target, DateUtil.timeStamptoDate((Long) result));
                        }
                    }
                    if (readMethod.getReturnType().isEnum()) {
                        Object result = readMethod.invoke(source);
                        writeMethod.invoke(target, enumFunc.apply(result));

                    }
                }
            }
        }
        return target;
    }

    /**
     * 默认枚举类型name为Integer类型
     *
     * @param targetClazz
     * @param source
     * @param <T>
     * @return
     */
    @SneakyThrows
    public static <T> T reqTransform(@NotNull final Class<T> targetClazz, Object source) {
        return reqTransform(targetClazz, source, o -> ((TransferEnum) o).getValue());

    }

    /**
     * 枚举类型name为String==》value
     *
     * @param targetClazz
     * @param source
     * @param <T>
     * @return
     */
    @SneakyThrows
    public static <T> T reqTransformWithEnumName(@NotNull final Class<T> targetClazz, Object source) {
        return reqTransform(targetClazz, source, o -> ((Enum) o).name());
    }

    /**
     * 枚举类型返回序号
     *
     * @param targetClazz
     * @param source
     * @param <T>
     * @return
     */
    @SneakyThrows
    public static <T> T reqTransformToEnumOrdinal(@NotNull final Class<T> targetClazz, Object source) {
        return reqTransform(targetClazz, source, o -> ((Enum) o).ordinal());
    }


    public static <T> List<T> reqBatchTransform(final Class<T> clazz, List<? extends Object> srcList) {
        if (CollectionUtils.isEmpty(srcList)) {
            return Collections.emptyList();
        }

        List<T> result = new ArrayList<>(srcList.size());
        for (Object srcObject : srcList) {
            result.add(reqTransform(clazz, srcObject));
        }
        return result;
    }

    @SneakyThrows
    public static void forEach(Object object, BiConsumer<String, Object> biConsumer) {
        BeanWrapper sourceInfo = new BeanWrapperImpl(object);
        PropertyDescriptor[] srcPds = sourceInfo.getPropertyDescriptors();
        for (PropertyDescriptor srcPd : srcPds) {
            if (srcPd.getReadMethod() != null && !srcPd.getName().equals("class") && !srcPd.getName().equals("dynamicTableName")&& !srcPd.getName().equals("routeTableName")) {
                biConsumer.accept(srcPd.getName(), srcPd.getReadMethod().invoke(object));
            }
        }
    }

    public static void copyPropertiesIfNotNull(Object source, Object target) {
        org.springframework.beans.BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
    }

    public static <T, R> String getBeanFieldName(Fn<T, R> fn) {
        return Reflections.fnToFieldName(fn);
    }



    public static boolean allPropertiesIsNull(Object source) {
        String s = JSON.toJSONString(source);
        JSONObject jsonObject = JSON.parseObject(s);
        return jsonObject.size() == 0;
    }
}
