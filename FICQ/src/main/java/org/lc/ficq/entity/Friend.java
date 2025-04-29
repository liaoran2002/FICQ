package org.lc.ficq.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@TableName("friend")
@NoArgsConstructor
@AllArgsConstructor
public class Friend{


    /**
     * id
     */
    @TableId
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 好友id
     */
    private Long friendId;

    /**
     * 用户昵称
     */
    private String friendNickName;

    /**
     * 用户头像
     */
    private String friendHeadImage;

    /**
     * 是否已删除
     */
    private Boolean deleted;

    /**
     * 创建时间
     */
    private Date createdTime;

}