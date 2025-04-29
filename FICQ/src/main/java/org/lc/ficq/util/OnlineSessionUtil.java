package org.lc.ficq.util;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket会话管理工具类
 */
@Slf4j
public class OnlineSessionUtil {

    /**
     * 存储用户会话信息
     * key: userId
     * value: Map<terminal, context>
     */
    private static final Map<Long, Map<Integer, ChannelHandlerContext>> USER_SESSIONS = new ConcurrentHashMap<>();

    /**
     * 添加用户会话
     */
    public static void addSession(Long userId, Integer terminal, ChannelHandlerContext ctx) {
        USER_SESSIONS.computeIfAbsent(userId, k -> new ConcurrentHashMap<>())
                .put(terminal, ctx);
        log.info("用户[{}]终端[{}]上线", userId, terminal);
    }

    /**
     * 移除用户会话
     */
    public static void removeSession(Long userId, Integer terminal) {
        if (userId != null && terminal != null) {
            Map<Integer, ChannelHandlerContext> userSessions = USER_SESSIONS.get(userId);
            if (userSessions != null) {
                userSessions.remove(terminal);
                if (userSessions.isEmpty()) {
                    USER_SESSIONS.remove(userId);
                }
                log.info("用户[{}]终端[{}]下线", userId, terminal);
            }
        }
    }

    /**
     * 获取用户会话
     */
    public static ChannelHandlerContext getSession(Long userId, Integer terminal) {
        if (userId == null || terminal == null) {
            return null;
        }
        Map<Integer, ChannelHandlerContext> userSessions = USER_SESSIONS.get(userId);
        return userSessions != null ? userSessions.get(terminal) : null;
    }

    /**
     * 获取用户所有会话
     */
    public static Map<Integer, ChannelHandlerContext> getUserSessions(Long userId) {
        return USER_SESSIONS.getOrDefault(userId, Map.of());
    }

    /**
     * 检查用户是否在线
     */
    public static boolean isUserOnline(Long userId) {
        return userId != null && USER_SESSIONS.containsKey(userId);
    }

    /**
     * 检查用户特定终端是否在线
     */
    public static boolean isTerminalOnline(Long userId, Integer terminal) {
        if (userId == null || terminal == null) {
            return false;
        }
        Map<Integer, ChannelHandlerContext> userSessions = USER_SESSIONS.get(userId);
        return userSessions != null && userSessions.containsKey(terminal);
    }

    /**
     * 获取用户在线终端列表
     */
    public static Map<Integer, ChannelHandlerContext> getOnlineTerminals(Long userId) {
        return USER_SESSIONS.getOrDefault(userId, Map.of());
    }

    /**
     * 关闭用户所有会话
     */
    public static void closeAllSessions(Long userId) {
        Map<Integer, ChannelHandlerContext> userSessions = USER_SESSIONS.remove(userId);
        if (userSessions != null) {
            userSessions.values().forEach(ctx -> {
                try {
                    if (ctx.channel().isActive()) {
                        ctx.close();
                    }
                } catch (Exception e) {
                    log.error("关闭会话失败", e);
                }
            });
            log.info("用户[{}]所有会话已关闭", userId);
        }
    }
} 