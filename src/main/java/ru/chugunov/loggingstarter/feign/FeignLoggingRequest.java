package ru.chugunov.loggingstarter.feign;

import feign.Logger;
import feign.Request;
import feign.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import ru.chugunov.loggingstarter.service.LoggingService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class FeignLoggingRequest extends Logger {

    public static final String FEIGN_LOGGING_BODY_PROPERTY_PREFIX = "logging.web-logging.log-feign-body";

    @Autowired
    private LoggingService loggingService;

    @Autowired
    private Environment environment;

    private boolean isFeignBodyLoggingEnabled() {
        return environment.getProperty(FEIGN_LOGGING_BODY_PROPERTY_PREFIX, Boolean.class, false);
    }
    @Override
    protected void logRequest(String configKey, Level logLevel, Request request) {
        loggingService.logFeignRequest(request);

        if (isFeignBodyLoggingEnabled()) {
            loggingService.logFeignRequestBody(request);
        }
    }

    @Override
    protected Response logAndRebufferResponse(String configKey, Level logLevel, Response response, long elapsedTime) throws IOException {
        String responseBody = StreamUtils.copyToString(response.body().asInputStream(), StandardCharsets.UTF_8);

        loggingService.logFeignResponse(response);

        if (isFeignBodyLoggingEnabled()) {
            loggingService.logFeignResponseBody(response, responseBody);
        }

        return response.toBuilder()
                .body(responseBody, StandardCharsets.UTF_8)
                .build();
    }

    @Override
    protected void log(String configKey, String format, Object... args) {
        // Имплементация не требуется
    }
}
