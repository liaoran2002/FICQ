package com.fcwr.ficq.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class OtherController {
    @RequestMapping("/logo")
    public String logo() {
        return "/html/logo.png";
    }

    /*
    //html文件在static文件夹中的转发
    @RequestMapping("/index")
    public void index(HttpServletRequest request, HttpServletResponse response){
        try {
            request.getRequestDispatcher("index.html").forward(request, response);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @RequestMapping("/login")
    public void login(HttpSession session, HttpServletRequest request, HttpServletResponse response){
        try {
            request.getRequestDispatcher("login.html").forward(request, response);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @RequestMapping("/register")
    public void register(HttpServletRequest request, HttpServletResponse response){
        try {
            request.getRequestDispatcher("register.html").forward(request, response);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @RequestMapping("/main")
    public void main(HttpServletRequest request, HttpServletResponse response){
        try {
            request.getRequestDispatcher("main.html").forward(request, response);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    */
    //html文件在templaces文件夹中的转发
    @RequestMapping("/index")
    public String index() {
        return "index";
    }

    @RequestMapping("/login")
    public String login() {
        return "login";
    }

//    @RequestMapping("/register")
//    public String register() {
//        return "register";
//    }

    @RequestMapping("/main")
    public String main() {
        return "main";
    }
}
