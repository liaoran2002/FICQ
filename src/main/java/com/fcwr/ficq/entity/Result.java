package com.fcwr.ficq.entity;

public class Result {
    private Boolean result_code;
    private String message;

    public Result() {
    }

    public Result(Boolean result_code, String message) {
        this.result_code = result_code;
        this.message = message;
    }

    public Boolean getResult_code() {
        return result_code;
    }

    public void setResult_code(Boolean result_code) {
        this.result_code = result_code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
