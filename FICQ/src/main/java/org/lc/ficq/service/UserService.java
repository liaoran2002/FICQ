package org.lc.ficq.service;

import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.validation.Valid;
import org.lc.ficq.dto.LoginDTO;
import org.lc.ficq.dto.ModifyPwdDTO;
import org.lc.ficq.dto.PageQueryDTO;
import org.lc.ficq.dto.RegisterDTO;
import org.lc.ficq.entity.User;
import org.lc.ficq.vo.ListResultVO;
import org.lc.ficq.vo.LoginVO;
import org.lc.ficq.vo.OnlineTerminalVO;
import org.lc.ficq.vo.UserVO;


import java.util.List;

public interface UserService extends IService<User> {

    /**
     * 用户登录
     *
     * @param dto 登录dto
     * @return 登录token
     */
    LoginVO login(LoginDTO dto);

    /**
     * 修改用户密码
     *
     * @param dto 修改密码dto
     */
    void modifyPassword(ModifyPwdDTO dto);

    /**
     * 用refreshToken换取新 token
     *
     * @param refreshToken 刷新token
     * @return 登录token
     */
    LoginVO refreshToken(String refreshToken);

    /**
     * 用户注册
     *
     * @param dto 注册dto
     */
    void register(RegisterDTO dto);

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    User findUserByUserName(String username);

    /**
     * 更新用户信息，好友昵称和群聊昵称等冗余信息也会更新
     *
     * @param vo 用户信息vo
     */
    void update(UserVO vo);

    /**
     * 根据用户昵id查询用户以及在线状态
     *
     * @param id 用户id
     * @return 用户信息
     */
    UserVO findUserById(Long id);

    /**
     * 根据用户昵称查询用户，最多返回20条数据
     *
     * @param name 用户名或昵称
     * @return 用户列表
     */
    List<UserVO> findUserByName(String name);

    List<UserVO> randUsers(int count);

    /**
     * 获取用户在线的终端类型
     *
     * @param userIds 用户id，多个用‘,’分割
     * @return 在线用户终端
     */
    List<OnlineTerminalVO> getOnlineTerminals(String userIds);


    ListResultVO<UserVO> findUserList(PageQueryDTO dto);

    ListResultVO<UserVO> findUser(PageQueryDTO dto);
}
