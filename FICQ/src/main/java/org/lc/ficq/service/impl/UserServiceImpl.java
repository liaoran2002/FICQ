package org.lc.ficq.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lc.ficq.config.props.JwtProperties;
import org.lc.ficq.dto.LoginDTO;
import org.lc.ficq.dto.ModifyPwdDTO;
import org.lc.ficq.dto.PageQueryDTO;
import org.lc.ficq.dto.RegisterDTO;
import org.lc.ficq.entity.Friend;
import org.lc.ficq.entity.GroupMember;
import org.lc.ficq.entity.User;
import org.lc.ficq.enums.ResultCode;
import org.lc.ficq.enums.TerminalType;
import org.lc.ficq.exception.GlobalException;
import org.lc.ficq.mapper.UserMapper;
import org.lc.ficq.netty.WebSocketMessageService;
import org.lc.ficq.service.FriendService;
import org.lc.ficq.service.GroupMemberService;
import org.lc.ficq.service.UserService;
import org.lc.ficq.session.SessionContext;
import org.lc.ficq.session.UserSession;
import org.lc.ficq.util.BeanUtils;
import org.lc.ficq.util.JwtUtil;
import org.lc.ficq.vo.ListResultVO;
import org.lc.ficq.vo.LoginVO;
import org.lc.ficq.vo.OnlineTerminalVO;
import org.lc.ficq.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private PasswordEncoder passwordEncoder;
    @Autowired
    private void setPasswordEncoder(PasswordEncoder passwordEncoder){
        this.passwordEncoder=passwordEncoder;
    }
    private final GroupMemberService groupMemberService;
    private final FriendService friendService;
    private final JwtProperties jwtProperties;
    private final WebSocketMessageService webSocketMessageService;

    @Override
    public LoginVO login(LoginDTO dto) {
        User user = this.findUserByUserName(dto.getUserName());
        if (Objects.isNull(user)) {
            throw new GlobalException("用户不存在");
        }
        if (user.getIsBanned()) {
            String tip = String.format("您的账号因'%s'已被管理员封禁,请联系客服!",user.getReason());
            throw new GlobalException(tip);
        }
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new GlobalException(String.valueOf(ResultCode.PASSWOR_ERROR));
        }
        // 生成token
        UserSession session = BeanUtils.copyProperties(user, UserSession.class);
        if (session != null) {
            session.setUserId(user.getId());
            session.setTerminal(dto.getTerminal());
        }
        String strJson = JSON.toJSONString(session);
        String accessToken = JwtUtil.sign(user.getId(), strJson, jwtProperties.getAccessTokenExpireIn(),
            jwtProperties.getAccessTokenSecret());
        String refreshToken = JwtUtil.sign(user.getId(), strJson, jwtProperties.getRefreshTokenExpireIn(),
            jwtProperties.getRefreshTokenSecret());
        LoginVO vo = new LoginVO();
        vo.setAccessToken(accessToken);
        vo.setAccessTokenExpiresIn(jwtProperties.getAccessTokenExpireIn());
        vo.setRefreshToken(refreshToken);
        vo.setRefreshTokenExpiresIn(jwtProperties.getRefreshTokenExpireIn());
        user.setLastLoginTime(new Date());
        this.updateById(user);
        return vo;
    }

    @Override
    public LoginVO refreshToken(String refreshToken) {
        //验证 token
        if (!JwtUtil.checkSign(refreshToken, jwtProperties.getRefreshTokenSecret())) {
            throw new GlobalException("您的登录信息已过期，请重新登录");
        }
        String strJson = JwtUtil.getInfo(refreshToken);
        Long userId = JwtUtil.getUserId(refreshToken);
        User user = this.getById(userId);
        if (Objects.isNull(user)) {
            throw new GlobalException("用户不存在");
        }
        if (user.getIsBanned()) {
            String tip = String.format("您的账号因'%s'被管理员封禁,请联系客服!",user.getReason());
            throw new GlobalException(tip);
        }
        String accessToken =
            JwtUtil.sign(userId, strJson, jwtProperties.getAccessTokenExpireIn(), jwtProperties.getAccessTokenSecret());
        String newRefreshToken = JwtUtil.sign(userId, strJson, jwtProperties.getRefreshTokenExpireIn(),
            jwtProperties.getRefreshTokenSecret());
        LoginVO vo = new LoginVO();
        vo.setAccessToken(accessToken);
        vo.setAccessTokenExpiresIn(jwtProperties.getAccessTokenExpireIn());
        vo.setRefreshToken(newRefreshToken);
        vo.setRefreshTokenExpiresIn(jwtProperties.getRefreshTokenExpireIn());
        return vo;
    }

    @Override
    public void register(RegisterDTO dto) {
        User user = this.findUserByUserName(dto.getUserName());
        if (!Objects.isNull(user)) {
            throw new GlobalException(String.valueOf(ResultCode.USERNAME_ALREADY_REGISTER));
        }
        user = BeanUtils.copyProperties(dto, User.class);
        if (user != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        this.save(user);
        if (user != null) {
            log.info("注册用户，用户id:{},用户名:{},昵称:{}", user.getId(), dto.getUserName(), dto.getNickName());
        }
    }

    @Override
    public void modifyPassword(ModifyPwdDTO dto) {
        UserSession session = SessionContext.getSession();
        User user = this.getById(session.getUserId());
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new GlobalException("旧密码不正确");
        }
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        this.updateById(user);
        log.info("用户修改密码，用户id:{},用户名:{},昵称:{}", user.getId(), user.getUserName(), user.getNickName());
    }

    @Override
    public User findUserByUserName(String username) {
        LambdaQueryWrapper<User> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(User::getUserName, username);
        return this.getOne(queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void update(UserVO vo) {
        UserSession session = SessionContext.getSession();
        if (!session.getUserId().equals(vo.getId())) {
            throw new GlobalException("不允许修改其他用户的信息!");
        }
        User user = this.getById(vo.getId());
        if (Objects.isNull(user)) {
            throw new GlobalException("用户不存在");
        }

        if (!user.getNickName().equals(vo.getNickName()) || !user.getHeadImageThumb().equals(vo.getHeadImageThumb())) {
            // 更新好友昵称和头像
            LambdaUpdateWrapper<Friend> wrapper1 = Wrappers.lambdaUpdate();
            wrapper1.eq(Friend::getFriendId, session.getUserId());
            wrapper1.set(Friend::getFriendNickName,vo.getNickName());
            wrapper1.set(Friend::getFriendHeadImage,vo.getHeadImageThumb());
            friendService.update(wrapper1);
            // 更新群聊中的昵称和头像
            LambdaUpdateWrapper<GroupMember> wrapper2 = Wrappers.lambdaUpdate();
            wrapper2.eq(GroupMember::getUserId, session.getUserId());
            wrapper2.set(GroupMember::getHeadImage,vo.getHeadImageThumb());
            wrapper2.set(GroupMember::getUserNickName,vo.getNickName());
            groupMemberService.update(wrapper2);
        }
        // 更新用户信息
        user.setNickName(vo.getNickName());
        user.setSex(vo.getSex());
        user.setSignature(vo.getSignature());
        user.setHeadImage(vo.getHeadImage());
        user.setHeadImageThumb(vo.getHeadImageThumb());
        this.updateById(user);
        log.info("用户信息更新，用户:{}}", user);
    }

    @Override
    public UserVO findUserById(Long id) {
        User user = this.getById(id);
        UserVO vo = BeanUtils.copyProperties(user, UserVO.class);
        if (vo != null) {
            vo.setOnline(webSocketMessageService.isOnline(id));
        }
        return vo;
    }

    @Override
    public List<UserVO> findUserByName(String name) {
        LambdaQueryWrapper<User> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.like(User::getUserName, name).or().like(User::getNickName, name).last("limit 20");
        List<User> users = this.list(queryWrapper);
        List<Long> userIds = users.stream().map(User::getId).collect(Collectors.toList());
        List<Long> onlineUserIds = webSocketMessageService.getOnlineUser(userIds);
        return users.stream().map(u -> {
            UserVO vo = BeanUtils.copyProperties(u, UserVO.class);
            if (vo != null) {
                vo.setOnline(onlineUserIds.contains(u.getId()));
            }
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<OnlineTerminalVO> getOnlineTerminals(String userIds) {
        List<Long> userIdList = Arrays.stream(userIds.split(",")).map(Long::parseLong).collect(Collectors.toList());
        // 查询在线的终端
        Map<Long, List<TerminalType>> terminalMap = webSocketMessageService.getOnlineTerminal(userIdList);
        // 组装vo
        List<OnlineTerminalVO> vos = new LinkedList<>();
        terminalMap.forEach((userId, types) -> {
            List<Integer> terminals = types.stream().map(TerminalType::code).collect(Collectors.toList());
            vos.add(new OnlineTerminalVO(userId, terminals));
        });
        return vos;
    }

    @Override
    public ListResultVO<UserVO> findUserList(PageQueryDTO dto) {
        UserSession session = SessionContext.getSession();
        if (this.findUserById(session.getUserId()).getType()!=0){
            throw new GlobalException("不是管理账户，禁止查询!");
        }
        if(dto.getBanned()==null){
            throw new GlobalException("封禁状态未添加!");
        }
        LambdaQueryWrapper<User> queryWrapper = Wrappers.lambdaQuery();
        List<User> users=this.page(new Page<>(dto.getPageNum(), dto.getPageSize()),queryWrapper.eq(User::getIsBanned,dto.getBanned())).getRecords();
        long count = this.count(queryWrapper);
        ListResultVO<UserVO> result = new ListResultVO<>();
        result.setList(users.stream().map(user -> {
            UserVO vo = BeanUtils.copyProperties(user,UserVO.class);
            if (vo!=null){
                vo.setOnline(webSocketMessageService.isOnline(user.getId()));
                return vo;
            }else
                throw new GlobalException("用户信息转换失败!");
        }).collect(Collectors.toList()));
        result.setTotal(count);
        return result;
    }
}
