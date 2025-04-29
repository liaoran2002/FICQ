package org.lc.ficq.netty.processor;

import org.lc.ficq.enums.CmdType;
import org.lc.ficq.util.SpringContextHolder;

public class ProcessorFactory {

    public static AbstractMessageProcessor createProcessor(CmdType cmd) {
        return switch (cmd) {
            case LOGIN-> SpringContextHolder.getBean(LoginProcessor.class);
            case HEART_BEAT -> SpringContextHolder.getBean(HeartbeatProcessor.class);
            case PRIVATE_MESSAGE->SpringContextHolder.getBean(PrivateMessageProcessor.class);
            case GROUP_MESSAGE->SpringContextHolder.getBean(GroupMessageProcessor.class);
            case SYSTEM_MESSAGE->SpringContextHolder.getBean(SystemMessageProcessor.class);
            default -> null;
        };
    }

}
