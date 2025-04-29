package org.lc.ficq.netty;

public interface Server {

    boolean isReady();

    void start();

    void stop();
}
