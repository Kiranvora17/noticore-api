package com.noticore.noticore_api.RequestInterceptor;

import com.noticore.noticore_api.dto.TenantsDto;
import com.noticore.noticore_api.service.ITenantsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class RequestInterceptor implements HandlerInterceptor {

    private final ITenantsService iTenantsService;

    @Value("${rapidapi.proxy.secret}")
    private String rapidApiProxySecret;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws IOException {

        // Step 1 — Verify RapidAPI proxy secret
        String proxySecret = request.getHeader("X-RapidAPI-Proxy-Secret");
        if (proxySecret == null || !proxySecret.equals(rapidApiProxySecret)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Unauthorized\"}");
            return false;
        }

        // Step 2 — Get tenant username
        String username = request.getHeader("X-RapidAPI-User");
        if (username == null || username.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"X-RapidAPI-User header is missing\"}");
            return false;
        }

        try {
            TenantsDto tenant = iTenantsService.getOrCreate(username);
            request.setAttribute("tenant", tenant);
        } catch (Exception e) {
            log.error("Error in request interceptor: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Internal server error\"}");
            return false;
        }

        return true;
    }
}
