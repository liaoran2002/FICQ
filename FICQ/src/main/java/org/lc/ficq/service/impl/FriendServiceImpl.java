package org.lc.ficq.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lc.ficq.entity.Friend;
import org.lc.ficq.model.PrivateMessageModel;
import org.lc.ficq.entity.PrivateMessage;
import org.lc.ficq.entity.User;
import org.lc.ficq.enums.MessageStatus;
import org.lc.ficq.enums.MessageType;
import org.lc.ficq.enums.TerminalType;
import org.lc.ficq.exception.GlobalException;
import org.lc.ficq.mapper.FriendMapper;
import org.lc.ficq.mapper.PrivateMessageMapper;
import org.lc.ficq.mapper.UserMapper;
import org.lc.ficq.model.UserInfo;
import org.lc.ficq.netty.WebSocketMessageService;
import org.lc.ficq.service.FriendService;
import org.lc.ficq.session.SessionContext;
import org.lc.ficq.session.UserSession;
import org.lc.ficq.vo.FriendVO;
import org.lc.ficq.vo.PrivateMessageVO;
import org.springframework.aop.framework.AopContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.lc.ficq.util.BeanUtils;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableAspectJAutoProxy(exposeProxy = true)
public class FriendServiceImpl extends ServiceImpl<FriendMapper, Friend> implements FriendService {

    private final PrivateMessageMapper privateMessageMapper;
    private final UserMapper userMapper;
    private final WebSocketMessageService webSocketMessageService;
    @Override
    public List<Friend> findAllFriends() {
        Long userId = SessionContext.getSession().getUserId();
        LambdaQueryWrapper<Friend> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Friend::getUserId, userId);
        return this.list(wrapper);
    }

    @Override
    public List<Friend> findByFriendIds(List<Long> friendIds) {
        Long userId = SessionContext.getSession().getUserId();
        LambdaQueryWrapper<Friend> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Friend::getUserId, userId);
        wrapper.in(Friend::getFriendId, friendIds);
        wrapper.eq(Friend::getDeleted,false);
        return this.list(wrapper);
    }

    @Override
    public List<FriendVO> findFriends() {
        List<Friend> friends = this.findAllFriends();
        return friends.stream().map(this::conver).collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addFriend(Long friendId) {
        long userId = SessionContext.getSession().getUserId();
        if (friendId.equals(userId)) {
            throw new GlobalException("不允许添加自己为好友");
        }
        // 互相绑定好友关系
        FriendServiceImpl proxy = (FriendServiceImpl) AopContext.currentProxy();
        proxy.bindFriend(userId, friendId);
        proxy.bindFriend(friendId, userId);
        // 推送添加好友提示
        sendAddTipMessage(friendId);
        log.info("添加好友，用户id:{},好友id:{}", userId, friendId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delFriend(Long friendId) {
        Long userId = SessionContext.getSession().getUserId();
        // 互相解除好友关系，走代理清理缓存
        FriendServiceImpl proxy = (FriendServiceImpl)AopContext.currentProxy();
        proxy.unbindFriend(userId, friendId);
        proxy.unbindFriend(friendId, userId);
        // 推送解除好友提示
        sendDelTipMessage(friendId);
        log.info("删除好友，用户id:{},好友id:{}", userId, friendId);
    }

    @Override
    public Boolean isFriend(Long userId1, Long userId2) {
        LambdaQueryWrapper<Friend> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Friend::getUserId, userId1);
        wrapper.eq(Friend::getFriendId, userId2);
        wrapper.eq(Friend::getDeleted,false);
        return this.exists(wrapper);
    }

    /**
     * 单向绑定好友关系
     *
     * @param userId   用户id
     * @param friendId 好友的用户id
     */
    public void bindFriend(Long userId, Long friendId) {
        QueryWrapper<Friend> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(Friend::getUserId, userId).eq(Friend::getFriendId, friendId);
        Friend friend = this.getOne(wrapper);
        if (Objects.isNull(friend)) {
            friend = new Friend();
        }
        friend.setUserId(userId);
        friend.setFriendId(friendId);
        User friendInfo = userMapper.selectById(friendId);
        friend.setFriendHeadImage(friendInfo.getHeadImage());
        friend.setFriendNickName(friendInfo.getNickName());
        friend.setDeleted(false);
        this.saveOrUpdate(friend);
        // 推送好友变化信息s
        sendAddFriendMessage(userId, friendId, friend);
    }

    /**
     * 单向解除好友关系
     *
     * @param userId   用户id
     * @param friendId 好友的用户id
     */
    public void unbindFriend(Long userId, Long friendId) {
        // 逻辑删除
        LambdaUpdateWrapper<Friend> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(Friend::getUserId, userId);
        wrapper.eq(Friend::getFriendId, friendId);
        wrapper.set(Friend::getDeleted,true);
        this.update(wrapper);
        // 推送好友变化信息
        sendDelFriendMessage(userId, friendId);
    }

    @Override
    public FriendVO findFriend(Long friendId) {
        UserSession session = SessionContext.getSession();
        LambdaQueryWrapper<Friend> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Friend::getUserId, session.getUserId());
        wrapper.eq(Friend::getFriendId, friendId);
        Friend friend = this.getOne(wrapper);
        if (Objects.isNull(friend)) {
            throw new GlobalException("对方不是您的好友");
        }
        return conver(friend);
    }

    private FriendVO conver(Friend f) {
        FriendVO vo = new FriendVO();
        vo.setId(f.getFriendId());
        vo.setHeadImage(f.getFriendHeadImage());
        vo.setNickName(f.getFriendNickName());
        vo.setDeleted(f.getDeleted());
        return vo;
    }

    public void sendAddFriendMessage(Long userId, Long friendId, Friend friend) {
        // 推送好友状态信息
        PrivateMessageVO msgInfo = new PrivateMessageVO();
        msgInfo.setSendId(friendId);
        msgInfo.setRecvId(userId);
        msgInfo.setSendTime(new Date());
        msgInfo.setType(MessageType.FRIEND_NEW.code());
        FriendVO vo = conver(friend);
        msgInfo.setContent(JSON.toJSONString(vo));
        PrivateMessageModel<PrivateMessageVO> sendMessage = new PrivateMessageModel<>();
        sendMessage.setSender(new UserInfo(friendId, TerminalType.UNKNOW.code()));
        sendMessage.setRecvId(userId);
        sendMessage.setData(msgInfo);
        sendMessage.setSendToSelf(false);
        sendMessage.setSendResult(false);
        webSocketMessageService.sendPrivateMessage(sendMessage);
    }

    void sendDelFriendMessage(Long userId, Long friendId) {
        // 推送好友状态信息
        PrivateMessageVO msgInfo = new PrivateMessageVO();
        msgInfo.setSendId(friendId);
        msgInfo.setRecvId(userId);
        msgInfo.setSendTime(new Date());
        msgInfo.setType(MessageType.FRIEND_DEL.code());
        PrivateMessageModel<PrivateMessageVO> sendMessage = new PrivateMessageModel<>();
        sendMessage.setSender(new UserInfo(friendId, TerminalType.UNKNOW.code()));
        sendMessage.setRecvId(userId);
        sendMessage.setData(msgInfo);
        sendMessage.setSendToSelf(false);
        sendMessage.setSendResult(false);
        webSocketMessageService.sendPrivateMessage(sendMessage);
    }

    void sendAddTipMessage(Long friendId) {
        UserSession session = SessionContext.getSession();
        PrivateMessage msg = new PrivateMessage();
        msg.setSendId(session.getUserId());
        msg.setRecvId(friendId);
        msg.setContent("你们已成为好友，现在可以开始聊天了");
        msg.setSendTime(new Date());
        msg.setStatus(MessageStatus.UNSEND.code());
        msg.setType(MessageType.TIP_TEXT.code());
        privateMessageMapper.insert(msg);
        // 推给对方
        PrivateMessageVO messageInfo = BeanUtils.copyProperties(msg, PrivateMessageVO.class);
        PrivateMessageModel<PrivateMessageVO> sendMessage = new PrivateMessageModel<>();
        sendMessage.setSender(new UserInfo(session.getUserId(), session.getTerminal()));
        sendMessage.setRecvId(friendId);
        sendMessage.setSendToSelf(false);
        sendMessage.setData(messageInfo);
        webSocketMessageService.sendPrivateMessage(sendMessage);
        // 推给自己
        sendMessage.setRecvId(session.getUserId());
        webSocketMessageService.sendPrivateMessage(sendMessage);
    }

    void sendDelTipMessage(Long friendId){
        UserSession session = SessionContext.getSession();
        // 推送好友状态信息
        PrivateMessage msg = new PrivateMessage();
        msg.setSendId(session.getUserId());
        msg.setRecvId(friendId);
        msg.setSendTime(new Date());
        msg.setType(MessageType.TIP_TEXT.code());
        msg.setStatus(MessageStatus.UNSEND.code());
        msg.setContent("你们的好友关系已被解除");
        privateMessageMapper.insert(msg);
        // 推送
        PrivateMessageVO messageInfo = BeanUtils.copyProperties(msg, PrivateMessageVO.class);
        PrivateMessageModel<PrivateMessageVO> sendMessage = new PrivateMessageModel<>();
        sendMessage.setSender(new UserInfo(friendId, TerminalType.UNKNOW.code()));
        sendMessage.setRecvId(friendId);
        sendMessage.setData(messageInfo);
        webSocketMessageService.sendPrivateMessage(sendMessage);
    }
}