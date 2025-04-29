package org.lc.ficq.session;

import jakarta.servlet.http.HttpServletRequest;
import org.lc.ficq.exception.GlobalException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/*
 * @Description
 * @Author Blue
 * @Date 2022/10/21
 */
public class SessionContext {

    public static UserSession getSession() {
        // 从请求上下文里获取Request对象
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        HttpServletRequest request;
        if (requestAttributes != null) {
            request = requestAttributes.getRequest();
            return (UserSession) request.getAttribute("session");
        }else
            throw new GlobalException("requestAttributes is null");
    }

}
