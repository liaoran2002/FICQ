package org.lc.ficq.listener;

import org.lc.ficq.model.SendResult;

import java.util.List;

public interface MessageListener<T> {

     void process(List<SendResult<T>> result);

}
