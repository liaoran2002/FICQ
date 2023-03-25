package com.fcwr.ficq.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fcwr.ficq.dao.UserDao;
import com.fcwr.ficq.entity.Message;
import com.fcwr.ficq.utils.MessageUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

//@ConditionalOnClass(WebSocketConfig.class)
@ServerEndpoint(value = "/chat", configurator = GetHttpSessionConfigurator.class)
@Component
public class ChatEndpoint {
    private static  Map<String, ChatEndpoint> onlineUsers = new ConcurrentHashMap<>();
    private Session session;
    private HttpSession httpSession;
    private String username;
    @Value("${file.chatPath}")
    private String chatPath;
    @Value("${file.upChatFolder}")
    private String upChat;
    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        this.session = session;
        this.httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        this.username=(String) this.httpSession.getAttribute("user");
        if(this.username==null){
            try {
                this.session.close();
            }catch (Exception e){
                return;
            }
            return;
        }
        if (onlineUsers.get(this.username)!=null){
            try {
                onlineUsers.get(this.username).session.close();
                onlineUsers.remove(this.username);
            }catch (Exception e){
                return;
            }
        }
        UserDao.updateLogin(this.username);
        onlineUsers.put(this.username, this);
        String resultMessage = MessageUtils.getResultMessage(getNames());
        broadcastAllUsers(resultMessage);
    }

    private void broadcastAllUsers(String message) {
        try {
            Set<String> names = onlineUsers.keySet();
            for (String name : names) {
                ChatEndpoint client = onlineUsers.get(name);
                client.session.getBasicRemote().sendText(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Set<String> getNames() {
        return onlineUsers.keySet();
    }

    @OnMessage
    public void onMessage(String message) {
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            Message value = objectMapper.readValue(message, Message.class);
            String to_name = value.getTo_name();
            if(to_name==null){
                return;
            }
            if (to_name.equals("群聊")){
                String messageJson = getMessageJson(value,to_name);
                broadcastAllUsers(messageJson);
            }else{
                Session toSession = onlineUsers.get(to_name).session;
                if(toSession == null){
                    onlineUsers.get(username).session.getBasicRemote().sendText(to_name +"已离线");
                }else{
                    String messageJson = getMessageJson(value,to_name);
                    if (messageJson!=null){
                        toSession.getBasicRemote().sendText(messageJson);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public String getMessageJson(Message value,String to_name){
        try{
            switch (value.getType()) {
                case "join":
                    return MessageUtils.getMessageJoin(value);
                case "chat":
                    return MessageUtils.getMessage(value);
                case "image":
                    MultipartFile imageFile=(MultipartFile)value.getContent();
                    String saveImageFile = saveFile(imageFile, to_name);
                    if(saveImageFile==null){
                        onlineUsers.get(username).session.getBasicRemote().sendText("您发送给" + to_name + "的消息出错了,请重试!");
                        break;
                    }
                    value.setContent(saveImageFile);
                    return MessageUtils.getMessageImage(value);
                case "video":
                    MultipartFile videoFile=(MultipartFile)value.getContent();
                    String saveVideoFile = saveFile(videoFile, to_name);
                    if(saveVideoFile==null){
                        onlineUsers.get(username).session.getBasicRemote().sendText("您发送给" + to_name + "的消息出错了,请重试!");
                        break;
                    }
                    value.setContent(saveVideoFile);
                    return MessageUtils.getMessageVideo(value);
                default:
                    onlineUsers.get(username).session.getBasicRemote().sendText("您发送给" + to_name + "的消息出错了,请重试!");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public String saveFile(MultipartFile file,String to_name) {
        String to_id = UserDao.queryIdByName(to_name);
        if (!file.isEmpty()&&to_id!=null) {
            try {
                String oldFileName = file.getOriginalFilename();
                String ext = oldFileName.substring(oldFileName.lastIndexOf(".")+1);
                String uuid = UUID.randomUUID().toString().replace("-", "");
                String fileExt="/" + uuid + "." + ext;
                File userFile = new File(upChat+"image/" + to_id + "/");
                if(userFile.exists()){
                    System.out.println(userFile.getName()+(userFile.createNewFile() ? "已创建" : "创建失败"));
                }
                if (ext.matches("(jpg|png|webp|gif|svg)")) {
                    String path = upChat+"image/"+ to_id +fileExt;
                    file.transferTo(new File(path));
                    return chatPath+"video/" + to_id + fileExt;
                } else if (ext.matches("(avi|wmv|mpeg|mp4|m4v|mov|asf|flv|f4v|rmvb|rm|3gp|vob)")) {
                    String path = upChat+"video/"+ to_id +fileExt;
                    file.transferTo(new File(path));
                    return chatPath + "video/" + to_id + fileExt;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println(this.username+ "离线了");
        UserDao.updateExit(this.username);
        onlineUsers.remove(this.username);
    }
}
