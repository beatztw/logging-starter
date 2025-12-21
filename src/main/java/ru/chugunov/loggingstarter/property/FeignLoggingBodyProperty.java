package ru.chugunov.loggingstarter.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "logging.web-logging")
public class FeignLoggingBodyProperty {

    private final boolean logFeignBody;

    public FeignLoggingBodyProperty(boolean logFeignBody) {
        this.logFeignBody = logFeignBody;
    }

    public boolean getLogFeignBody() {
        return logFeignBody;
    }
}
