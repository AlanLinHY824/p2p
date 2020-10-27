package com.powernode.p2p.service;

import com.powernode.p2p.model.BRechargeRecord;

import java.util.List;

/**
 * @Author AlanLin
 * @Description
 * @Date 2020/10/21
 */
public interface RechargeService {

    List<BRechargeRecord> queryRecentRechargeRecord(Integer id);

    BRechargeRecord makeOrder(Integer uid, Double rechargeMoney);

    BRechargeRecord queryRechargeRecordByOrderId(String rechargeNo);

    void recharge(Integer userId, String rechargeNo, Double rechargeMoney);

    void closeOrder(String out_trade_no);

    List<BRechargeRecord> queryRechargeRecordByStatus(String rechargeStatus);
}
