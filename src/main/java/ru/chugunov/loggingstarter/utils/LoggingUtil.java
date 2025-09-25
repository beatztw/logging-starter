package ru.chugunov.loggingstarter.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class LoggingUtil {

    public String inlineRequestHeaders(HttpServletRequest request) {
        Map<String, String> headersMap = Collections.list(request.getHeaderNames()).stream()
                .collect(Collectors.toMap(it -> it, request::getHeader));

        String headers = headersMap.entrySet().stream()
                .map(entry -> {
                    String headerName = entry.getKey();
                    String headerValue = entry.getValue();

                    return headerName + "=" + headerValue;
                })
                .collect(Collectors.joining(","));
        return "headers={" + headers + "}";
    }

    public String inlineResponseHeaders(ContentCachingResponseWrapper response) {
        Map<String, String> headersMap = response.getHeaderNames().stream()
                .collect(Collectors.toMap(it -> it, response::getHeader));

        String headers = headersMap.entrySet().stream()
                .map(entry -> {
                    String headerName = entry.getKey();
                    String headerValue = entry.getValue();

                    return headerName + "=" + headerValue;
                })
                .collect(Collectors.joining(","));
        return "headers={" + headers + "}";
    }

    public String formatQueryString(HttpServletRequest request) {
        return Optional.ofNullable(request.getQueryString())
                .map(qs -> "?" + qs)
                .orElse(Strings.EMPTY);
    }
}
