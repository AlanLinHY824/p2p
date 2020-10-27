package com.powernode.p2p.service;

import com.powernode.p2p.exception.ResultException;
import com.powernode.p2p.myutils.PageModel;
import com.powernode.p2p.vo.BBidInfoVo;

import java.util.List;

/**
 * 投资记录服务
 * @Author AlanLin
 * @Description
 * @Date 2020/10/12
 */
public interface BidService {
    /**
     * 查询投资记录总额
     * @return
     */
    Double queryTotalDealAmount();

    void buyLoan(Integer id, Integer loanId, Double bidMoney) throws ResultException;

    List<BBidInfoVo> queryBidByLoanId(Integer loanId, PageModel pageModel);

    Integer queryBidCountByLoanId(Integer loanId);

    List<BBidInfoVo> queryRecentBidRecord(Integer id);
}
