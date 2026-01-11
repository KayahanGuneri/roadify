package com.roadify.aiassistant.api.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;




public class LoggingContextFilter extends OncePerRequestFilter {

    private static final String TRACE_ID_KEY = "traceId";
    private static final String USER_ID_KEY = "userId";



    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    )throws ServletException, IOException {
        try {
            String existingTraceId = MDC.get(TRACE_ID_KEY);
            if (existingTraceId == null || existingTraceId.isBlank()){
                MDC.put(TRACE_ID_KEY, UUID.randomUUID().toString());
            }

            String userId = request.getHeader("X-User-Id");
            if (userId != null && !userId.isBlank()){
                MDC.put(USER_ID_KEY, userId);
            }

            filterChain.doFilter(request, response);
        }finally {
            MDC.remove(TRACE_ID_KEY);
            MDC.remove(USER_ID_KEY);
        }
    }
}
