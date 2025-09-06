package com.zeta.paynow_agent.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE + 20)
public class LoggingFilter extends OncePerRequestFilter {

    private static final Pattern CUSTOMER_ID_PATTERN =
            Pattern.compile("(\"customerId\"\\s*:\\s*)(\\d+)");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        CachedBodyHttpServletRequest cachedBodyHttpServletRequest = new CachedBodyHttpServletRequest(request);
        String body = readBody(cachedBodyHttpServletRequest);
        String redacted = redactCustomerId(body);
        log.info("[Request] {} {}",method,uri);
        log.info("[requestBody] {}",redacted);

        filterChain.doFilter(cachedBodyHttpServletRequest, response);
    }

    private String readBody(HttpServletRequest request) throws IOException {
        try (BufferedReader reader = request.getReader()) {
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }

    private String redactCustomerId(String body) {
        if (body == null || body.isBlank()) return body;

        Matcher matcher = CUSTOMER_ID_PATTERN.matcher(body);
        return matcher.replaceAll(m -> m.group(1) + "\"" + maskCustomerId(m.group(2)) + "\"");
    }


    private String maskCustomerId(String customerId) {
        if (customerId == null || customerId.isBlank()) return "***";

        int len = customerId.length();
        if (len <= 3) return "***";

        return "***" + customerId.substring(len - 3);
    }
}