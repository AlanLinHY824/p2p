package com.powernode.p2p.myutils;

import com.powernode.p2p.exception.ResultException;

/**
 * @Author AlanLin
 * @Description
 * @Date 2020/10/14
 */
public class Result {
    /**
     * JSON返回结果
     */
    private Integer code;
    private String message;
    private Object result;

    private Result(Integer code, String message, Object result) {
        this.code = code;
        this.message = message;
        this.result = result;
    }

    private Result(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static Result SUCCESS(Object result){
        return new Result(200,"SUCCESS",result);
    }

    public static Result SUCCESS(){
        return new Result(200,"SUCCESS");
    }

    public static Result FAIL(ResultException resultException){
        return new Result(resultException.getCode(),resultException.getMessage());
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Object getResult() {
        return result;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
