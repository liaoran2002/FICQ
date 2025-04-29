package org.lc.ficq;


import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;


@Component
public class RunScript {

    @PostConstruct
    public void start(){
        System.out.println("服务器启动......");
    }

    @PreDestroy
    public void end(){
        System.out.println("服务器关闭......");
    }
}

