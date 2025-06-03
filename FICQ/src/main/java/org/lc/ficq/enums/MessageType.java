package org.lc.ficq.enums;

import lombok.AllArgsConstructor;


/**
 * 0-9: 真正的消息，需要存储到数据库
 * 10-19: 状态类消息: 撤回、已读、回执
 * 20-29: 提示类消息: 在会话中间显示的提示
 * 30-39: UI交互类消息: 显示加载状态等
 * 40-49: 操作交互类消息: 语音通话、视频通话消息等
 * 50-60: 后台操作类消息: 用户封禁、群组封禁等
 * 80-89: 好友变化消息
 * 90-99: 群聊变化消息
 *
 */
@AllArgsConstructor
public enum MessageType {

    TEXT(0, "文字消息"),
    IMAGE(1, "图片消息"),
    FILE(2, "文件消息"),
    AUDIO(3, "语音消息"),
    VIDEO(4, "视频消息"),
    SENTEXT(5,"敏感消息"),
    RECALL(10, "撤回"),
    READED(11, "已读"),
    RECEIPT(12, "消息已读回执"),
    ADMINRC(13,"管理员撤回"),
    TIP_TIME(20,"时间提示"),
    TIP_TEXT(21,"文字提示"),
    LOADING(30,"加载中标记"),
    ACT_RT_VOICE(40,"语音通话"),
    ACT_RT_VIDEO(41,"视频通话"),
    USER_BANNED(50,"用户封禁"),
    GROUP_BANNED(51,"群聊封禁"),
    GROUP_UNBAN(52,"群聊解封"),
    FRIEND_NEW(80, "新增好友"),
    FRIEND_DEL(81, "删除好友"),
    GROUP_NEW(90, "新增群聊"),
    GROUP_DEL(91, "删除群聊"),
    ;

    private final Integer code;

    private final String desc;


    public Integer code() {
        return this.code;
    }
}
