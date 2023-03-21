package com.fcwr.ficq.entity;

public class ResultMessage  {
    private Object message;
    private String send_time;

    public ResultMessage() {
    }

    public ResultMessage(Object message, String send_time) {
        this.send_time = send_time;
        this.message = message;
    }
    public Object getMessage() {
        return message;
    }
    public void setMessage(Object message) {
        this.message = message;
    }
    public String getSend_time() {
        return send_time;
    }
    public void setSend_time(String send_time) {
        this.send_time = send_time;
    }

}
