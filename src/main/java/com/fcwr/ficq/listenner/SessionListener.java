package com.fcwr.ficq.listenner;

import com.fcwr.ficq.dao.UserDao;
import com.fcwr.ficq.utils.MySessionContext;
import javax.servlet.ServletContext;
import javax.servlet.http.*;
import java.util.HashSet;

public class SessionListener implements HttpSessionListener, HttpSessionAttributeListener {

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        session.setMaxInactiveInterval(60*60*3);
        ServletContext servletContext = session.getServletContext();
        HashSet<HttpSession> sessions = (HashSet<HttpSession>) servletContext.getAttribute("sessions");
        if (sessions == null) {
            sessions= new HashSet<>();
            servletContext.setAttribute("sessions",sessions);
        }
        sessions.add(session);
        MySessionContext.AddSession(event.getSession());
    }



    @Override
    public void sessionDestroyed(javax.servlet.http.HttpSessionEvent event) {
        HttpSession session = event.getSession();
        ServletContext servletContext = session.getServletContext();
        String user = (String) session.getAttribute("user");
        UserDao.updateExit(user);
        HashSet<?> sessions = (HashSet<?>) servletContext.getAttribute("sessions");
        if (sessions != null && sessions.contains(session)) {
            sessions.remove(session);
            MySessionContext.DelSession(session);
        }
    }
/*
    这段代码实现了一个 HttpSessionListener 的监听器，主要用于在用户登录和退出时记录用户的会话（session），具体说明如下：
    attributeAdded(HttpSessionBindingEvent event) 方法：当向 HttpSession 中添加属性时调用该方法。此处没有实现方法体，所以该方法未做任何操作。
    sessionCreated(HttpSessionEvent event) 方法：当创建 HttpSession 时调用该方法。该方法将当前会话的最大非活动时间设置为 3 小时，并将该会话保存到 ServletContext 中名为 "sessions" 的 HashSet 集合中，并将其添加到自定义的 MySessionContext 类中。
    sessionDestroyed(javax.servlet.http.HttpSessionEvent event) 方法：当销毁 HttpSession 时调用该方法。该方法首先从 HttpSession 中获取用户信息并更新用户状态，然后从 ServletContext 中名为 "sessions" 的 HashSet 集合中移除该会话，并同时从自定义的 MySessionContext 类中删除该会话。
    总之，该代码实现了管理用户会话的功能，可以有效地记录在线用户，控制访问权限等。
*/
}
