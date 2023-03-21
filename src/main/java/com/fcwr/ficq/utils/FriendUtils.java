package com.fcwr.ficq.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fcwr.ficq.dao.FriendDao;

public class FriendUtils {
    public static String getFriends(String username) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(FriendDao.queryFriend(username));
        }catch (Exception e){
            e.printStackTrace();
        }
        return  null;
    }
}
