package org.lc.ficq.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@TableName("user")
@AllArgsConstructor
@NoArgsConstructor
public class User {
    /**
     * id
     */
    @TableId
    private Long id;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 用户头像
     */
    private String headImage;

    /**
     * 用户头像缩略图
     */
    private String headImageThumb;

    /**
     * 密码(明文)
     */
    private String password;

    /**
     * 性别 0:男 1::女
     */
    private Integer sex;

    /**
     * 个性签名
     */
    private String signature;

    /**
     * 账号是否被封禁
     */
    private Boolean isBanned;

    /**
     * 账号被封禁原因
     */
    private String reason;

    /**
     * 最后登录时间
     */
    private Date lastLoginTime;

    /**
     * 创建时间(注册时间)
     */
    private Date createdTime;

    /**
     *  账号类型 1:普通用户 0:管理员
     */
    private Integer type;

}
