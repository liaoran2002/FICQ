package org.lc.ficq.service;

import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.validation.Valid;
import org.lc.ficq.dto.PageQueryDTO;
import org.lc.ficq.dto.PrivateMessageDTO;
import org.lc.ficq.entity.PrivateMessage;
import org.lc.ficq.vo.ListResultVO;
import org.lc.ficq.vo.PrivateMessageVO;


import java.util.List;

public interface PrivateMessageService extends IService<PrivateMessage> {

    /**
     * 发送私聊消息(高并发接口，查询mysql接口都要进行缓存)
     *
     * @param dto 私聊消息
     * @return 消息id
     */
    PrivateMessageVO sendMessage(PrivateMessageDTO dto);


    /**
     * 撤回消息
     *
     * @param id 消息id
     */
    PrivateMessageVO recallMessage(Long id);

    /**
     * 拉取历史聊天记录
     *
     * @param friendId 好友id
     * @param page     页码
     * @param size     页码大小
     * @return 聊天记录列表
     */
    List<PrivateMessageVO> findHistoryMessage(Long friendId, Long page, Long size);


    /**
     * 拉取离线消息，只能拉取最近1个月的消息，最多拉取1000条
     *
     * @param minId 消息起始id
     */
    void pullOfflineMessage(Long minId);

    /**
     * 消息已读,将整个会话的消息都置为已读状态
     *
     * @param friendId 好友id
     */
    void readedMessage(Long friendId);

    /**
     *  获取某个会话中已读消息的最大id
     *
     * @param friendId 好友id
     */
    Long getMaxReadedId(Long friendId);

    ListResultVO<PrivateMessageVO> findMessageList(@Valid PageQueryDTO dto);

    ListResultVO<PrivateMessageVO> findSensitiveWordHit(@Valid PageQueryDTO dto);
}
