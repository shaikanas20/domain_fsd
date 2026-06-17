package com.cinereserve.notification.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GatewayHeaderFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String userIdStr = httpRequest.getHeader("X-User-Id");
        String email = httpRequest.getHeader("X-User-Email");
        String role = httpRequest.getHeader("X-User-Role");

        if (userIdStr != null && !userIdStr.isEmpty()) {
            UserContext.set(Long.valueOf(userIdStr), email, role);
        }
        
        try {
            chain.doFilter(request, response);
        } finally {
            UserContext.clear();
        }
    }
}
