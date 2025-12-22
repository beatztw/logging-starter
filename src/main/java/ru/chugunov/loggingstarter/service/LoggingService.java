package ru.chugunov.loggingstarter.service;

import feign.Request;
import feign.Response;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.chugunov.loggingstarter.enums.RequestDirection;
import ru.chugunov.loggingstarter.property.LoggingProperty;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LoggingService {

    private static final Logger log = LoggerFactory.getLogger(LoggingService.class);

    @Autowired
    private LoggingProperty property;

    public void logRequest(HttpServletRequest request) {
        String method = request.getMethod();
        String requestURI = request.getRequestURI() + formatQueryString(request);
        String headers = inlineHeaders(request);

        log.info("Запрос: {} {} {} {}", RequestDirection.IN, method, requestURI, headers);
    }

    public void logRequestBody(HttpServletRequest request, Object body) {
        String method = request.getMethod();
        String requestURI = request.getRequestURI() + formatQueryString(request);

        log.info("Тело запроса: {} {} {} {}", RequestDirection.IN, method, requestURI, body);
    }

    public void logFeignRequest(Request request) {
        String method = request.httpMethod().name();
        String requestURI = request.url();
        String headers = formatHeaders(request.headers());

        String body = "";
        if (property.getLogFeignBody()) {
            body = new String(request.body(), StandardCharsets.UTF_8);
        }

        log.info("Запрос: {} {} {} {} body={}", RequestDirection.OUT, method, requestURI, headers, body);
    }

    public void logResponse(HttpServletRequest request, HttpServletResponse response, String responseBody) {
        String method = request.getMethod();
        String requestURI = request.getRequestURI() + formatQueryString(request);
        String headers = inlineHeaders(request);

        log.info("Ответ: {} {} {} {} {} body={}", RequestDirection.IN, method, requestURI,
                response.getStatus(), headers ,responseBody);
    }

    public void logFeignResponse(Response response, String responseBody) {
        String url = response.request().url();
        String method = response.request().httpMethod().name();
        String headers = inlineHeaders(response);
        int status = response.status();

        if (property.getLogFeignBody()) {
            responseBody = "";
        }

        log.info("Ответ: {} {} {} {} {} body={}", RequestDirection.OUT, method, url, status, headers, responseBody);
    }

    private String inlineHeaders(Response response) {
        Map<String, Collection<String>> headersMap = response.headers();

        return formatHeaders(headersMap);
    }

    private String inlineHeaders(HttpServletRequest request) {
        Map<String, Collection<String>> headersMap = Collections.list(request.getHeaderNames()).stream()
                .collect(Collectors.toMap(
                        it -> it,
                        headerName -> Collections.list(request.getHeaders(headerName))
                ));

        return formatHeaders(headersMap);
    }

    private String formatQueryString(HttpServletRequest request) {
        return Optional.ofNullable(request.getQueryString())
                .map(qs -> "?" + qs)
                .orElse(Strings.EMPTY);
    }

    private String formatHeaders(Map<String, Collection<String>> headersMap) {
        String headers = headersMap.entrySet().stream()
                .map(entry -> {
                    String headerName = entry.getKey();
                    String headerValue = String.join(",", entry.getValue());

                    return headerName + "=" + headerValue;
                })
                .collect(Collectors.joining(","));
        return "headers={" + headers + "}";
    }
}
