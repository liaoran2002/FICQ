package com.fcwr.ficq.entity;

import java.io.Serializable;

public class Message implements Serializable {

    private Integer id;

    private String from_name;

    private String to_name;

    private String type;
    private Object content;

    private String send_time;

    public Message() {
    }

    public Message(String from_name, String to_name, String type, String content) {
        this.from_name = from_name;
        this.to_name = to_name;
        this.type = type;
        this.content = content;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFrom_name() {
        return from_name;
    }

    public void setFrom_name(String from_name) {
        this.from_name = from_name;
    }

    public String getTo_name() {
        return to_name;
    }

    public void setTo_name(String to_name) {
        this.to_name = to_name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public String getSend_time() {
        return send_time;
    }

    public void setSend_time(String send_time) {
        this.send_time = send_time;
    }

}
