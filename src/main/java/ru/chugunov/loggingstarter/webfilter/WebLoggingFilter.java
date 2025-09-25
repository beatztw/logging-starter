package ru.chugunov.loggingstarter.webfilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingResponseWrapper;
import ru.chugunov.loggingstarter.utils.LoggingUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class WebLoggingFilter extends HttpFilter {

    private static final Logger log = LoggerFactory.getLogger(WebLoggingFilter.class);
    @Autowired
    private LoggingUtil loggingUtil;

    @Override
    protected void doFilter(HttpServletRequest request,
                            HttpServletResponse response,
                            FilterChain chain) throws IOException, ServletException {
        String method = request.getMethod();
        String requestURI = request.getRequestURI() + loggingUtil.formatQueryString(request);
        String headers = loggingUtil.inlineRequestHeaders(request);

        log.info("Запрос: {} {} {}", method, requestURI, headers);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        try {
            super.doFilter(request, responseWrapper, chain);

            String responseBody = "body=" + new String(responseWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
            String responseHeaders = loggingUtil.inlineResponseHeaders(responseWrapper);

            log.info("Ответ: {} {} {} {} {}", method, requestURI, responseHeaders, response.getStatus(), responseBody);
        } finally {
            responseWrapper.copyBodyToResponse();
        }
    }
}
