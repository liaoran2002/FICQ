package org.lc.ficq.service.impl;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.lc.ficq.contant.Constant;
import org.lc.ficq.dto.GroupMessageDTO;
import org.lc.ficq.dto.PageQueryDTO;
import org.lc.ficq.entity.Group;
import org.lc.ficq.entity.GroupMember;
import org.lc.ficq.entity.GroupMessage;
import org.lc.ficq.enums.MessageStatus;
import org.lc.ficq.enums.MessageType;
import org.lc.ficq.enums.TerminalType;
import org.lc.ficq.exception.GlobalException;
import org.lc.ficq.mapper.GroupMessageMapper;
import org.lc.ficq.model.GroupMessageModel;
import org.lc.ficq.model.UserInfo;
import org.lc.ficq.netty.WebSocketMessageService;
import org.lc.ficq.service.GroupMemberService;
import org.lc.ficq.service.GroupMessageService;
import org.lc.ficq.service.GroupService;
import org.lc.ficq.session.SessionContext;
import org.lc.ficq.session.UserSession;
import org.lc.ficq.util.BeanUtils;
import org.lc.ficq.util.CommaTextUtils;
import org.lc.ficq.util.SensitiveFilterUtil;
import org.lc.ficq.vo.GroupMessageVO;
import org.lc.ficq.vo.ListResultVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupMessageServiceImpl extends ServiceImpl<GroupMessageMapper, GroupMessage> implements
        GroupMessageService {
    private final GroupService groupService;
    private final GroupMemberService groupMemberService;
    private final WebSocketMessageService webSocketMessageService;
    private final SensitiveFilterUtil sensitiveFilterUtil;

    @Override
    public GroupMessageVO sendMessage(GroupMessageDTO dto) {
        UserSession session = SessionContext.getSession();
        Group group = groupService.getAndCheckById(dto.getGroupId());
        // 是否在群聊里面
        GroupMember member = groupMemberService.findByGroupAndUserId(dto.getGroupId(), session.getUserId());
        if (Objects.isNull(member) || member.getQuit()) {
            throw new GlobalException("您已不在群聊里面，无法发送消息");
        }
        // 群聊成员列表
        List<Long> userIds = groupMemberService.findUserIdsByGroupId(group.getId());
        if (dto.getReceipt() && userIds.size() > Constant.MAX_LARGE_GROUP_MEMBER) {
            // 大群的回执消息过于消耗资源，不允许发送
            throw new GlobalException(String.format("当前群聊大于%s人,不支持发送回执消息", Constant.MAX_LARGE_GROUP_MEMBER));
        }
        // 不用发给自己
        userIds = userIds.stream().filter(id -> !session.getUserId().equals(id)).collect(Collectors.toList());
        // 保存消息
        GroupMessage msg = BeanUtils.copyProperties(dto, GroupMessage.class);
        if (msg != null) {
            msg.setSendId(session.getUserId());
            msg.setStatus(MessageStatus.SENDED.code());
            msg.setSendTime(new Date());
            msg.setSendNickName(member.getShowNickName());
            msg.setAtUserIds(CommaTextUtils.asText(dto.getAtUserIds()));
            // 过滤内容中的敏感词
            if (MessageType.TEXT.code().equals(dto.getType())) {
                Map.Entry<String, Boolean> stringBooleanEntry = sensitiveFilterUtil.filter(dto.getContent());
                msg.setContent(stringBooleanEntry.getKey());
                if (stringBooleanEntry.getValue()) {
                    msg.setType(MessageType.SENTEXT.code());
                }
            }
            this.save(msg);
        }
        // 群发
        GroupMessageVO msgInfo = BeanUtils.copyProperties(msg, GroupMessageVO.class);
        if (msgInfo != null) {
            msgInfo.setAtUserIds(dto.getAtUserIds());
        }
        GroupMessageModel<GroupMessageVO> sendMessage = new GroupMessageModel<>();
        sendMessage.setSender(new UserInfo(session.getUserId(), session.getTerminal()));
        sendMessage.setRecvIds(userIds);
        sendMessage.setSendResult(false);
        sendMessage.setData(msgInfo);
        webSocketMessageService.sendGroupMessage(sendMessage);
        log.info("发送群聊消息，发送id:{},群聊id:{},内容:{}", session.getUserId(), dto.getGroupId(), dto.getContent());
        return msgInfo;
    }

    @Transactional
    @Override
    public GroupMessageVO recallMessage(Long id) {
        UserSession session = SessionContext.getSession();
        GroupMessage msg = this.getById(id);
        if (Objects.isNull(msg)) {
            throw new GlobalException("消息不存在");
        }
        if (!msg.getSendId().equals(session.getUserId())) {
            throw new GlobalException("这条消息不是由您发送,无法撤回");
        }
        if (System.currentTimeMillis() - msg.getSendTime().getTime() > Constant.ALLOW_RECALL_SECOND * 1000) {
            throw new GlobalException("消息已发送超过5分钟，无法撤回");
        }
        // 判断是否在群里
        GroupMember member = groupMemberService.findByGroupAndUserId(msg.getGroupId(), session.getUserId());
        if (Objects.isNull(member) || Boolean.TRUE.equals(member.getQuit())) {
            throw new GlobalException("您已不在群聊里面，无法撤回消息");
        }
        // 修改数据库
        msg.setStatus(MessageStatus.RECALL.code());
        this.updateById(msg);
        // 生成一条撤回消息
        GroupMessage recallMsg = new GroupMessage();
        recallMsg.setStatus(MessageStatus.UNSEND.code());
        recallMsg.setType(MessageType.RECALL.code());
        recallMsg.setGroupId(msg.getGroupId());
        recallMsg.setSendId(session.getUserId());
        recallMsg.setSendNickName(member.getShowNickName());
        recallMsg.setContent(id.toString());
        recallMsg.setSendTime(new Date());
        this.save(recallMsg);
        // 群发
        List<Long> userIds = groupMemberService.findUserIdsByGroupId(msg.getGroupId());
        GroupMessageVO msgInfo = BeanUtils.copyProperties(recallMsg, GroupMessageVO.class);
        GroupMessageModel<GroupMessageVO> sendMessage = new GroupMessageModel<>();
        sendMessage.setSender(new UserInfo(session.getUserId(), session.getTerminal()));
        sendMessage.setRecvIds(userIds);
        sendMessage.setData(msgInfo);
        webSocketMessageService.sendGroupMessage(sendMessage);
        log.info("撤回群聊消息，发送id:{},群聊id:{},内容:{}", session.getUserId(), msg.getGroupId(), msg.getContent());
        return msgInfo;
    }


    @Override
    public void pullOfflineMessage(Long minId) {
        UserSession session = SessionContext.getSession();
        if (!webSocketMessageService.isOnline(session.getUserId())) {
            throw new GlobalException("网络连接失败，无法拉取离线消息");
        }
        // 查询用户加入的群组
        List<GroupMember> members = groupMemberService.findByUserId(session.getUserId());
        Map<Long, GroupMember> groupMemberMap = CollStreamUtil.toIdentityMap(members, GroupMember::getGroupId);
        Set<Long> groupIds = groupMemberMap.keySet();
        if (CollectionUtil.isEmpty(groupIds)) {
            // 关闭加载中标志
            this.sendLoadingMessage(false);
            return;
        }
        // 开启加载中标志
        this.sendLoadingMessage(true);
        // 只能拉取最近3个月的,最多拉取3000条
        int months = session.getTerminal().equals(TerminalType.APP.code()) ? 1 : 3;
        Date minDate = DateUtils.addMonths(new Date(), -months);
        LambdaQueryWrapper<GroupMessage> wrapper = Wrappers.lambdaQuery();
        wrapper.gt(GroupMessage::getId, minId)
                .gt(GroupMessage::getSendTime, minDate)
                .in(GroupMessage::getGroupId, groupIds)
                .orderByAsc(GroupMessage::getId);
        List<GroupMessage> messages = this.list(wrapper);
        // 通过群聊对消息进行分组
        Map<Long, List<GroupMessage>> messageGroupMap = messages.stream().collect(Collectors.groupingBy(GroupMessage::getGroupId));
        // 退群前的消息
        List<GroupMember> quitMembers = groupMemberService.findQuitInMonth(session.getUserId());
        for (GroupMember quitMember : quitMembers) {
            wrapper = Wrappers.lambdaQuery();
            wrapper.gt(GroupMessage::getId, minId)
                    .between(GroupMessage::getSendTime, minDate, quitMember.getQuitTime())
                    .eq(GroupMessage::getGroupId, quitMember.getGroupId())
                    .ne(GroupMessage::getStatus, MessageStatus.RECALL.code())
                    .orderByAsc(GroupMessage::getId);
            List<GroupMessage> groupMessages = this.list(wrapper);
            messageGroupMap.put(quitMember.getGroupId(), groupMessages);
            groupMemberMap.put(quitMember.getGroupId(), quitMember);
        }
        // 推送消息
        AtomicInteger sendCount = new AtomicInteger();
        messageGroupMap.forEach((groupId, groupMessages) -> {
            for (GroupMessage m : groupMessages) {
                // 排除加群之前的消息
                GroupMember member = groupMemberMap.get(m.getGroupId());
                if (DateUtil.compare(member.getCreatedTime(), m.getSendTime()) > 0) {
                    continue;
                }
                // 排除不需要接收的消息
                List<String> recvIds = CommaTextUtils.asList(m.getRecvIds());
                if (!recvIds.isEmpty() && !recvIds.contains(session.getUserId().toString())) {
                    continue;
                }
                // 组装vo
                GroupMessageVO vo = BeanUtils.copyProperties(m, GroupMessageVO.class);
                // 被@用户列表
                List<String> atIds = CommaTextUtils.asList(m.getAtUserIds());
                if (vo != null) {
                    vo.setAtUserIds(atIds.stream().map(Long::parseLong).collect(Collectors.toList()));
                }
                // 填充状态
                if (vo != null) {
                    vo.setStatus(MessageStatus.UNSEND.code());
                }
                // 推送
                GroupMessageModel<GroupMessageVO> sendMessage = new GroupMessageModel<>();
                sendMessage.setSender(new UserInfo(m.getSendId(), TerminalType.WEB.code()));
                sendMessage.setRecvIds(Collections.singletonList(session.getUserId()));
                sendMessage.setRecvTerminals(Collections.singletonList(session.getTerminal()));
                sendMessage.setSendResult(false);
                sendMessage.setSendToSelf(false);
                sendMessage.setData(vo);
                webSocketMessageService.sendGroupMessage(sendMessage);
                sendCount.getAndIncrement();
            }
        });
        // 关闭加载中标志
        this.sendLoadingMessage(false);
        log.info("拉取离线群聊消息,用户id:{},数量:{}", session.getUserId(), sendCount.get());
    }

    @Override
    public void readedMessage(Long groupId) {
        UserSession session = SessionContext.getSession();
        // 取出最后的消息id
        LambdaQueryWrapper<GroupMessage> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(GroupMessage::getGroupId, groupId)
                .orderByDesc(GroupMessage::getId)
                .last("limit 1")
                .select(GroupMessage::getId);
        GroupMessage message = this.getOne(wrapper);
        if (Objects.isNull(message)) {
            return;
        }
        // 推送消息给自己的其他终端,同步清空会话列表中的未读数量
        GroupMessageVO msgInfo = new GroupMessageVO();
        msgInfo.setType(MessageType.READED.code());
        msgInfo.setSendTime(new Date());
        msgInfo.setSendId(session.getUserId());
        msgInfo.setGroupId(groupId);
        GroupMessageModel<GroupMessageVO> sendMessage = new GroupMessageModel<>();
        sendMessage.setSender(new UserInfo(session.getUserId(), session.getTerminal()));
        sendMessage.setSendToSelf(true);
        sendMessage.setData(msgInfo);
        sendMessage.setSendResult(true);
        webSocketMessageService.sendGroupMessage(sendMessage);
    }

    /*
    @Override
    public List<Long> findReadedUsers(Long groupId, Long messageId) {
        UserSession session = SessionContext.getSession();
        GroupMessage message = this.getById(messageId);
        if (Objects.isNull(message)) {
            throw new GlobalException("消息不存在");
        }
        // 是否在群聊里面
        GroupMember member = groupMemberService.findByGroupAndUserId(groupId, session.getUserId());
        if (Objects.isNull(member) || member.getQuit()) {
            throw new GlobalException("您已不在群聊里面");
        }
        // 已读位置key
        String key = StrUtil.join(":", RedisKey.IM_GROUP_READED_POSITION, groupId);
        // 一次获取所有用户的已读位置
        Map<Object, Object> maxIdMap = redisTemplate.opsForHash().entries(key);
        // 返回已读用户的id集合
        return getReadedUserIds(maxIdMap, message.getId(),message.getSendId());
    }*/

    @Override
    public List<GroupMessageVO> findHistoryMessage(Long groupId, Long page, Long size) {
        page = page > 0 ? page : 1;
        size = size > 0 ? size : 10;
        Long userId = SessionContext.getSession().getUserId();
        long stIdx = (page - 1) * size;
        // 群聊成员信息
        GroupMember member = groupMemberService.findByGroupAndUserId(groupId, userId);
        if (Objects.isNull(member) || member.getQuit()) {
            throw new GlobalException("您已不在群聊中");
        }
        // 查询聊天记录，只查询加入群聊时间之后的消息
        QueryWrapper<GroupMessage> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(GroupMessage::getGroupId, groupId).gt(GroupMessage::getSendTime, member.getCreatedTime())
                .ne(GroupMessage::getStatus, MessageStatus.RECALL.code()).orderByDesc(GroupMessage::getId).last("limit " + stIdx + "," + size);
        List<GroupMessage> messages = this.list(wrapper);
        List<GroupMessageVO> messageInfos =
                messages.stream().map(m -> BeanUtils.copyProperties(m, GroupMessageVO.class)).collect(Collectors.toList());
        log.info("拉取群聊记录，用户id:{},群聊id:{}，数量:{}", userId, groupId, messageInfos.size());
        return messageInfos;
    }

    @Override
    public ListResultVO<GroupMessageVO> findMessageList(PageQueryDTO dto) {
        LambdaQueryWrapper<GroupMessage> wrapper = Wrappers.lambdaQuery();
        List<GroupMessage> groupMessages = this.page(new Page<>(dto.getPageNum(), dto.getPageSize()), wrapper).getRecords();
        ListResultVO<GroupMessageVO> vo = new ListResultVO<>();
        vo.setList(groupMessages.stream().map(groupMessage -> BeanUtils.copyProperties(groupMessage, GroupMessageVO.class)).toList());
        vo.setTotal(this.count(wrapper));
        return vo;
    }

    @Override
    public ListResultVO<GroupMessageVO> findSensitiveWordHit(PageQueryDTO dto) {
        LambdaQueryWrapper<GroupMessage> wrapper = Wrappers.lambdaQuery();
        List<GroupMessage> groupMessages = this.page(new Page<>(dto.getPageNum(), dto.getPageSize()), wrapper.eq(GroupMessage::getType, 5)).getRecords();
        ListResultVO<GroupMessageVO> vo = new ListResultVO<>();
        vo.setList(groupMessages.stream().map(groupMessage -> BeanUtils.copyProperties(groupMessage, GroupMessageVO.class)).toList());
        vo.setTotal(this.count(wrapper));
        return vo;
    }

    /*
    private List<Long> getReadedUserIds(Map<Object, Object> maxIdMap, Long messageId, Long sendId) {
        List<Long> userIds = new LinkedList<>();
        maxIdMap.forEach((k, v) -> {
            Long userId = Long.valueOf(k.toString());
            long maxId = Long.parseLong(v.toString());
            // 发送者不计入已读人数
            if (!sendId.equals(userId) && maxId >= messageId) {
                userIds.add(userId);
            }
        });
        return userIds;
    }
    */

    private void sendLoadingMessage(Boolean isLoadding) {
        UserSession session = SessionContext.getSession();
        GroupMessageVO msgInfo = new GroupMessageVO();
        msgInfo.setType(MessageType.LOADING.code());
        msgInfo.setContent(isLoadding.toString());
        GroupMessageModel<GroupMessageVO> sendMessage = new GroupMessageModel<>();
        sendMessage.setSender(new UserInfo(session.getUserId(), session.getTerminal()));
        sendMessage.setRecvIds(Collections.singletonList(session.getUserId()));
        sendMessage.setRecvTerminals(Collections.singletonList(session.getTerminal()));
        sendMessage.setData(msgInfo);
        sendMessage.setSendToSelf(false);
        sendMessage.setSendResult(false);
        webSocketMessageService.sendGroupMessage(sendMessage);
    }

}
