package org.lc.ficq.interceptor;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.lc.ficq.config.props.JwtProperties;
import org.lc.ficq.enums.ResultCode;
import org.lc.ficq.exception.GlobalException;
import org.lc.ficq.session.UserSession;
import org.lc.ficq.util.JwtUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
@AllArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtProperties jwtProperties;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        //如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        //从 http 请求头中取出 token
        String token = request.getHeader("accessToken");
        if (StrUtil.isEmpty(token)) {
            log.error("未登录，url:{}", request.getRequestURI());
            throw new GlobalException(String.valueOf(ResultCode.NO_LOGIN));
        }
        String strJson = JwtUtil.getInfo(token);
        UserSession userSession = JSON.parseObject(strJson, UserSession.class);
        // 验证 token
        if (!JwtUtil.checkSign(token, jwtProperties.getAccessTokenSecret())) {
            if (userSession != null) {
                log.error("token已失效，用户:{}", userSession.getUserName());
            }
            log.error("token:{}", token);
            throw new GlobalException(ResultCode.INVALID_TOKEN);
        }
        // 存放session
        request.setAttribute("session", userSession);
        return true;
    }
}
