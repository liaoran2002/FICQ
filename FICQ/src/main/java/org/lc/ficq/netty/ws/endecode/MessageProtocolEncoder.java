package org.lc.ficq.netty.ws.endecode;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.lc.ficq.model.SendInfo;

import java.util.List;

public class MessageProtocolEncoder extends MessageToMessageEncoder<SendInfo> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, SendInfo sendInfo, List<Object> list) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String text = objectMapper.writeValueAsString(sendInfo);
        TextWebSocketFrame frame = new TextWebSocketFrame(text);
        list.add(frame);
    }
}
