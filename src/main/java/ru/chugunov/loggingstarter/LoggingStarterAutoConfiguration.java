package ru.chugunov.loggingstarter;

import feign.Logger;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import ru.chugunov.loggingstarter.aspect.LogExecutionAspect;
import ru.chugunov.loggingstarter.feign.FeignLogger;
import ru.chugunov.loggingstarter.property.LoggingProperty;
import ru.chugunov.loggingstarter.service.LoggingService;
import ru.chugunov.loggingstarter.webfilter.WebLoggingFilter;
import ru.chugunov.loggingstarter.webfilter.WebLoggingRequestBodyAdvice;

@AutoConfiguration
@EnableConfigurationProperties(LoggingProperty.class)
@ConditionalOnProperty(prefix = "logging", value = "enabled", havingValue = "true", matchIfMissing = true)
public class LoggingStarterAutoConfiguration {

    @Bean
    public LoggingService loggingService(){
        return new LoggingService();
    }

    @Bean
    @ConditionalOnProperty(prefix = "logging", value = "log-exec-time", havingValue = "true")
    public LogExecutionAspect logExecutionAspect(){
        return new LogExecutionAspect();
    }

    @Bean
    @ConditionalOnProperty(prefix = "logging.web-logging", value = "enabled", havingValue = "true", matchIfMissing = true)
    public WebLoggingFilter webLoggingFilter(){
        return new WebLoggingFilter();
    }

    @Bean
    @ConditionalOnProperty(prefix = "logging.web-logging", value = { "enabled", "log-body" }, havingValue = "true")
    public WebLoggingRequestBodyAdvice webLoggingRequestBodyAdvice(){
        return new WebLoggingRequestBodyAdvice();
    }

    @Bean
    @ConditionalOnProperty(prefix = "logging.web-logging", value = "log-feign-requests", havingValue = "true")
    public FeignLogger feignLogger(){
        return new FeignLogger();
    }

    @Bean
    @ConditionalOnProperty(prefix = "logging.web-logging", value = "log-feign-requests", havingValue = "true")
    public Logger.Level feignLoggerLevel(){
        return Logger.Level.BASIC;
    }
}
