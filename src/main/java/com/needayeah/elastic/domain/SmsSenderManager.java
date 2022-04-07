package com.needayeah.elastic.domain;

import com.google.common.collect.Maps;
import com.needayeah.elastic.common.utils.SpringApplicationHolder;
import com.needayeah.elastic.interfaces.enums.SmsSenderEnum;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 短信发送平台调用管理器
 *
 * @author lixiaole
 * @date 2021/12/8
 */
public class SmsSenderManager {

    private Map<SmsSenderEnum, SmsSenderInvokeStrategy> smsSenderInvokeStrategyMap;

    private static class LazyHolder {
        private static SmsSenderManager manager = new SmsSenderManager();

        static {
            manager.init();
        }
    }

    private void init() {
        List<? extends SmsSenderInvokeStrategy> invokeStrategies = SpringApplicationHolder.getBeans(SmsSenderInvokeStrategy.class);
        smsSenderInvokeStrategyMap = Maps.newHashMapWithExpectedSize(invokeStrategies.size());
        for (SmsSenderInvokeStrategy invokeStrategy : invokeStrategies) {
            for (SmsSenderEnum senderEnum : invokeStrategy.getSender()) {
                smsSenderInvokeStrategyMap.put(senderEnum, invokeStrategy);
            }
        }
    }

    /**
     * 实例获取
     *
     * @param sender
     * @return
     */
    public static SmsSenderInvokeStrategy getSenderInvoke(SmsSenderEnum sender) {
        SmsSenderInvokeStrategy invokeStrategy = LazyHolder.manager.smsSenderInvokeStrategyMap.get(sender);
        if (Objects.isNull(invokeStrategy)) {
            throw new IllegalArgumentException(MessageFormat
                    .format("This sms sender [{0}] has not instance, please implements SmsSenderInvokeStrategy", sender));
        }
        return invokeStrategy;
    }
}
