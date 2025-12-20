package ru.chugunov.loggingstarter.service;

import feign.Request;
import feign.Response;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.chugunov.loggingstarter.enums.RequestDirection;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LoggingService {

    private static final Logger log = LoggerFactory.getLogger(LoggingService.class);

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

        log.info("Feign-Запрос: {} {} {} {}", RequestDirection.IN, method, requestURI, headers);
    }

    public void logFeignRequestBody(Request request) {
        String method = request.httpMethod().name();
        String requestURI = request.url();
        String body = new String(request.body(), StandardCharsets.UTF_8);

        log.info("Тело Feign-запроса: {} {} {} {}", RequestDirection.IN, method, requestURI, body);
    }

    public void logResponse(HttpServletRequest request, HttpServletResponse response, String responseBody) {
        String method = request.getMethod();
        String requestURI = request.getRequestURI() + formatQueryString(request);

        log.info("Ответ: {} {} {} {} body={}", RequestDirection.OUT, method, requestURI, response.getStatus(), responseBody);
    }

    public void logFeignResponse(Response response) {
        String url = response.request().url();
        String method = response.request().httpMethod().name();
        int status = response.status();

        log.info("Feign-Ответ: {} {} {} {}", RequestDirection.OUT, method, url, status);
    }

    public void logFeignResponseBody(Response response, String responseBody) {
        String url = response.request().url();
        String method = response.request().httpMethod().name();
        int status = response.status();

        log.info("Тело Feign-Ответ: {} {} {} {} body={}", RequestDirection.OUT, method, url, status, responseBody);

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
