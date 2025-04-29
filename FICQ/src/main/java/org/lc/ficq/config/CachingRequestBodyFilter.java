package org.lc.ficq.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;

// 缓存请求的Filter
public class CachingRequestBodyFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,@NonNull HttpServletResponse response,@NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // 如果是 WebSocket 握手请求，直接放行（不缓存）
        if (isWebSocketHandshake(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 普通 HTTP 请求：缓存 Body
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        filterChain.doFilter(wrappedRequest, response);
    }

    private boolean isWebSocketHandshake(HttpServletRequest request) {
        String connection = request.getHeader("Connection");
        String upgrade = request.getHeader("Upgrade");
        return "Upgrade".equalsIgnoreCase(connection) && "websocket".equalsIgnoreCase(upgrade);
    }
}
