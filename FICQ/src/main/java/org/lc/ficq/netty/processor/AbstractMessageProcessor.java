package org.lc.ficq.netty.processor;

import io.netty.channel.ChannelHandlerContext;

public abstract class AbstractMessageProcessor<T> {

    public void process(ChannelHandlerContext ctx, T data) {
    }

    public void process(T data) {

    }

    @SuppressWarnings("unchecked")
    public T transForm(Object o) {
        return (T) o;
    }

}
