package com.fcwr.ficq.entity;

public class Friend {
    private Integer id;

    private Integer username;

    private Integer friendname;

    private String time;

    public Friend() {
    }

    public Friend(Integer id, Integer username, Integer friendname, String time) {
        this.id = id;
        this.username = username;
        this.friendname = friendname;
        this.time = time;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUsername() {
        return username;
    }

    public void setUsername(Integer username) {
        this.username = username;
    }

    public Integer getFriendname() {
        return friendname;
    }

    public void setFriendname(Integer friendname) {
        this.friendname = friendname;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
