package com.fcwr.ficq.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fcwr.ficq.dao.MessageDao;
import com.fcwr.ficq.entity.Message;
import com.fcwr.ficq.entity.ResultMessage;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MessageUtils {

	public static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	//{"id":"1","from_name":"","to_name":"","type":"join","content":"content","send_time":""}
	public static String getMessage(Message message) {
		message.setContent(Base64Utils.encode((String) message.getContent()));
		MessageDao.insert(message);
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			Message msg = MessageDao.getMessage(message);
			msg.setContent(Base64Utils.decode((String) msg.getContent()));
			return objectMapper.writeValueAsString(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static String getMessageJoin(Message message) {
		message.setId(1);
		message.setSend_time(format.format(new Date()));
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.writeValueAsString(MessageDao.getMessage(message)).replace("\"id\":\"1\",","");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static String getMessageImage(Message message) {
		MessageDao.insertImage(message);
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.writeValueAsString(MessageDao.getMessage(message));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getMessageVideo(Message message) {
		MessageDao.insertVideo(message);
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.writeValueAsString(MessageDao.getMessage(message));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static String getResultMessage(Object message) {
		ResultMessage messageResult = new ResultMessage();
		messageResult.setMessage(message);
		messageResult.setSend_time(format.format(new Date()));
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.writeValueAsString(messageResult);
		}catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}
}
