package com.powernode.p2p.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.powernode.p2p.mapper.BBidInfoMapper;
import com.powernode.p2p.mapper.BLoanInfoMapper;
import com.powernode.p2p.mapper.UFinanceAccountMapper;
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
@Service(interfaceClass = LoanService.class,timeout = 20000,version = "1.0.0",retries = 5,weight = 5)
@Component
public class
LoanServiceImpl implements LoanService{

    @Autowired
    BLoanInfoMapper loanInfoMapper;

    @Autowired
    BBidInfoMapper bidInfoMapper;

    @Autowired
    UFinanceAccountMapper financeAccountMapper;

    @Override
    public Double queryHisAvgRate() {
        System.out.println("服务20881");
        Double hisAvgRate=loanInfoMapper.selectHisAvgRate();
        DecimalFormat df1 = new DecimalFormat("0.00");
        return Double.valueOf( df1.format(hisAvgRate));
    }

    @Override
    public List<BLoanInfo> queryLoanInfoByTypeAndNum(Map condtion) {
        System.out.println("服务20881");
        return loanInfoMapper.queryLoanInfoByTypeAndNum(condtion);
    }

    @Override
    public List<BLoanInfo> queryLoanInfoByType(Integer ptype, PageModel pageModel) {
        System.out.println("服务20881");
        loanInfoMapper.queryLoanInfoByPage(ptype,pageModel.getCurrentPage()*pageModel.getPageSize()+1,pageModel.getPageSize());
        return  loanInfoMapper.queryLoanInfoByPage(ptype,(pageModel.getCurrentPage()-1)*pageModel.getPageSize(),pageModel.getPageSize());
    }

    @Override
    public Integer queryLoanCountByType(Integer ptype) {
        System.out.println("服务20881");
        BLoanInfoExample bLoanInfoExample = new BLoanInfoExample();
        BLoanInfoExample.Criteria criteria = bLoanInfoExample.createCriteria();
        criteria.andProductTypeEqualTo(ptype);
        return loanInfoMapper.countByExample(bLoanInfoExample);
    }

    @Override
    public BLoanInfo queryLoanInfoById(Integer loanId) {
        System.out.println("服务20881");
        return loanInfoMapper.selectByPrimaryKey(loanId);
    }

    @Override
    public List<BBidInfoVo> queryBidByLoanId(Integer loanId) {
        System.out.println("服务20881");
        List<BBidInfoVo> bBidInfos=bidInfoMapper.selectBidByLoanId(loanId);
        return bBidInfos;
    }


}
