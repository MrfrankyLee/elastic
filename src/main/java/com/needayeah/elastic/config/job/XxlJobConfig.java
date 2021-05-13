package com.needayeah.elastic.config.job;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * xxl-job config
 *
 * @author xuxueli 2017-04-28
 */
@Slf4j
@Configuration
@Conditional(value = XxlJobConfig.EnableJobCondition.class)
public class XxlJobConfig {

    @Value("${xxl.job.admin.addresses}")
    private String adminAddresses;

    @Value("${xxl.job.accessToken}")
    private String accessToken;

    @Value("${xxl.job.executor.appname}")
    private String appname;

    @Value("${xxl.job.executor.address}")
    private String address;

    @Value("${xxl.job.executor.ip}")
    private String ip;

    @Value("${xxl.job.executor.port}")
    private int port;

    @Value("${xxl.job.executor.logpath}")
    private String logPath;

    @Value("${xxl.job.executor.logretentiondays}")
    private int logRetentionDays;


    @Bean
    public XxlJobSpringExecutor xxlJobExecutor() {
        log.info(">>>>>>>>>>> xxl-job config init.");
        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        xxlJobSpringExecutor.setAdminAddresses(adminAddresses);
        xxlJobSpringExecutor.setAppname(appname);
        xxlJobSpringExecutor.setAddress(address);
        xxlJobSpringExecutor.setIp(ip);
        xxlJobSpringExecutor.setPort(port);
        xxlJobSpringExecutor.setAccessToken(accessToken);
        xxlJobSpringExecutor.setLogPath(logPath);
        xxlJobSpringExecutor.setLogRetentionDays(logRetentionDays);

        return xxlJobSpringExecutor;
    }

    /**
     * 针对多网卡、容器内部署等情况，可借助 "spring-cloud-commons" 提供的 "InetUtils" 组件灵活定制注册IP；
     * <p>
     * 1、引入依赖：
     * <dependency>
     * <groupId>org.springframework.cloud</groupId>
     * <artifactId>spring-cloud-commons</artifactId>
     * <version>${version}</version>
     * </dependency>
     * <p>
     * 2、配置文件，或者容器启动变量
     * spring.cloud.inetutils.preferred-networks: 'xxx.xxx.xxx.'
     * <p>
     * 3、获取IP
     * String ip_ = inetUtils.findFirstNonLoopbackHostInfo().getIpAddress();
     */

    public static class EnableJobCondition implements Condition {

        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            // 后续更改为从配置中心获取xxl-job 开关
            //String enable = ConfigService.getAppConfig().getProperty("xxl.job.admin.addresses", "");
            String enable = Strings.EMPTY;
            return Strings.isEmpty(enable);
        }
    }
}