package org.lc.ficq.session;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.lc.ficq.model.SessionInfo;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserSession extends SessionInfo {

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 用户昵称
     */
    private String nickName;
}
