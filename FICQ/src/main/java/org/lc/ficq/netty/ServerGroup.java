package org.lc.ficq.netty;

import jakarta.annotation.PreDestroy;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class ServerGroup implements CommandLineRunner {

    public static volatile long serverId = 0;
    private final List<Server> imServers;

    /***
     * 判断服务器是否就绪
     *
     **/
    public boolean isReady() {
        for (Server imServer : imServers) {
            if (!imServer.isReady()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void run(String... args) {
        // 启动服务
        for (Server imServer : imServers) {
            imServer.start();
        }
    }

    @PreDestroy
    public void destroy() {
        // 停止服务
        for (Server imServer : imServers) {
            imServer.stop();
        }
    }
}
