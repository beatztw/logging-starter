package ru.chugunov.loggingstarter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import ru.chugunov.loggingstarter.aspect.LogExecutionAspect;
import ru.chugunov.loggingstarter.utils.LoggingUtil;
import ru.chugunov.loggingstarter.webfilter.WebLoggingFilter;
import ru.chugunov.loggingstarter.webfilter.WebLoggingRequestBodyAdvice;

@AutoConfiguration
@ConditionalOnProperty(prefix = "logging", value = "enabled", havingValue = "true", matchIfMissing = true)
public class LoggingStarterAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(LoggingStarterAutoConfiguration.class);

    @Bean
    public LoggingUtil loggingUtil(){
        return new LoggingUtil();
    }

    @Bean
    @ConditionalOnProperty(prefix = "logging", value = "log-exec-time", havingValue = "true")
    public LogExecutionAspect logExecutionAspect(){
        log.info("Загрузка logExecutionAspect...");
        return new LogExecutionAspect();
    }

    @Bean
    @ConditionalOnProperty(prefix = "logging.web-logging", value = "enabled", havingValue = "true", matchIfMissing = true)
    public WebLoggingFilter webLoggingFilter(){
        log.info("Загрузка webLoggingFilter...");
        return new WebLoggingFilter();
    }

    @Bean
    @ConditionalOnProperty(prefix = "logging.web-logging", value = { "enabled", "log-body" }, havingValue = "true")
    public WebLoggingRequestBodyAdvice webLoggingRequestBodyAdvice(){
        log.info("Загрузка webLoggingRequestBodyAdvice...");
        return new WebLoggingRequestBodyAdvice();
    }
}
