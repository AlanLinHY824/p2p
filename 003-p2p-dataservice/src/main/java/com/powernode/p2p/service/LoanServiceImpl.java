package com.powernode.p2p.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.powernode.p2p.mapper.BBidInfoMapper;
import com.powernode.p2p.mapper.BLoanInfoMapper;
import com.powernode.p2p.model.BBidInfoExample;
import com.powernode.p2p.model.BLoanInfo;
import com.powernode.p2p.model.BLoanInfoExample;
import com.powernode.p2p.myutils.PageModel;
import com.powernode.p2p.vo.BBidInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

/**
 * @Author AlanLin
 * @Description
 * @Date 2020/10/12
 */
@Service(interfaceClass = LoanService.class,timeout = 20000,version = "1.0.0")
@Component
public class LoanServiceImpl implements LoanService{

    @Autowired
    BLoanInfoMapper loanInfoMapper;

    @Autowired
    BBidInfoMapper bidInfoMapper;

    @Override
    public Double queryHisAvgRate() {
        Double hisAvgRate=loanInfoMapper.selectHisAvgRate();
        DecimalFormat df1 = new DecimalFormat("0.00");
        return Double.valueOf( df1.format(hisAvgRate));
    }

    @Override
    public List<BLoanInfo> queryLoanInfoByTypeAndNum(Map condtion) {

        return loanInfoMapper.queryLoanInfoByTypeAndNum(condtion);
    }

    @Override
    public List<BLoanInfo> queryLoanInfoByType(Integer ptype, PageModel pageModel) {
        loanInfoMapper.queryLoanInfoByPage(ptype,pageModel.getCurrentPage()*pageModel.getPageSize()+1,pageModel.getPageSize());
        return  loanInfoMapper.queryLoanInfoByPage(ptype,(pageModel.getCurrentPage()-1)*pageModel.getPageSize(),pageModel.getPageSize());
    }

    @Override
    public Integer queryLoanCountByType(Integer ptype) {
        BLoanInfoExample bLoanInfoExample = new BLoanInfoExample();
        BLoanInfoExample.Criteria criteria = bLoanInfoExample.createCriteria();
        criteria.andProductTypeEqualTo(ptype);
        return loanInfoMapper.countByExample(bLoanInfoExample);
    }

    @Override
    public BLoanInfo queryLoanInfoById(Integer loanId) {
        return loanInfoMapper.selectByPrimaryKey(loanId);
    }

    @Override
    public List<BBidInfoVo> queryBidByLoanId(Integer loanId) {
        List<BBidInfoVo> bBidInfos=bidInfoMapper.selectBidByLoanId(loanId);
        return bBidInfos;
    }

    @Override
    public List<BBidInfoVo> queryBidByLoanId(Integer loanId, PageModel pageModel) {
        int currentPage=pageModel.getCurrentPage();
        int pageSize = pageModel.getPageSize();
        int start=pageSize*(currentPage-1);
        return bidInfoMapper.selectBidByLoanIdPage(loanId,start,pageSize);
    }

    @Override
    public Integer queryBidCountByLoanId(Integer loanId) {
        BBidInfoExample bBidInfoExample = new BBidInfoExample();
        BBidInfoExample.Criteria criteria = bBidInfoExample.createCriteria();
        criteria.andLoanIdEqualTo(loanId);
        return bidInfoMapper.countByExample(bBidInfoExample);
    }
}
