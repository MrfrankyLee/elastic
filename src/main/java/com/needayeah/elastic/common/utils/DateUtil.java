package com.needayeah.elastic.common.utils;


import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateUtil{

    public final static int MILLISECOND_MAX = 999;

    public final static String SPECIAL_SIMPLE_FORMAT = "yyMMdd";

    public final static String SIMPLE_FORMAT = "yyyyMMdd";
    /**
     * 时间格式
     */
    public final static String TIME_FORMAT = "HH:mm:ss:SS";

    /**
     * 时间格式
     */
    public final static String TIME_SHORT_FORMAT = "HH:mm:ss";

    /**
     * 缺省短日期格式
     */
    public final static String DEFAULT_SHORT_DATE_FORMAT = "yyyy-MM-dd";

    /**
     * 缺省短日期格式
     */
    public final static String DEFAULT_MONTH_DATE_FORMAT = "yyyy-MM";

    /**
     * 缺省中文短日期格式
     */
    public final static String DEFAULT_SHORT_MONTH_FORMAT_ZH = "M月d日";

    /**
     * 缺省长日期格式
     */
    public final static String DEFAULT_LONG_TIME_FORMAT = DEFAULT_SHORT_DATE_FORMAT + " " + TIME_FORMAT;

    public final static String DEFAULT_LONG_DATE_FORMAT_S = "yyyy/MM/dd HH:mm:ss";

    public final static String DEFAULT_LONG_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public final static DateTimeFormatter DTF_DEFAULT_LONG_TIME_FORMAT = DateTimeFormat
            .forPattern(DEFAULT_LONG_TIME_FORMAT);

    public final static String DEFAULT_SHORT_TIME_FORMAT = DEFAULT_SHORT_DATE_FORMAT
            + " "
            + TIME_SHORT_FORMAT;

    public final static DateTimeFormatter DTF_DEFAULT_SHORT_TIME_FORMAT = DateTimeFormat
            .forPattern(DEFAULT_SHORT_TIME_FORMAT);

    /**
     * Java能支持的最小日期字符串（yyyy-MM-dd）类型
     */
    public final static String JAVA_MIN_SHORT_DATE_STR = "1970-01-01";

    /**
     * Java能支持的最小日期字符串（yyyy-MM-dd HH:mm:ss:SS）
     */
    public final static String JAVA_MIN_LONG_DATE_STR = "1970-01-01 00:00:00:00";

    /**
     * 支付传输默认时间格式
     */
    public final static String DEFAULT_PAY_FORMAT = "yyyyMMddHHmmss";

    /**
     * 带分割符时间格式
     */
    public final static String DEFAULT_SPLIT_PAY_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 带毫秒格式
     */
    public final static String DEFAULT_PAY_MILLISECOND_FORMAT = "yyyyMMddHHmmssSSS";

    /**
     * 默认的时间段显示格式
     */
    public final static String DEFAULT_PERIOD_FORMAT = "{0}天{1}小时{2}分钟";


    public static final long BASE_START_DAY = 1420041600000L;
    private static final Logger logger = LoggerFactory.getLogger(DateUtil.class);
    public static long DAY_UNIT_OF_SECONDS = 24 * 60 * 60L;
    public static final long ONE_DAY_MILLIS = DAY_UNIT_OF_SECONDS * 1000;
    public static long HOUR_UNIT_OF_SECONDS = 60 * 60L;
    public static long HOUR_UNIT_OF_FIVE_SECONDS = 60 * 5L;
    public static long MINUTE_UNIT_OF_SECONDS = 60L;

    public final static String SHOP_SHORT_DATE_STR = "2013-01-01 00:00:00";

    public final static String SHOP_SHORT_DATE_END = "2020-05-01 00:00:00";


    public enum TimeUnit {
        /**
         * 枚举
         */
        yy("时间单位－年"),
        MM("时间单位－月"),
        dd("时间单位－日"),
        HH("时间单位－时"),
        mm("时间单位－分"),
        ss("时间单位－秒");

        public String desc;

        TimeUnit(String desc) {
            this.desc = desc;
        }
    }


    /**
     * 日期相加
     *
     * @param tu    时间单位 @see com.rrx.common.util.DateUtil.TimeUnit
     * @param delta 相加的值，不能超过Integer的最大值和最小值
     */
    public static Date add(TimeUnit tu, long delta) {
        if (delta == 0) {
            return new Date();
        }

        int deltaInt = 0;
        if (delta > Integer.MIN_VALUE && delta < Integer.MAX_VALUE) {
            deltaInt = (int) delta;
        }
        if (delta > Integer.MAX_VALUE) {
            deltaInt = Integer.MAX_VALUE;
        }
        if (delta < Integer.MIN_VALUE) {
            deltaInt = Integer.MIN_VALUE;
        }

        DateTime dt = new DateTime();
        switch (tu) {
            case yy:
                dt = dt.plusYears(deltaInt);
                break;
            case MM:
                dt = dt.plusMonths(deltaInt);
                break;
            case dd:
                dt = dt.plusDays(deltaInt);
                break;
            case HH:
                dt = dt.plusHours(deltaInt);
                break;
            case mm:
                dt = dt.plusMinutes(deltaInt);
                break;
            case ss:
                dt = dt.plusSeconds(deltaInt);
                break;
        }

        return dt.toDate();
    }

    public static Date add(Date baseDate, TimeUnit tu, long delta) {
        if (delta == 0) {
            return baseDate;
        }

        int deltaInt = 0;
        if (delta > Integer.MIN_VALUE && delta < Integer.MAX_VALUE) {
            deltaInt = (int) delta;
        }
        if (delta > Integer.MAX_VALUE) {
            deltaInt = Integer.MAX_VALUE;
        }
        if (delta < Integer.MIN_VALUE) {
            deltaInt = Integer.MIN_VALUE;
        }

        DateTime dt = new DateTime(baseDate);
        switch (tu) {
            case yy:
                dt = dt.plusYears(deltaInt);
                break;
            case MM:
                dt = dt.plusMonths(deltaInt);
                break;
            case dd:
                dt = dt.plusDays(deltaInt);
                break;
            case HH:
                dt = dt.plusHours(deltaInt);
                break;
            case mm:
                dt = dt.plusMinutes(deltaInt);
                break;
            case ss:
                dt = dt.plusSeconds(deltaInt);
                break;
        }

        return dt.toDate();
    }

    /**
     * 获取当天起始时间
     * 格林尼治时间比北京时间少8个小时，需要减去8小时的毫秒
     */
    public static long getStartTimeStampToday() {
        return BASE_START_DAY + ((System.currentTimeMillis() - BASE_START_DAY) / ONE_DAY_MILLIS)
                * ONE_DAY_MILLIS;
    }

    public static String getStartTimeToday() {
        return DateUtil.convertDateToStr(BASE_START_DAY + ((System.currentTimeMillis() - BASE_START_DAY) / ONE_DAY_MILLIS)
                * ONE_DAY_MILLIS, DEFAULT_LONG_DATE_FORMAT);
    }

    public static Date getStartDateToday() {
        return new Date(getStartTimeStampToday());
    }

    /**
     * 获取明天的起始时间戳
     */
    public static long getStartTimeStampTomorrow() {
        return BASE_START_DAY
                + ((System.currentTimeMillis() - BASE_START_DAY) / ONE_DAY_MILLIS + 1)
                * ONE_DAY_MILLIS;
    }

    public static long getStartTimeStampYesterday() {
        return BASE_START_DAY
                + ((System.currentTimeMillis() - BASE_START_DAY) / ONE_DAY_MILLIS - 1)
                * ONE_DAY_MILLIS;
    }

    public static String getStartTimeYesterday() {
        return DateUtil.convertDateToStr(BASE_START_DAY
                + ((System.currentTimeMillis() - BASE_START_DAY) / ONE_DAY_MILLIS - 1)
                * ONE_DAY_MILLIS, DEFAULT_LONG_DATE_FORMAT);
    }

    public static long getStartTimeStampBeforeYesterday() {
        return BASE_START_DAY + ((System.currentTimeMillis() - BASE_START_DAY) / ONE_DAY_MILLIS - 2)
                * ONE_DAY_MILLIS;
    }

    public static String getStartTimeWeekDay() {
        return DateUtil.convertDateToStr(BASE_START_DAY + ((System.currentTimeMillis() - BASE_START_DAY) / ONE_DAY_MILLIS - 7)
                * ONE_DAY_MILLIS, DEFAULT_LONG_DATE_FORMAT);
    }

    public static Date getStartDateTomorrow() {
        return new Date(getStartTimeStampTomorrow());
    }

    public static Date getStartDateYesterday() {
        return new Date(getStartTimeStampYesterday());
    }

    public static Date getStartDateBeforeYesterday() {
        return new Date(getStartTimeStampBeforeYesterday());
    }

    /**
     * 获取当天结束时间毫秒
     * 格林尼治时间比北京时间少8个小时，需要减去8小时的毫秒
     */
    public static long getEndTimeStampToday() {
        return BASE_START_DAY
                + ((System.currentTimeMillis() - BASE_START_DAY) / ONE_DAY_MILLIS + 1)
                * ONE_DAY_MILLIS - 1;
    }

    public static Date getEndDateToday() {
        return new Date(getEndTimeStampToday());
    }

    public static Date getTimeFormatDay(String dateString) {

        DateTime dt1 = DateTime.parse(dateString, DateTimeFormat.forPattern(SIMPLE_FORMAT));
        return dt1.toDate();

    }

    public static String getDateStringOfToday() {
        return new DateTime(BASE_START_DAY
                + ((System.currentTimeMillis() - BASE_START_DAY) / ONE_DAY_MILLIS)
                * ONE_DAY_MILLIS).toString(DEFAULT_SHORT_DATE_FORMAT);
    }

    public static String getDateStringOfDay(Date date) {
        return new DateTime(date).toString(DEFAULT_SHORT_DATE_FORMAT);
    }

    /**
     * 获取当天结束时间字符串
     * 格林尼治时间比北京时间少8个小时，需要减去8小时的毫秒
     */
    public static String getEndTimeToday() {
        return new DateTime(getEndTimeStampToday()).toString(DEFAULT_SHORT_TIME_FORMAT);
    }

    /**
     * 获取指定日期结束时间字符串
     * 格林尼治时间比北京时间少8个小时，需要减去8小时的毫秒
     */
    public static String getEndDateStrOfDay(Date date) {
        return new DateTime(BASE_START_DAY
                + ((date.getTime() - BASE_START_DAY) / ONE_DAY_MILLIS + 1)
                * ONE_DAY_MILLIS - 1).toString(DEFAULT_SHORT_TIME_FORMAT);
    }

    /**
     * 获取指定日期结束时间字符串
     * 格林尼治时间比北京时间少8个小时，需要减去8小时的毫秒
     */
    public static Date getEndDateOfDay(Date date) {
        return new DateTime(BASE_START_DAY
                + ((date.getTime() - BASE_START_DAY) / ONE_DAY_MILLIS + 1)
                * ONE_DAY_MILLIS - 1).toDate();
    }

    /**
     * 获取几天后结束时间字符串
     * 格林尼治时间比北京时间少8个小时，需要减去8小时的毫秒
     */
    public static long getEndTimeStampOfPlusDay(int plusDays) {
        return BASE_START_DAY
                + ((System.currentTimeMillis() - BASE_START_DAY) / ONE_DAY_MILLIS + plusDays + 1)
                * ONE_DAY_MILLIS - 1;
    }

    public static String getEndTimeOfPlusDay(int plusDays) {
        return new DateTime(getEndTimeStampOfPlusDay(plusDays)).toString(DEFAULT_SHORT_TIME_FORMAT);
    }

    /**
     * 获取昨天结束时间字符串
     * 格林尼治时间比北京时间少8个小时，需要减去8小时的毫秒
     */
    public static String getTimeEndOfYesterday() {
        return new DateTime(BASE_START_DAY
                + ((System.currentTimeMillis() - BASE_START_DAY) / ONE_DAY_MILLIS)
                * ONE_DAY_MILLIS - 1).toString(DEFAULT_SHORT_DATE_FORMAT);
    }

    public static boolean compareTime(Date time) {
        DateTime dt1 = new DateTime();
        return dt1.isAfter(time.getTime());
    }

    public static boolean compareTime(String time) {
        return compareTime(time, getCurrDateStrWithDefaultLongTimeFormat(),
                DTF_DEFAULT_LONG_TIME_FORMAT);
    }

    public static boolean compareTime(String time1, String time2) {
        return compareTime(time1, time2, DTF_DEFAULT_LONG_TIME_FORMAT);
    }

    public static boolean compareTime(String time1, String time2, String dateFormat) {
        DateTimeFormatter dtf = DateTimeFormat.forPattern(dateFormat);
        return compareTime(time1, time2, dtf);
    }

    public static boolean compareTime(String time1, String time2, DateTimeFormatter dtf) {
        //时间解析
        DateTime dt1 = DateTime.parse(time1, dtf);
        DateTime dt2 = DateTime.parse(time2, dtf);

        return dt1.isAfter(dt2);
    }

    /**
     * 获取给定时间的之前几天的时间
     *
     * @param dateString 给定时间
     * @param days       之前天数
     * @return 字符时间
     */
    public static String getBeforeDayStr(String dateString, int days) {

        DateTime dt1 = DateTime.parse(dateString, DateTimeFormat.forPattern(SIMPLE_FORMAT));
        dt1 = dt1.minusDays(days);
        return dt1.toString(DateTimeFormat.forPattern(SIMPLE_FORMAT));

    }

    /**
     * 获取给定时间的之前几天的时间
     *
     * @param dateString 给定时间
     * @param days       之前天数
     * @return 字符时间
     */
    public static String getBeforeDayStr(String dateString, int days, String dateFormat) {

        DateTime dt1 = DateTime.parse(dateString, DateTimeFormat.forPattern(dateFormat));
        dt1 = dt1.minusDays(days);
        return dt1.toString(DateTimeFormat.forPattern(dateFormat));

    }

    /**
     * 获取给定时间的之前几月的时间
     *
     * @param dateString 给定时间
     * @return 字符时间
     */
    public static String getBeforeMonthStr(String dateString, int months, String dateFormat) {
        DateTime dt1 = DateTime.parse(dateString, DateTimeFormat.forPattern(dateFormat));
        dt1 = dt1.minusMonths(months);
        return dt1.toString(DateTimeFormat.forPattern(dateFormat));

    }

    /**
     * 获取给定时间的之后几天的时间
     *
     * @param dateString 给定时间
     * @param days       之后天数
     * @return 字符时间
     */
    public static String getAfterDayStr(String dateString, int days) {
        DateTime dt1 = DateTime.parse(dateString, DateTimeFormat.forPattern(SIMPLE_FORMAT));
        dt1 = dt1.plusDays(days);
        return dt1.toString(DateTimeFormat.forPattern(SIMPLE_FORMAT));
    }

    public static String getAfterDayStr(String dateString, int days, String format) {
        DateTime dt1 = DateTime.parse(dateString, DateTimeFormat.forPattern(format));
        dt1 = dt1.plusDays(days);
        return dt1.toString(DateTimeFormat.forPattern(format));
    }

    /**
     * 获取给定时间的之后几月的时间
     *
     * @param dateString 给定时间
     * @return 字符时间
     */
    public static String getAfterMonthStr(String dateString, int months, String format) {
        DateTime dt1 = DateTime.parse(dateString, DateTimeFormat.forPattern(format));
        dt1 = dt1.plusMonths(months);
        return dt1.toString(DateTimeFormat.forPattern(format));
    }

    public static String convertDateToStr(Date date) {
        return convertDateToStr(date, DEFAULT_SHORT_DATE_FORMAT);
    }

    public static String convertDateToStr(Date date, String dateFormat) {
        if (date == null || !StringUtils.hasLength(dateFormat)) {
            return "";
        }
        DateTime dt = new DateTime(date);
        return dt.toString(DateTimeFormat.forPattern(dateFormat));
    }

    public static String covertDateToStr(long time) {
        return convertDateToStr(time, DEFAULT_SHORT_TIME_FORMAT);
    }

    public static String convertDateToStr(long time, String dateFormat) {
        return convertDateToStr(new Date(time), dateFormat);
    }

    /**
     * 校验时间格式
     *
     * @param date    日期
     * @param lenient 是否严格检查 true 宽松检查， false 严格检查
     */
    public static boolean defaultDateFormatValidate(String date, boolean lenient) {
        if (date.length() != 14 && date.length() != 13) {
            return false;
        }
        if (!lenient) {
            try {
                DateUtil.convertStrToDate(String.valueOf(date), DEFAULT_PAY_FORMAT);
                return true;
            } catch (Exception e) {
                return false;
            }
        } else {
            //检验是否能转为Long类型，防止抛异常
            for (int i = 0; i < date.length(); i++) {
                if (date.charAt(i) < 48 || date.charAt(i) > 57) {
                    return false;
                }
            }
            Long dateVal = Long.valueOf(date);
            int hook = date.length() == 14 ? 1 : 10;
            int year = (int) (dateVal * hook / (10000000000L));
            int month = (int) ((dateVal * hook / 100000000) % 100);
            int dat = (int) ((dateVal * hook / 1000000) % 100);
            int hour = (int) ((dateVal * hook / 10000) % 100);
            int min = (int) ((dateVal * hook / 100) % 100);
            int sed = (int) ((dateVal) % (100 / hook));
            if (year < 1700 || month > 12 || month == 0 || dat > 31 || dat == 0 || hour > 23
                    || min > 59 || sed > 59) {
                return false;
            }
        }
        return true;
    }

    public static Date convertStrToDate(String time, String dateFormat) {
        if (StringUtils.isEmpty(time)) {
            return null;
        }
        DateTimeFormatter format = DateTimeFormat.forPattern(dateFormat);
        //时间解析
        DateTime dt1 = DateTime.parse(time, format);
        return dt1.toDate();
    }

    public static Date convertStrToDate(String time) {
        if (StringUtils.isEmpty(time)) {
            return null;
        }
        DateTimeFormatter format = DateTimeFormat.forPattern(DEFAULT_SHORT_DATE_FORMAT);
        //时间解析
        DateTime dt1 = DateTime.parse(time, format);
        return dt1.toDate();
    }

    public static Date convertStrToDateWithDefaultDayFormat(String time) {
        return convertStrToDate(time, DEFAULT_PAY_FORMAT);
    }

    private static Timestamp convertStrToTimestamp(String time) {
        if (time == null) {
            return null;
        }

        DateTimeFormatter format = DateTimeFormat.forPattern(DEFAULT_LONG_TIME_FORMAT);
        DateTime dt1 = DateTime.parse(time, format);
        return new Timestamp(dt1.getMillis());
    }

    public static String convertToPeriod(long period) {

        return MessageFormat.format(DEFAULT_PERIOD_FORMAT, (period / DAY_UNIT_OF_SECONDS),
                (period % DAY_UNIT_OF_SECONDS / HOUR_UNIT_OF_SECONDS),
                (period % HOUR_UNIT_OF_SECONDS / MINUTE_UNIT_OF_SECONDS));
    }

    public static double dateDiffDays(Date startDate, Date endDate) {
        return (double) (endDate.getTime() - startDate.getTime()) / (60 * 60 * 24 * 1000);
    }

    public static Date getStartDateByDate(Date date) {
        return new Date(BASE_START_DAY
                + ((date.getTime() - BASE_START_DAY) / ONE_DAY_MILLIS)
                * ONE_DAY_MILLIS);
    }

    // 只比较日期，不考虑时分秒; 返回 endDate - startDate
    public static int compareDay(Date startDate, Date endDate) {
        long day1 = ((startDate.getTime() - BASE_START_DAY) / ONE_DAY_MILLIS);
        long day2 = ((endDate.getTime() - BASE_START_DAY) / ONE_DAY_MILLIS);
        return (int) (day2 - day1);
    }

    // 只比较日期，不考虑时分秒; 返回 endDate - startDate
    public static int compareDay(long startDate, long endDate) {
        return (int) ((endDate - startDate) / DAY_UNIT_OF_SECONDS);
    }


    // 只比较日期月份; 返回相差月份数
    public static int compareMonths(String date1, String date2, String dateFormat) {
        DateTime dt1 = DateTime.parse(date1, DateTimeFormat.forPattern(dateFormat));
        DateTime dt2 = DateTime.parse(date2, DateTimeFormat.forPattern(dateFormat));
        return Months.monthsBetween(dt1, dt2).getMonths();
    }

    public static int compareMonths(Date date1, Date date2) {
        DateTime dt1 = new DateTime(date1);
        DateTime dt2 = new DateTime(date2);
        return Months.monthsBetween(dt1, dt2).getMonths();
    }

    /**
     * 只比较月份 0 两个时间相等 1 startDate > endDate -1 startDate < endDate
     *
     * @param startDate
     * @param endDate
     * @return 0 两个时间相等 1 startDate > endDate -1 startDate < endDate
     */
    public static int compareMonthsNew(Date startDate, Date endDate) {
        DateTime dt1 = new DateTime(startDate);
        DateTime dt2 = new DateTime(endDate);
        int aa = Months.monthsBetween(dt1, dt2).getMonths();
        if (aa > 0) {
            return -1;
        } else if (aa < 0) {
            return 1;
        }
        return 0;
    }

    public static String getCurrDateStrWithDefaultLongTimeFormat() {
        return getCurrDateStr(DEFAULT_LONG_TIME_FORMAT);
    }

    /**
     * 获取当前时间精确到秒级别的默认格式时间
     */
    public static String getCurrDateStrWithDefaultPayFormat() {
        return getCurrDateStr(DEFAULT_PAY_FORMAT);
    }

    /**
     * 获取当前时间精确到毫秒级别的默认格式时间
     */
    public static String getCurrDateStrWithDefaultPayMillisecondFormat() {
        return getCurrDateStr(DEFAULT_PAY_MILLISECOND_FORMAT);
    }

    public static String getCurrDateStr(String dateFormat) {
        return convertDateToStr(new Date(), dateFormat);
    }

    public static Timestamp getCurrTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    public static Date getCurrDate() {
        return new Date();
    }

    public static String toBeginDate(String fieldValue) {
        if (!StringUtils.hasLength(fieldValue)) {
            return "";
        }
        if (fieldValue.length() > 10) {
            return fieldValue;
        }

        fieldValue += " 00:00:00";
        return fieldValue;
    }

    public static String toEndDate(String fieldValue) {
        if (!StringUtils.hasLength(fieldValue)) {
            return "";
        }

        if (fieldValue.length() > 10) {
            return fieldValue;
        }

        fieldValue += " 23:59:59";
        return fieldValue;
    }

    public static String getStandardDatetimeStr(String time) {
        if (!StringUtils.hasLength(time)) {
            return "";
        }

        DateTime dt1 = DateTime.parse(time, DTF_DEFAULT_LONG_TIME_FORMAT);
        return dt1.toString();
    }

    public static String getTimeDiff(String startTime, String endTime) {

        DateTime dt1 = DateTime.parse(startTime, DTF_DEFAULT_LONG_TIME_FORMAT);
        DateTime dt2 = DateTime.parse(endTime, DTF_DEFAULT_LONG_TIME_FORMAT);

        String result = "";

        //默认为毫秒，除以1000是为了转换成秒
        long interval = (dt1.getMillis() - dt2.getMillis()) / 1000; //秒
        long day = interval / (DAY_UNIT_OF_SECONDS); //天
        long hour = interval % (DAY_UNIT_OF_SECONDS) / HOUR_UNIT_OF_SECONDS; //小时
        long minute = interval % HOUR_UNIT_OF_SECONDS / MINUTE_UNIT_OF_SECONDS; //分钟
        long second = interval % MINUTE_UNIT_OF_SECONDS; //秒
        if (day > 0) {
            result = result + day + "天";
        }
        if (hour > 0) {
            result = result + hour + "时";
        }
        if (minute > 0) {
            result = result + minute + "分";
        }
        if (second > 0) {
            result = result + second + "秒";
        }

        return result;

    }

    public static long getSecondDiff(String startTime, String endTime) {
        DateTime dt1 = DateTime.parse(startTime, DTF_DEFAULT_SHORT_TIME_FORMAT);
        DateTime dt2 = DateTime.parse(endTime, DTF_DEFAULT_SHORT_TIME_FORMAT);

        return (dt1.getMillis() - dt2.getMillis()) / 1000l;
    }

    public static Date parse(String str) {
        DateFormat sdf = new SimpleDateFormat(DEFAULT_SHORT_DATE_FORMAT);
        try {
            return sdf.parse(str);
        } catch (ParseException e) {
            logger.error("error:", e);
            return null;
        }
    }

    public static Date parse(String str, String format) {
        DateFormat sdf = new SimpleDateFormat(format);
        try {
            return sdf.parse(str);
        } catch (ParseException e) {
            logger.error("error:", e);
            return null;
        }
    }


    public static String format(Date str) {
        DateFormat sdf = new SimpleDateFormat(DEFAULT_SHORT_DATE_FORMAT);
        try {
            return sdf.format(str);
        } catch (Exception e) {
            logger.error("error:", e);
            return null;
        }
    }


    public static String format(Date str, String format) {
        if (null == str) {
            return "";
        }
        DateFormat sdf = new SimpleDateFormat(format);
        try {
            return sdf.format(str);
        } catch (Exception e) {
            logger.error("error:", e);
            return null;
        }
    }

    public static boolean compareTime(Date time1, Date time2) {
        DateTime dt1 = new DateTime(time1);
        DateTime dt2 = new DateTime(time2);
        return dt1.isAfter(dt2);
    }

    public static Boolean compareTime(long startTime, long endTime) {
        if (endTime - startTime > 0) {
            return true;
        }
        return false;
    }

    /**
     * 获取一个月的第一天
     *
     * @param monthTime 2017-04
     * @return 2017-04-01 00:00:00
     */
    public static String getFirstDayOfMonth(String monthTime) {

        if (!StringUtils.hasLength(monthTime)) {
            return "";
        }
        if (monthTime.length() != 7) {
            return monthTime;
        }

        monthTime += "-01 00:00:00";
        return monthTime;
    }

    public static String getFirstDayOfMonth() {
        LocalDate today = LocalDate.now();
        return String.format("%s-%s-01 00:00:00", today.getYear(), today.getMonth());
    }

    /**
     * 获取一个月的最后一天
     *
     * @param monthTime 2017-04
     * @return 2017-04-30 23:59:59
     */
    public static String getLastDayOfMonth(String monthTime) {

        monthTime = getFirstDayOfMonth(monthTime);
        monthTime = getAfterMonthStr(monthTime, 1, DEFAULT_SPLIT_PAY_FORMAT);
        Date endTime = add(convertStrToDate(monthTime, DEFAULT_SPLIT_PAY_FORMAT), DateUtil.TimeUnit.ss, -1);
        return convertDateToStr(endTime, DEFAULT_SPLIT_PAY_FORMAT);
    }

    public static String getDateStr(String date) {
        logger.info("start date transform and date is {}", date);
        if (date == null) {
            return null;
        }
        String[] startArr = date.split("-");
        if (startArr.length == 1) {
            return date;
        }
        logger.info("end date transform and date is {}", date);
        return startArr[0] + "年" + startArr[1] + "月" + startArr[2] + "日";
    }

    public static String transformTimeStamp(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_SHORT_DATE_FORMAT);
        if (!StringUtils.hasLength(date)) {
            return null;
        }
        try {
            return String.valueOf(sdf.parse(date).getTime());
        } catch (ParseException e) {
            logger.error("error:", e);
            return null;
        }
    }

    /**
     * long 型 转 yyyy-MM-dd HH:mm:ss
     * 1572580461630L 13位时间戳
     *
     * @param time
     * @return
     */
    public static Date timeStamptoDate(long time) {
        String result1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(time));
        Date date1 = parseLong(result1);   //对应的就是时间戳对应的Date
        return date1;

    }

    /**
     * long 型 转 yyyy-MM-dd HH:mm:ss
     * 1572580461630L 13位时间戳
     *
     * @param time
     * @return
     */
    public static Date timeStamptoShortDate(long time) {
        String result1 = new SimpleDateFormat(DEFAULT_SHORT_DATE_FORMAT).format(new Date(time));
        Date date1 = parse(result1);   //对应的就是时间戳对应的Date
        return date1;

    }

    /**
     * 将2017-03-09 00:00:00格式的日期转换为17-3-9 00:00:00
     *
     * @param date 传入的日期
     * @return 返回的日期
     */
    public static String conversionFormat(String date) {

        String newTime = date.substring(2);
        int index = newTime.indexOf("-");

        if (org.apache.commons.codec.binary.StringUtils.equals("0", newTime.substring(index + 1, index + 2))) {
            newTime = newTime.substring(0, index + 1) + newTime.substring(index + 2);
        }

        int lastIndex = newTime.lastIndexOf("-");
        if (org.apache.commons.codec.binary.StringUtils.equals("0", newTime.substring(lastIndex + 1, lastIndex + 2))) {
            newTime = newTime.substring(0, lastIndex + 1) + newTime.substring(lastIndex + 2);
        }

        return newTime;
    }

    public static long getCurrentSecond() {
        return LocalDateTime.now().getSecond();
    }

    public static long getCurrentMills() {
        return System.currentTimeMillis();
    }

    /**
     * Date转localDate
     *
     * @param date
     * @return String
     */
    public static String getStringTime(Date date) {
        if (date == null) {
            return null;
        }
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDate localDate = instant.atZone(zoneId).toLocalDate();
        return localDate.toString();
    }

    /**
     * Date转带毫秒的string
     *
     * @param date
     * @return String
     */
    public static String getStringTimes(Date date) {
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDate = LocalDateTime.ofInstant(instant, zoneId);
        String time = localDate.toString().replace("T", " ");
        if (time.length() > 19) {
            time = time.substring(0, 19);
        } else if (time.length() < 19) {
            time = time + ":00";
        }
        return time;
    }

    /**
     * string转date
     *
     * @param time
     * @return
     */
    public static Date getData(String time) {
        java.time.format.DateTimeFormatter df = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime ldt = LocalDateTime.parse(time, df);
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = ldt.atZone(zoneId);
        Date date = Date.from(zdt.toInstant());
        return date;
    }

    /**
     * @param date
     * @return
     * @author yangnan
     * 生成yyyyMMddHHmmss格式的日期字符串
     */
    public static String withOutSpace(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(date);
    }

    /**
     * @param date
     * @return
     * @author yangnan
     * 生成yyyyMMdd格式的日期字符串
     */
    public static String dateWithOutSpace(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(date);
    }

    /**
     * @param date
     * @return
     * @author yangnan
     * 生成yyyy年MM月dd日格式的日期字符串
     */
    public static String dateWithOutSpace2(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        return sdf.format(date);
    }

    public static int getAgeByBirth(Date birthDay) {
        int age;
        Calendar cal = Calendar.getInstance();
        if (cal.before(birthDay)) { //出生日期晚于当前时间，无法计算
            throw new IllegalArgumentException(
                    "The birthDay is before Now.It's unbelievable!");
        }
        int yearNow = cal.get(Calendar.YEAR);  //当前年份
        int monthNow = cal.get(Calendar.MONTH);  //当前月份
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH); //当前日期
        cal.setTime(birthDay);
        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH);
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);
        age = yearNow - yearBirth;   //计算整岁数
        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                if (dayOfMonthNow < dayOfMonthBirth) {
                    age--;//当前日期在生日之前，年龄减一
                }
            } else {
                age--;//当前月份在生日之前，年龄减一
            }
        }
        return age;
    }

    // 获得某天最小时间 2017-10-15 00:00:00
    public static Date getStartOfDay(Date date) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneId.systemDefault());
        LocalDateTime startOfDay = localDateTime.with(LocalTime.MIN);
        return Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());

    }

    public static Date parseLong(String str) {
        DateFormat sdf = new SimpleDateFormat(DEFAULT_LONG_DATE_FORMAT);
        try {
            return sdf.parse(str);
        } catch (ParseException e) {
            logger.error("error:", e);
            return null;
        }
    }


    public static String formatDate(Date date) {
        DateFormat sdf = new SimpleDateFormat(DEFAULT_LONG_DATE_FORMAT);
        return sdf.format(date);
    }

    /**
     * 返回两个时间之间的所有时间
     *
     * @param begin
     * @param end
     * @return
     */
    public static List<String> getBetweenDate(String begin, String end) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        List<String> betweenList = new ArrayList<String>();
        try {
            Calendar startDay = Calendar.getInstance();
            startDay.setTime(format.parse(begin));
            startDay.add(Calendar.DATE, -1);

            while (true) {
                startDay.add(Calendar.DATE, 1);
                Date newDate = startDay.getTime();
                String newEnd = format.format(newDate);
                betweenList.add(newEnd);
                if (end.equals(newEnd)) {
                    break;
                }
            }
        } catch (Exception e) {
            logger.error("error:", e);
        }

        return betweenList;
    }

    public static String getDate(String dates, String day) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DEFAULT_SHORT_DATE_FORMAT); // 日期格式
        Date date = null;
        Date newDate = null;
        try {
            date = dateFormat.parse(dates); // 指定日期
            newDate = addDate(date, Integer.valueOf(day)); // 指定日期天数
        } catch (ParseException e) {
            logger.error("error:", e);
        }
        return dateFormat.format(newDate);
    }

    public static Date addDate(Date date, long day) throws ParseException {
        long time = date.getTime(); // 得到指定日期的毫秒数
        day = day * 24 * 60 * 60 * 1000; // 要加上的天数转换成毫秒数
        time += day; // 相加得到新的毫秒数
        // time -= day; // 相减得到新的毫秒数
        return new Date(time); // 将毫秒数转换成日期
    }

    /*
     * 将时间戳转换为时间
     */
    public static String stampToDate(String s) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long lt = new Long(s);
        Date date = new Date(lt);
        return simpleDateFormat.format(date);
    }

    public static Integer DateToTimestamp(Date time) {
        Timestamp ts = new Timestamp(time.getTime());

        return (int) ((ts.getTime()) / 1000);
    }

    /**
     * 将8:00:00格式的时间转换成当前天的时间戳
     */
    public static long convertTimeToStamp(String time) {
        StringBuilder builder = new StringBuilder();
        builder.append(DateUtil.format(new Date()));
        builder.append(" ");
        builder.append(time);
        if (time.length() == 5) {
            builder.append(":").append("00");
        }

        return DateUtil.parseLong(builder.toString()).getTime();
    }

    /**
     * Date 转long
     *
     * @param date
     * @return
     */
    public static Long convertDateToLong(Date date) {
        if (ObjectUtils.isEmpty(date)) {
            return null;
        }
        return date.getTime();
    }

    public static String getEndTime(String dateStr) {
        final Date date = convertStrToDate(dateStr, DEFAULT_SHORT_DATE_FORMAT);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneId.systemDefault());
        LocalDateTime endOfDay = localDateTime.with(LocalTime.MAX);
        Date d = Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());
        return convertDateToStr(d, DEFAULT_LONG_DATE_FORMAT);
    }

    public static long getTodayEndTime() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tomorrow = now.plusDays(1).toLocalDate().atStartOfDay();
        long dateCurt = Duration.between(now, tomorrow).getSeconds();
        return dateCurt;
    }

    public static void main(String args[]) {

        String loanStartTime = DateUtil.stampToDate("1584201599187");
        System.out.println(loanStartTime);


        /**
         * 1554220800000
         * 2019-04-03
         * 1552579200000
         * 2019-03-15
         */

//        System.out.println(getCurrHourTime(new Date()));

     /*   String loanStartTimeLong = "1552579200000";
        String loanEndTimeLong = "1554220800000";

        String loanEndTime = DateUtil.stampToDate(loanEndTimeLong);
        System.out.println(loanEndTime);
        List<String> everyDay = DateUtil.getBetweenDate(loanStartTime, loanEndTime);
        System.out.println(everyDay.size());
*/
//        System.out.println(System.currentTimeMillis());
//
//        String loanStartTime = DateUtil.stampToDate("1549111867603");
//        System.out.println(loanStartTime);
//
//        System.out.println(DateUtil.getEndDateOfDay(new Date()));
//
//
//        Calendar applyTime = Calendar.getInstance();
//        applyTime.add(Calendar.DAY_OF_YEAR, -7);
//        System.out.println(applyTime.getTime());
        System.out.println(DateUtil.parse("2020/06/10 12:25:12", DEFAULT_LONG_DATE_FORMAT));
    }


}
