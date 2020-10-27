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
    MESSAGECODE_EXPIRE("验证码已过期",1003),
    MESSAGECODE_ERROR("验证码错误",1004),
    MESSAGECODE_SEND_ERROR("短信发送失败",1005),
    EXTERNAL_CALL_ERROR("外部服务调用失败",1006),
    ID_INCONSISTENT("身份验证未通过，若确认信息填写无误，请咨询公安户籍部门，谢谢！",1007),
    USER_NOT_FOUND("用户不存在",1008),
    PASSWORD_ERRO("登录密码错误",1009),
    NOR_LOGGED("对不起，请登录后再试",1010),
    LEFTPRODUCTMONEY_NOT_ENOUGH("对不起，已售罄，请挑选其他商品产品",1011),
    LOAN_NOT_FOUND("对不起，未找到该产品",1012),
    AVAILABLEMONEY_NOT_ENOUGH("对不起，您的余额不足",1013),
    ACCOUNT_ERROR("账户更新异常，投资失败，请稍后再试",1014),
    BID_ERROR("交易信息建立失败，请稍后再试！",1015),
    LOAN_STATUS_ERROR("产品状态修改失败，请稍后再试！",1016),
    ORDER_ADD_ERROR("订单创建失败，请稍后再试！",1017),
    PAY_STATUS_QUERY_FAIL("支付状态查询失败",1018),
    RECHARGE_FAIL("尝试充值失败，等待定时器重试",1019),
    RECHARGE_UPDATE_ACCOUNT_FAIL("充值时账户余额修改失败，等待定时器重试",1020),
    CONNECT_FAIL("通信失败",1021),
    INTERNAL_ERRO("服务器挤爆啦，请稍后再试！",500);

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

    ResultEnum() {
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
