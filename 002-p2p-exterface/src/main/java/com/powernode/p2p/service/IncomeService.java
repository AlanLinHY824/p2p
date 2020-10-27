package com.powernode.p2p.service;

import com.powernode.p2p.vo.BIncomeRecordVo;

import java.util.List;

/**
 * @Author AlanLin
 * @Description
 * @Date 2020/10/21
 */
public interface IncomeService {
    List<BIncomeRecordVo> queryRecentIncomeRecord(Integer id);

    void generatePlan();

    void generatePayBack();
}
