package com.powernode.p2p.vo;

import java.io.Serializable;

/**
 * @Author AlanLin
 * @Description
 * @Date 2020/10/20
 */
public class InvestRange implements Serializable {
    private String phone;
    private Double invest;

    public InvestRange() {
    }

    public InvestRange(String phone, Double invest) {
        this.phone = phone;
        this.invest = invest;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Double getInvest() {
        return invest;
    }

    public void setInvest(Double invest) {
        this.invest = invest;
    }
}
