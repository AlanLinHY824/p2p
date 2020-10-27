package com.powernode.p2p.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.powernode.p2p.model.BLoanInfo;
import com.powernode.p2p.model.UUser;

import java.io.Serializable;
import java.util.Date;

public class BBidInfoVo implements Serializable {
    private Integer id;

    private Integer loanId;

    private Integer uid;

    private Double bidMoney;

    private Date bidTime;

    private Integer bidStatus;

    private UUser user;

    private BLoanInfo loanInfo;

    public BLoanInfo getLoanInfo() {
        return loanInfo;
    }

    public void setLoanInfo(BLoanInfo loanInfo) {
        this.loanInfo = loanInfo;
    }

    public UUser getUser() {
        return user;
    }

    public void setUser(UUser user) {
        this.user = user;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLoanId() {
        return loanId;
    }

    public void setLoanId(Integer loanId) {
        this.loanId = loanId;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Double getBidMoney() {
        return bidMoney;
    }

    public void setBidMoney(Double bidMoney) {
        this.bidMoney = bidMoney;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getBidTime() {
        return bidTime;
    }

    public void setBidTime(Date bidTime) {
        this.bidTime = bidTime;
    }

    public Integer getBidStatus() {
        return bidStatus;
    }

    public void setBidStatus(Integer bidStatus) {
        this.bidStatus = bidStatus;
    }
}