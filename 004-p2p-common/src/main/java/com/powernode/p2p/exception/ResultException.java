package com.powernode.p2p.exception;

import com.powernode.p2p.myutils.ResultEnum;

/**
 * @Author AlanLin
 * @Description
 * @Date 2020/10/13
 */
public class ResultException extends RuntimeException{

    private ResultEnum resultEnum;
    private String message;
    private Integer code;

    public ResultException(ResultEnum resultEnum) {
        this.resultEnum = resultEnum;
        this.message=resultEnum.getMessage();
        this.code=resultEnum.getCode();
    }

    public ResultException() {
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Integer getCode() {
        return code;
    }

}
