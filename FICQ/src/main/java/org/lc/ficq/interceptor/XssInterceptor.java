package org.lc.ficq.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.lc.ficq.enums.ResultCode;
import org.lc.ficq.exception.GlobalException;
import org.lc.ficq.util.XssUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@Component
public class XssInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response,@NonNull Object handler) throws Exception {
        // 1. 检查Query/Form参数（GET/POST表单请求）
        Map<String, String[]> paramMap = request.getParameterMap();
        for (String[] values : paramMap.values()) {
            for (String value : values) {
                if (XssUtil.checkXss(value)) {
                    throw new GlobalException(String.valueOf(ResultCode.XSS_PARAM_ERROR));
                }
            }
        }

        // 2. 检查Body（JSON/XML等）
        if (isJsonRequest(request)) {
            // 使用缓存的方式读取Body
            String body = getCachedBody(request);
            if (XssUtil.checkXss(body)) {
                throw new GlobalException(String.valueOf(ResultCode.XSS_PARAM_ERROR));
            }
        }

        return true;
    }

    private boolean isJsonRequest(HttpServletRequest request) {
        String contentType = request.getContentType();
        return contentType != null && contentType.contains("application/json");
    }

    private String getCachedBody(HttpServletRequest request) {
        if (request instanceof ContentCachingRequestWrapper wrappedRequest) {
            byte[] bodyBytes = wrappedRequest.getContentAsByteArray();
            return new String(bodyBytes, StandardCharsets.UTF_8);
        }
        // 如果未包装，直接读取（不推荐，仅作兼容）
        return getBodyDirectly(request);
    }

    @SneakyThrows
    private String getBodyDirectly(HttpServletRequest request) {
        BufferedReader reader = request.getReader();
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }
}
