package com.example.demo.bean;

import lombok.Data;

@Data
public class Result<T> {
    //返回结果
    private Integer code;  // 状态码，如 200 表示成功
    private String msg;    // 提示信息
    private T data;        // 返回的数据

    public static <T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.setCode(200);
        r.setMsg("success");
        r.setData(data);
        return r;
    }

    public static <T> Result<T> fail(String msg) {
        Result<T> r = new Result<>();
        r.setCode(400);
        r.setMsg(msg);
        r.setData(null);
        return r;
    }
}