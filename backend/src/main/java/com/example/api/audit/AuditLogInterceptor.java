package com.example.api.audit;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuditLogInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(AuditLogInterceptor.class);
    private final AuditLogService auditLogService;

    public AuditLogInterceptor(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String user = authentication != null ? authentication.getName() : "anonymous";
        log.info("{} {} by {} -> {}", request.getMethod(), request.getRequestURI(), user, response.getStatus());
        auditLogService.addEvent(request.getMethod(), request.getRequestURI(), user, response.getStatus());
    }
}
