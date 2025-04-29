package org.lc.ficq.listener;


import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONObject;
import org.lc.ficq.annotation.Listener;
import org.lc.ficq.enums.ListenerType;
import org.lc.ficq.model.SendResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

@Component
public class MessageListenerMulticaster {

    @Autowired(required = false)
    private List<MessageListener>  messageListeners  = Collections.emptyList();

    public  void multicast(ListenerType listenerType, List<SendResult> results){
        if(CollUtil.isEmpty(results)){
            return;
        }
        for(MessageListener listener:messageListeners){
            Listener annotation = listener.getClass().getAnnotation(Listener.class);
            if(annotation!=null && (annotation.type().equals(ListenerType.ALL) || annotation.type().equals(listenerType))){
                results.forEach(result->{
                    // 将data转回对象类型
                    if(result.getData() instanceof JSONObject data){
                        Type superClass = listener.getClass().getGenericInterfaces()[0];
                        Type type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
                        result.setData(data.toJavaObject(type));
                    }
                });
                // 回调到调用方处理
                listener.process(results);
            }
        }
    }


}
