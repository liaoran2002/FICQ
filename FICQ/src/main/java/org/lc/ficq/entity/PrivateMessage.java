package org.lc.ficq.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("private_message")
public class PrivateMessage {

    /**
     * id
     */
    private Long id;

    /**
     * 发送用户id
     */
    private Long sendId;

    /**
     * 接收用户id
     */
    private Long recvId;

    /**
     * 发送内容
     */
    private String content;

    /**
     * 消息类型 MessageType
     */
    private Integer type;

    /**
     * 状态
     */
    private Integer status;


    /**
     * 发送时间
     */
    private Date sendTime;

}
