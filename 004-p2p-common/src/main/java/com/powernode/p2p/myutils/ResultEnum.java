package com.powernode.p2p.myutils;

/**
 * @Author AlanLin
 * @Description
 * @Date 2020/10/13
 */
public enum ResultEnum {
    //可能出现的结果
    SUCCESS("执行成功",200),
    NOT_FOUND("未找到数据",1001),
    PHONE_EXISTS("该手机号已被注册",1002),
    ID_INCONSISTENT("身份验证未通过",1002),
    INTERNAL_ERRO("服务器繁忙",500);

    /**
     * 结果信息
     */
    String message;
    /**
     * 状态码
     */
    Integer code;

    /**
     * 构造方法
     * @param message
     * @param code
     */
    ResultEnum(String message, Integer code) {
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
