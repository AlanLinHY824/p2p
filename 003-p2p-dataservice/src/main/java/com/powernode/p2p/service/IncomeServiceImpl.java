package com.powernode.p2p.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.powernode.p2p.constants.MyConstants;
import com.powernode.p2p.mapper.BBidInfoMapper;
import com.powernode.p2p.mapper.BIncomeRecordMapper;
import com.powernode.p2p.mapper.BLoanInfoMapper;
import com.powernode.p2p.mapper.UFinanceAccountMapper;
import com.powernode.p2p.model.BBidInfo;
import com.powernode.p2p.model.BBidInfoExample;
import com.powernode.p2p.model.BIncomeRecord;
import com.powernode.p2p.model.BLoanInfo;
import com.powernode.p2p.vo.BIncomeRecordVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @Author AlanLin
 * @Description
 * @Date 2020/10/21
 */
@Service(interfaceClass = IncomeService.class,timeout = 20000,version = "1.0.0",retries = 5,weight = 5)
@Component
@Slf4j
public class IncomeServiceImpl implements IncomeService {

    @Autowired
    private BIncomeRecordMapper incomeRecordMapper;

    @Autowired
    private BLoanInfoMapper loanInfoMapper;

    @Autowired
    private BBidInfoMapper bidInfoMapper;

    @Autowired
    private UFinanceAccountMapper financeAccountMapper;

    @Override
    public List<BIncomeRecordVo> queryRecentIncomeRecord(Integer id) {
        return incomeRecordMapper.selectRecentIncomeRecord(id, MyConstants.RECENT_INCOME_COUNT);
    }

    @Override
    @Transactional()
    public void generatePlan() {
        log.info("开始生成收益计划");
        List<BLoanInfo> loanInfos = loanInfoMapper.selectLoanInfoByStatus(1);
        for (BLoanInfo loanInfo : loanInfos) {
            BBidInfoExample bBidInfoExample = new BBidInfoExample();
            bBidInfoExample.createCriteria().andLoanIdEqualTo(loanInfo.getId());
            List<BBidInfo> bBidInfos = bidInfoMapper.selectByExample(bBidInfoExample);
            for (BBidInfo bBidInfo : bBidInfos) {
                BIncomeRecord bIncomeRecord = new BIncomeRecord();
                bIncomeRecord.setBidId(bBidInfo.getId());
                bIncomeRecord.setBidMoney(bBidInfo.getBidMoney());
                int productType = loanInfo.getProductType();
                if (productType==0){
                    bIncomeRecord.setIncomeDate(DateUtils.addDays(loanInfo.getProductFullTime(),3+loanInfo.getCycle()));
                    bIncomeRecord.setIncomeMoney(Math.round(loanInfo.getRate()/100/365*loanInfo.getCycle()*bBidInfo.getBidMoney()*100)/100d);
                }else {
                    bIncomeRecord.setIncomeDate(DateUtils.addDays(loanInfo.getProductFullTime(),3+loanInfo.getCycle()*30));
                    bIncomeRecord.setIncomeMoney(Math.round(loanInfo.getRate()/100/365*loanInfo.getCycle()*30*bBidInfo.getBidMoney()*100)/100d);
                }
                bIncomeRecord.setIncomeStatus(0);
                bIncomeRecord.setUid(bBidInfo.getUid());
                bIncomeRecord.setLoanId(bBidInfo.getLoanId());
                incomeRecordMapper.insertSelective(bIncomeRecord);
            }
            loanInfo.setProductStatus(2);
            loanInfoMapper.updateByPrimaryKeySelective(loanInfo);
        }
        log.info("收益计划生成完毕");
    }

    @Override
    @Transactional
    public void generatePayBack() {
        log.info("开始返还收益");
        List<BIncomeRecord> bIncomeRecords = incomeRecordMapper.selectIncomeByDateAndStatus(new Date(), 0);
        for (BIncomeRecord bIncomeRecord : bIncomeRecords) {
            financeAccountMapper.updateMoneyByUid(bIncomeRecord.getUid(),bIncomeRecord.getIncomeMoney()+bIncomeRecord.getBidMoney());
            bIncomeRecord.setIncomeStatus(1);
            incomeRecordMapper.updateByPrimaryKeySelective(bIncomeRecord);
        }
        log.info("收益返还结束");
    }
}
