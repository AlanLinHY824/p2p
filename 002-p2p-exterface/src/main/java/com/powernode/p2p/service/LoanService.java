package com.powernode.p2p.service;

import com.powernode.p2p.model.BLoanInfo;
import com.powernode.p2p.myutils.PageModel;
import com.powernode.p2p.vo.BBidInfoVo;

import java.util.List;
import java.util.Map;

/**
 * @Author AlanLin
 * @Description
 * @Date 2020/10/12
 */
public interface LoanService {
    /**
     * 查询历史平均年化利率
     * @return
     */
    Double queryHisAvgRate();

    /**
     * 查询各类产品信息
     * @param condition 查询条件
     * @return
     */
    List<BLoanInfo> queryLoanInfoByTypeAndNum(Map condition);

    List<BLoanInfo> queryLoanInfoByType(Integer ptype, PageModel pageModel);

    Integer queryLoanCountByType(Integer ptype);

    BLoanInfo queryLoanInfoById(Integer loanId);

    List<BBidInfoVo> queryBidByLoanId(Integer loanId);

    Integer queryBidCountByLoanId(Integer loanId);

    List<BBidInfoVo> queryBidByLoanId(Integer loanId, PageModel pageModel);
}
