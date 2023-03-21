package com.fcwr.ficq;

import com.fcwr.ficq.dao.UserDao;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class RunScript {

    @PostConstruct
    public void start(){
        System.out.println("服务器启动......");
    }

    @PreDestroy
    public void end(){
        UserDao.updateAllExit();
        System.out.println("服务器关闭......");
    }
}

