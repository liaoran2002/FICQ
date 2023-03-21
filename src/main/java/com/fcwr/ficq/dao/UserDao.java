package com.fcwr.ficq.dao;

import com.fcwr.ficq.entity.User;
import com.fcwr.ficq.utils.JdbcUtil;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public class UserDao {
    private static final JdbcTemplate jdbcTemplate=new JdbcTemplate(JdbcUtil.getDs());
    private static Boolean checkName(String username){
        return jdbcTemplate.query("select username from user where username=?",new BeanPropertyRowMapper<>(User.class),username).isEmpty();
    }
    public static void register(String username, String password){
        if (!checkName(username)) return;
        jdbcTemplate.update("insert into user values(null,?,?,default,now(),now(),false)", username, password);
    }
    public static Boolean updateUsername(String oldusername,String username){
        if (checkName(username)) return null;
        return jdbcTemplate.update("update user set username=? where username=?",username,oldusername)>0;
    }
    public static void updateLogin(String username){
        if (checkName(username)) return;
        jdbcTemplate.update("update user set login_status=1,last_login_date=now() where username=?", username);
    }
    public static Boolean updateExit(String username){
        if (checkName(username)) return null;
        return jdbcTemplate.update("update user set login_status=0 where username=?",username)>0;
    }
    public static Boolean updatePassword(String username,String password){
        if (checkName(username)) return null;
        return jdbcTemplate.update("update user set password=? where username=?",password,username)>0;
    }
    public static Boolean updateAvatar_path(String username,String path) {
        if (checkName(username)) return null;
        return jdbcTemplate.update("update user set avatar_path=? where username=?",path,username)>0;
    }
    public static int updateAllExit() {
        return jdbcTemplate.update("update user set login_status=0");
    }
    public static List<User> queryList(){
        if (jdbcTemplate.queryForList("select id from user").isEmpty()) return null;
        return jdbcTemplate.query("select * from user order by last_login_date desc limit 0",new BeanPropertyRowMapper<>(User.class));
    }

    public static List<User> queryByStatus(int status){
        if (jdbcTemplate.queryForList("select * from user where login_status=?",status).isEmpty()) return null;
        return jdbcTemplate.query("select * from user where login_status=?",new BeanPropertyRowMapper<>(User.class),status);
    }

    public static User queryByName(String username) {
        if (checkName(username)) return null;
        return jdbcTemplate.query("select * from user where username=?",new BeanPropertyRowMapper<>(User.class),username).get(0);
    }
    public static String queryIdByName(String username){
        if (checkName(username)) return null;
        return jdbcTemplate.query("select id from user where username=?",new BeanPropertyRowMapper<>(String.class),username).get(0);
    }
    public static String queryAvatarByName(String username) {
        if (checkName(username)) return null;
        return jdbcTemplate.queryForObject("select avatar_path from user where username=?",String.class,username);
    }
}
