package com.fcwr.ficq.entity;

import java.io.Serializable;

public class User implements Serializable {

    private Integer id;
    private String username;

    private String password;

    private String reg_date;

    private String last_login_date;

    private String avatar_path;

    private Boolean login_status;
    private Boolean admin;

    public User(Integer id, String username, String password, String reg_date, String last_login_date, String avatar_path, Boolean login_status, Boolean admin) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.reg_date = reg_date;
        this.last_login_date = last_login_date;
        this.avatar_path = avatar_path;
        this.login_status = login_status;
        this.admin = admin;
    }

    public User() {
    }

    public String getAvatar_path() {
        return avatar_path;
    }

    public void setAvatar_path(String avatar_path) {
        this.avatar_path = avatar_path;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getReg_date() {
        return reg_date;
    }

    public void setReg_date(String reg_date) {
        this.reg_date = reg_date;
    }

    public String getLast_login_date() {
        return last_login_date;
    }

    public void setLast_login_date(String last_login_date) {
        this.last_login_date = last_login_date;
    }

    public Boolean getLogin_status() {
        return login_status;
    }

    public void setLogin_status(Boolean login_status) {
        this.login_status = login_status;
    }
}
