package com.fcwr.ficq.dao;

import com.fcwr.ficq.entity.Friend;
import com.fcwr.ficq.utils.JdbcUtil;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public class FriendDao {
    private static final JdbcTemplate jdbcTemplate = new JdbcTemplate(JdbcUtil.getDs());

    public static Boolean ckeckFriend(String username, String friendname) {
        return jdbcTemplate.query("select count(*) from friend where username=? and friendname=? or username=? and friendname=?", new BeanPropertyRowMapper<>(Integer.class), username, friendname, friendname, username).get(0) != 0;
    }

    public static void addFriend(String username, String friendname) {
        if (ckeckFriend(username, friendname)) return;
        jdbcTemplate.update("insert into friend(username,friendname) values(?,?)", username, friendname);
    }

    public static Boolean deleteFriend(String username, String friendname) {
        if (!ckeckFriend(username, friendname)) return false;
        return jdbcTemplate.update("delete from friend where username=? and friendname=? or username=? and friendname=?", username, friendname, friendname, username)>0;
    }

    public static List<Friend> queryFriend(String username) {
        return jdbcTemplate.query("select * from friend where username=? or friendname=?", new BeanPropertyRowMapper<>(Friend.class), username);
    }
}
