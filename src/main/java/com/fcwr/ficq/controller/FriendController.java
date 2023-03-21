package com.fcwr.ficq.controller;

import com.fcwr.ficq.dao.FriendDao;
import com.fcwr.ficq.utils.FriendUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
public class FriendController {
    @RequestMapping("/friend")
    public String friend(@RequestParam("username") String username, HttpSession session){
        return FriendUtils.getFriends(username);
    }
    @RequestMapping("/delfriend")
    public String delfriend(@RequestParam("username") String username,@RequestParam("friendname") String friendname,HttpSession session){
        return FriendDao.deleteFriend(username,friendname)?"true":"false";
    }
}