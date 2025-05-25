package org.lc.ficq.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.lc.ficq.contant.Constant;
import org.lc.ficq.dto.PageQueryDTO;
import org.lc.ficq.dto.PrivateMessageDTO;
import org.lc.ficq.entity.PrivateMessage;
import org.lc.ficq.entity.SensitiveWord;
import org.lc.ficq.enums.MessageStatus;
import org.lc.ficq.enums.MessageType;
import org.lc.ficq.enums.TerminalType;
import org.lc.ficq.exception.GlobalException;
import org.lc.ficq.mapper.PrivateMessageMapper;
import org.lc.ficq.model.PrivateMessageModel;
import org.lc.ficq.model.UserInfo;
import org.lc.ficq.netty.WebSocketMessageService;
import org.lc.ficq.service.FriendService;
import org.lc.ficq.service.PrivateMessageService;
import org.lc.ficq.session.SessionContext;
import org.lc.ficq.session.UserSession;
import org.lc.ficq.util.SensitiveFilterUtil;
import org.lc.ficq.vo.ListResultVO;
import org.lc.ficq.vo.PrivateMessageVO;
import org.lc.ficq.vo.SensitiveWordVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.lc.ficq.util.BeanUtils;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrivateMessageServiceImpl extends ServiceImpl<PrivateMessageMapper, PrivateMessage>
    implements PrivateMessageService {

    private final FriendService friendService;
    private final WebSocketMessageService webSocketMessageService;
    private final SensitiveFilterUtil sensitiveFilterUtil;

    @Override
    public PrivateMessageVO sendMessage(PrivateMessageDTO dto) {
        UserSession session = SessionContext.getSession();
        Boolean isFriends = friendService.isFriend(session.getUserId(), dto.getRecvId());
        if (Boolean.FALSE.equals(isFriends)) {
            throw new GlobalException("您已不是对方好友，无法发送消息");
        }
        // 保存消息
        PrivateMessage msg = BeanUtils.copyProperties(dto, PrivateMessage.class);
        if (msg != null) {
            msg.setSendId(session.getUserId());

        msg.setStatus(MessageStatus.UNSEND.code());
        msg.setSendTime(new Date());
        // 过滤内容中的敏感词
        if (MessageType.TEXT.code().equals(dto.getType())) {
            Map.Entry<String, Boolean> stringBooleanEntry = sensitiveFilterUtil.filter(dto.getContent());
            msg.setContent(stringBooleanEntry.getKey());
            if (stringBooleanEntry.getValue()) {
                msg.setType(MessageType.SENTEXT.code());
            }
        }
        }
        this.save(msg);
        // 推送消息
        PrivateMessageVO msgInfo = BeanUtils.copyProperties(msg, PrivateMessageVO.class);
        PrivateMessageModel<PrivateMessageVO> sendMessage = new PrivateMessageModel<>();
        sendMessage.setSender(new UserInfo(session.getUserId(), session.getTerminal()));
        if (msgInfo != null) {
            sendMessage.setRecvId(msgInfo.getRecvId());
        }
        sendMessage.setSendToSelf(true);
        sendMessage.setData(msgInfo);
        sendMessage.setSendResult(true);
        webSocketMessageService.sendPrivateMessage(sendMessage);
        log.info("发送私聊消息，发送id:{},接收id:{}，内容:{}", session.getUserId(), dto.getRecvId(), dto.getContent());
        return msgInfo;
    }

    @Transactional
    @Override
    public PrivateMessageVO recallMessage(Long id) {
        UserSession session = SessionContext.getSession();
        PrivateMessage msg = this.getById(id);
        if (Objects.isNull(msg)) {
            throw new GlobalException("消息不存在");
        }
        if (!msg.getSendId().equals(session.getUserId())) {
            throw new GlobalException("这条消息不是由您发送,无法撤回");
        }
        if (System.currentTimeMillis() - msg.getSendTime().getTime() > Constant.ALLOW_RECALL_SECOND * 1000) {
            throw new GlobalException("消息已发送超过5分钟，无法撤回");
        }
        // 修改消息状态
        msg.setStatus(MessageStatus.RECALL.code());
        this.updateById(msg);
        // 生成一条撤回消息
        PrivateMessage recallMsg = new PrivateMessage();
        recallMsg.setSendId(session.getUserId());
        recallMsg.setStatus(MessageStatus.UNSEND.code());
        recallMsg.setSendTime(new Date());
        recallMsg.setRecvId(msg.getRecvId());
        recallMsg.setType(MessageType.RECALL.code());
        recallMsg.setContent(id.toString());
        this.save(recallMsg);
        // 推送消息
        PrivateMessageVO msgInfo = BeanUtils.copyProperties(recallMsg, PrivateMessageVO.class);
        PrivateMessageModel<PrivateMessageVO> sendMessage = new PrivateMessageModel<>();
        sendMessage.setSender(new UserInfo(session.getUserId(), session.getTerminal()));
        if (msgInfo != null) {
            sendMessage.setRecvId(msgInfo.getRecvId());
        }
        sendMessage.setData(msgInfo);
        webSocketMessageService.sendPrivateMessage(sendMessage);
        log.info("撤回私聊消息，发送id:{},接收id:{}，内容:{}", msg.getSendId(), msg.getRecvId(), msg.getContent());
        return msgInfo;
    }

    @Override
    public List<PrivateMessageVO> findHistoryMessage(Long friendId, Long page, Long size) {
        page = page > 0 ? page : 1;
        size = size > 0 ? size : 10;
        Long userId = SessionContext.getSession().getUserId();
        long stIdx = (page - 1) * size;
        QueryWrapper<PrivateMessage> wrapper = new QueryWrapper<>();
        wrapper.lambda().and(
                wrap -> wrap.and(wp -> wp.eq(PrivateMessage::getSendId, userId).eq(PrivateMessage::getRecvId, friendId))
                    .or(wp -> wp.eq(PrivateMessage::getRecvId, userId).eq(PrivateMessage::getSendId, friendId)))
            .ne(PrivateMessage::getStatus, MessageStatus.RECALL.code()).orderByDesc(PrivateMessage::getId)
            .last("limit " + stIdx + "," + size);

        List<PrivateMessage> messages = this.list(wrapper);
        List<PrivateMessageVO> messageInfos =
            messages.stream().map(m -> BeanUtils.copyProperties(m, PrivateMessageVO.class))
                .collect(Collectors.toList());
        log.info("拉取聊天记录，用户id:{},好友id:{}，数量:{}", userId, friendId, messageInfos.size());
        return messageInfos;
    }

    @Override
    public void pullOfflineMessage(Long minId) {
        UserSession session = SessionContext.getSession();
        // 开启加载中标志
        this.sendLoadingMessage(true);
        // 获取当前用户的消息
        LambdaQueryWrapper<PrivateMessage> wrapper = Wrappers.lambdaQuery();
        // 只能拉取最近3个月的消息,移动端只拉取一个月消息
        int months = session.getTerminal().equals(TerminalType.APP.code()) ? 1 : 3;
        Date minDate = DateUtils.addMonths(new Date(), -months);
        wrapper.gt(PrivateMessage::getId, minId);
        wrapper.ge(PrivateMessage::getSendTime, minDate);
        wrapper.and(wp -> wp.eq(PrivateMessage::getSendId, session.getUserId()).or()
            .eq(PrivateMessage::getRecvId, session.getUserId()));
        wrapper.orderByAsc(PrivateMessage::getId);
        List<PrivateMessage> messages = this.list(wrapper);
        // 推送消息
        for (PrivateMessage m : messages) {
            PrivateMessageVO vo = BeanUtils.copyProperties(m, PrivateMessageVO.class);
            PrivateMessageModel<PrivateMessageVO> sendMessage = new PrivateMessageModel<>();
            sendMessage.setSender(new UserInfo(m.getSendId(), TerminalType.WEB.code()));
            sendMessage.setRecvId(session.getUserId());
            sendMessage.setRecvTerminals(List.of(session.getTerminal()));
            sendMessage.setSendToSelf(false);
            sendMessage.setData(vo);
            sendMessage.setSendResult(true);
            webSocketMessageService.sendPrivateMessage(sendMessage);
        }
        // 关闭加载中标志
        this.sendLoadingMessage(false);
        log.info("拉取私聊消息，用户id:{},数量:{}", session.getUserId(), messages.size());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void readedMessage(Long friendId) {
        UserSession session = SessionContext.getSession();
        // 推送消息给自己，清空会话列表上的已读数量
        PrivateMessageVO msgInfo = new PrivateMessageVO();
        msgInfo.setType(MessageType.READED.code());
        msgInfo.setSendId(session.getUserId());
        msgInfo.setRecvId(friendId);
        PrivateMessageModel<PrivateMessageVO> sendMessage = new PrivateMessageModel<>();
        sendMessage.setData(msgInfo);
        sendMessage.setSender(new UserInfo(session.getUserId(), session.getTerminal()));
        sendMessage.setSendToSelf(true);
        sendMessage.setSendResult(false);
        webSocketMessageService.sendPrivateMessage(sendMessage);
        // 推送回执消息给对方，更新已读状态
        msgInfo = new PrivateMessageVO();
        msgInfo.setType(MessageType.RECEIPT.code());
        msgInfo.setSendId(session.getUserId());
        msgInfo.setRecvId(friendId);
        sendMessage = new PrivateMessageModel<>();
        sendMessage.setSender(new UserInfo(session.getUserId(), session.getTerminal()));
        sendMessage.setRecvId(friendId);
        sendMessage.setSendToSelf(false);
        sendMessage.setSendResult(false);
        sendMessage.setData(msgInfo);
        webSocketMessageService.sendPrivateMessage(sendMessage);
        // 修改消息状态为已读
        LambdaUpdateWrapper<PrivateMessage> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.eq(PrivateMessage::getSendId, friendId).eq(PrivateMessage::getRecvId, session.getUserId())
            .eq(PrivateMessage::getStatus, MessageStatus.SENDED.code())
            .set(PrivateMessage::getStatus, MessageStatus.READED.code());
        this.update(updateWrapper);
        log.info("消息已读，接收方id:{},发送方id:{}", session.getUserId(), friendId);
    }

    @Override
    public Long getMaxReadedId(Long friendId) {
        UserSession session = SessionContext.getSession();
        LambdaQueryWrapper<PrivateMessage> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(PrivateMessage::getSendId, session.getUserId()).eq(PrivateMessage::getRecvId, friendId)
            .eq(PrivateMessage::getStatus, MessageStatus.READED.code()).orderByDesc(PrivateMessage::getId)
            .select(PrivateMessage::getId).last("limit 1");
        PrivateMessage message = this.getOne(wrapper);
        if (Objects.isNull(message)) {
            return -1L;
        }
        return message.getId();
    }

    @Override
    public ListResultVO<PrivateMessageVO> findSensitiveWordHit(PageQueryDTO dto) {
        LambdaQueryWrapper<PrivateMessage> wrapper = Wrappers.lambdaQuery();
        List<PrivateMessage> privateMessages = this.page(new Page<>(dto.getPageNum(), dto.getPageSize()),wrapper.eq(PrivateMessage::getType,5)).getRecords();
        ListResultVO<PrivateMessageVO> vo = new ListResultVO<>();
        vo.setList(privateMessages.stream().map(privateMessage -> BeanUtils.copyProperties(privateMessage,PrivateMessageVO.class)).toList());
        vo.setTotal(this.count(wrapper));
        return vo;
    }

    @Override
    public ListResultVO<PrivateMessageVO> findMessageList(PageQueryDTO dto) {
        LambdaQueryWrapper<PrivateMessage> wrapper = Wrappers.lambdaQuery();
        List<PrivateMessage> privateMessages = this.page(new Page<>(dto.getPageNum(), dto.getPageSize()),wrapper).getRecords();
        ListResultVO<PrivateMessageVO> vo = new ListResultVO<>();
        vo.setList(privateMessages.stream().map(privateMessage -> BeanUtils.copyProperties(privateMessage,PrivateMessageVO.class)).toList());
        vo.setTotal(this.count(wrapper));
        return vo;
    }

    private void sendLoadingMessage(Boolean isLoadding) {
        UserSession session = SessionContext.getSession();
        PrivateMessageVO msgInfo = new PrivateMessageVO();
        msgInfo.setType(MessageType.LOADING.code());
        msgInfo.setContent(isLoadding.toString());
        PrivateMessageModel<PrivateMessageVO> sendMessage = new PrivateMessageModel<>();
        sendMessage.setSender(new UserInfo(session.getUserId(), session.getTerminal()));
        sendMessage.setRecvId(session.getUserId());
        sendMessage.setRecvTerminals(List.of(session.getTerminal()));
        sendMessage.setData(msgInfo);
        sendMessage.setSendToSelf(false);
        sendMessage.setSendResult(false);
        webSocketMessageService.sendPrivateMessage(sendMessage);
    }
}
