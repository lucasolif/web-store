package br.edu.utfpr.apiproduto.shared.exception.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationIdFilter extends OncePerRequestFilter {

    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    private static final int MAX_CORRELATION_ID_LENGTH = 100;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String correlationId = resolveCorrelationId(request);

        request.setAttribute(RequestAttributes.CORRELATION_ID, correlationId);
        response.setHeader(CORRELATION_ID_HEADER, correlationId);
        MDC.put("correlationId", correlationId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove("correlationId");
        }
    }

    private String resolveCorrelationId(HttpServletRequest request) {
        String providedCorrelationId = request.getHeader(CORRELATION_ID_HEADER);

        if (providedCorrelationId == null || providedCorrelationId.isBlank() || providedCorrelationId.length() > MAX_CORRELATION_ID_LENGTH) {
            return UUID.randomUUID().toString();
        }

        return providedCorrelationId.trim();
    }
}
