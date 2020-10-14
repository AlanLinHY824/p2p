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
    private Integer cdoe;
    private String messqge;
    private Object result;

    private Result(Integer cdoe, String messqge, Object result) {
        this.cdoe = cdoe;
        this.messqge = messqge;
        this.result = result;
    }

    private Result(Integer cdoe, String messqge) {
        this.cdoe = cdoe;
        this.messqge = messqge;
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

    public Integer getCdoe() {
        return cdoe;
    }

    public String getMessqge() {
        return messqge;
    }

    public Object getResult() {
        return result;
    }

    public void setCdoe(Integer cdoe) {
        this.cdoe = cdoe;
    }

    public void setMessqge(String messqge) {
        this.messqge = messqge;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
