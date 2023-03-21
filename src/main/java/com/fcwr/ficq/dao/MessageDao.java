package com.fcwr.ficq.dao;

import com.fcwr.ficq.entity.Message;
import com.fcwr.ficq.utils.JdbcUtil;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

public class MessageDao {
    private static final JdbcTemplate jdbcTemplate=new JdbcTemplate(JdbcUtil.getDs());
    public static void insert(Message msg){
        jdbcTemplate.update("insert into message values(null,?,?,default,?,now())", msg.getFrom_name(), msg.getTo_name(), msg.getContent());
    }
    public static void insertImage(Message msg){
        jdbcTemplate.update("insert into message values(null,?,?,'image',?,now())", msg.getFrom_name(), msg.getTo_name(), msg.getContent());
    }
    public static void insertVideo(Message msg){
        jdbcTemplate.update("insert into message values(null,?,?,'video',?,now())", msg.getFrom_name(), msg.getTo_name(), msg.getContent());
    }
    public static Message getMessage(Message msg){
        return jdbcTemplate.query("select * from message where from_name=? and to_name=? and content=? ORDER BY send_time DESC limit 1", new BeanPropertyRowMapper<>(Message.class), msg.getFrom_name(), msg.getTo_name(), msg.getContent()).get(0);
    }
}
