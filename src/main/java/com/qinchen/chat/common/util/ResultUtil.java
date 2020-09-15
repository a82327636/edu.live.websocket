package com.qinchen.chat.common.util;


import java.io.Serializable;

//@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultUtil<T> implements Serializable {

    private static final long serialVersionUID = 1L;
    private String code;
    private String msg;
    private T data;

    private ResultUtil(String code) {
        this.code = code;
    }

    private ResultUtil(String code, T data) {
        this.code = code;
        this.data = data;
    }

    private ResultUtil(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    private ResultUtil(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public T getData() {
        return data;
    }

    public String getMsg() {
        return msg;
    }

    public static <T> ResultUtil<T> success(T data) {
        return new ResultUtil<T>("1", "success", data);
    }
    public static <T> ResultUtil<T> success() {
        return new ResultUtil<T>("1", "success", null);
    }
    public static <T> ResultUtil<T> error() {
        return new ResultUtil<T>("0", "error");
    }
    public static <T> ResultUtil<T> error(int code, String msg) {
        return new ResultUtil<T>(code+"", msg);
    }
    public static <T> ResultUtil<T> error(String code, String msg) {
        return new ResultUtil<T>(code, msg);
    }
    public static <T> ResultUtil<T> error(String code, String msg,T data) {
        return new ResultUtil<T>(code, msg,data);
    }
}