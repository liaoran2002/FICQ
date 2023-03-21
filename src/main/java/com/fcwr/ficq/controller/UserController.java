package com.fcwr.ficq.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fcwr.ficq.dao.UserDao;
import com.fcwr.ficq.entity.Result;
import com.fcwr.ficq.entity.User;
import com.fcwr.ficq.utils.Base64Utils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.Objects;
import java.util.UUID;

@RestController
public class UserController {
    private final String usernameC=Base64Utils.encode("username").replace("=","");
    private final String passwordC=Base64Utils.encode("password").replace("=","");
    @RequestMapping("/loginSerC")
    public String loginSerC(HttpSession session, HttpServletRequest request) {
        String username = usernameC;
        String password = passwordC;
        Cookie[] cookies = request.getCookies();
        if (cookies==null){
            return "false";
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(username)) {
                username = Base64Utils.decode(cookie.getValue());
            } else if (cookie.getName().equals(password)) {
                password = Base64Utils.decode(cookie.getValue());
            }
        }
        if (!username.equals(usernameC) && !password.equals(passwordC)) {
            User user = UserDao.queryByName(username);
            if (user.getPassword().equals(password)) {
                session.setAttribute("user", username);
                return "true";
            }
        }
        return "false";
    }

    @RequestMapping("/loginSer")
    public String loginSer(@RequestParam("username") String username, @RequestParam("password") String
            password, HttpSession session, HttpServletResponse response) {
        Result result = new Result();
        ObjectMapper mapper = new ObjectMapper();
        String s = "{'message':'出现错误,请重试','result_code':false}";
        User user = UserDao.queryByName(username);
        try {
            if (username.equals("") || password.equals("")) {
                result.setMessage("用户名或密码不能为空");
                result.setResult_code(false);
            } else if (user == null) {
                result.setMessage("用户名不存在");
                result.setResult_code(false);
            } else if (user.getPassword().equals(password)) {
                session.setAttribute("user", username);
                result.setMessage("登录成功");
                result.setResult_code(true);
                Cookie usernameCookie = new Cookie(usernameC, Base64Utils.encode(username));
                Cookie passwordCookie = new Cookie(passwordC, Base64Utils.encode(password));
                usernameCookie.setMaxAge(60 * 60 * 72);
                passwordCookie.setMaxAge(60 * 60 * 72);
                response.addCookie(usernameCookie);
                response.addCookie(passwordCookie);
            } else {
                result.setMessage("用户名或密码错误");
                result.setResult_code(false);
            }
            s = mapper.writeValueAsString(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    @RequestMapping("/loginTest")
    public String loginTest(HttpServletResponse response) {
        Cookie usernameCookie = new Cookie(usernameC, "");
        Cookie passwordCookie = new Cookie(passwordC, "");
        usernameCookie.setMaxAge(1);
        passwordCookie.setMaxAge(1);
        response.addCookie(usernameCookie);
        response.addCookie(passwordCookie);
        return "ok";
    }


    @RequestMapping("/regSer")
    public String regSer(@RequestParam("username") String username,@RequestParam("password") String password,@RequestParam("repassword") String repassword, HttpSession session,HttpServletResponse response) {
        Result result = new Result();
        ObjectMapper mapper = new ObjectMapper();
        String s = "{'message':'出现错误,请重试','result_code':false}";
        if (!password.equals(repassword)){
            return "{'message':'两次输入的密码不相同','result_code':false}";
        }
        User user = UserDao.queryByName(username);
        try {
            if (username.equals("") || password.equals("")) {
                result.setMessage("用户名或密码不能为空");
                result.setResult_code(false);
            } else if (user != null) {
                result.setMessage("用户已存在");
                result.setResult_code(false);
            } else {
                UserDao.register(username, password);
                session.setAttribute("user", username);
                result.setMessage("注册成功");
                result.setResult_code(true);
                Cookie usernameCookie = new Cookie(usernameC, Base64Utils.encode(username));
                Cookie passwordCookie = new Cookie(passwordC, Base64Utils.encode(password));
                usernameCookie.setMaxAge(60 * 60 * 72);
                passwordCookie.setMaxAge(60 * 60 * 72);
                response.addCookie(usernameCookie);
                response.addCookie(passwordCookie);
            }
            s = mapper.writeValueAsString(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }


    @RequestMapping("/getUsername")
    public String getUsername(HttpSession session) {
        return (String) session.getAttribute("user");
    }

    @RequestMapping("/getAvatar")
    public String getAvatar(HttpSession session) {
        return UserDao.queryAvatarByName((String) session.getAttribute("user"));
    }

    @Value("${file.avatarPath}")
    private String avatar;
    @Value("${file.upAvatarFolder}")
    private String upAvatar;
    @Value("${file.defaultAvatar}")
    private String defaultPng;

    @PostMapping("/upAvatar")
    public String upload(MultipartFile file, HttpSession session) {
        Result result = new Result();
        ObjectMapper mapper = new ObjectMapper();
        String s = "{'message':'出现错误,请重试','result_code':false}";
        if (!file.isEmpty()) {
            try {
                String oldFileName = file.getOriginalFilename();
                assert oldFileName != null;
                String ext = oldFileName.substring(oldFileName.lastIndexOf(".") + 1);
                if (ext.matches("(jpg|png|webp|gif|svg)")) {
                    String uuid = UUID.randomUUID().toString().replace("-", "");
                    String path = upAvatar + uuid + "." + ext;
                    file.transferTo(new File(path));
                    String oldPath = Objects.requireNonNull(UserDao.queryAvatarByName((String) session.getAttribute("user"))).replace(avatar, "");
                    UserDao.updateAvatar_path((String) session.getAttribute("user"), avatar + uuid + "." + ext);
                    if (!Objects.equals(oldPath, defaultPng)) {
                        if (new File(upAvatar + oldPath).delete()) System.out.println(oldPath + "已删除");
                    }
                    result.setResult_code(true);
                    result.setMessage(UserDao.queryAvatarByName((String) session.getAttribute("user")));
                    s = mapper.writeValueAsString(result);
                    return s;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return s;
    }


}


