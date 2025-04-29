package org.lc.ficq.netty.tcp.endecode;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.lc.ficq.model.SendInfo;

import java.util.List;

@Slf4j
public class MessageProtocolDecoder extends ReplayingDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf.readableBytes() < 4) {
            return;
        }
        // 获取到包的长度
        long length = byteBuf.readLong();
        // 转成IMSendInfo
        ByteBuf contentBuf = byteBuf.readBytes((int) length);
        String content = contentBuf.toString(CharsetUtil.UTF_8);
        ObjectMapper objectMapper = new ObjectMapper();
        SendInfo sendInfo = objectMapper.readValue(content, SendInfo.class);
        list.add(sendInfo);
    }
}
