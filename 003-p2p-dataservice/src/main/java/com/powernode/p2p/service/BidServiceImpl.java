package com.powernode.p2p.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.powernode.p2p.constants.MyConstants;
import com.powernode.p2p.exception.ResultException;
import com.powernode.p2p.mapper.BBidInfoMapper;
import com.powernode.p2p.mapper.BLoanInfoMapper;
import com.powernode.p2p.mapper.UFinanceAccountMapper;
import com.powernode.p2p.model.BBidInfo;
import com.powernode.p2p.model.BBidInfoExample;
import com.powernode.p2p.model.BLoanInfo;
import com.powernode.p2p.myutils.PageModel;
import com.powernode.p2p.myutils.ResultEnum;
import com.powernode.p2p.vo.BBidInfoVo;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @Author AlanLin
 * @Description
 * @Date 2020/10/12
 */
@Service(interfaceClass = BidService.class,timeout = 20000,version = "1.0.0")
@Component
public class BidServiceImpl implements BidService {

    @Autowired
    private BBidInfoMapper bidInfoMapper;

    @Autowired
    private BLoanInfoMapper loanInfoMapper;

    @Autowired
    private UFinanceAccountMapper financeAccountMapper;

    @Override
    public Double queryTotalDealAmount() {
        return bidInfoMapper.selectTotalDealAmount();
    }

    @Override
    @Transactional
    public void buyLoan(Integer id, Integer loanId, Double bidMoney) throws ResultException {
        int count= 0;
        int retry= 0;
        //修改剩余可投金额
        BLoanInfo bLoanInfo =null;
        while (count!=1){
            if (retry==3){
                throw new ResultException(ResultEnum.INTERNAL_ERRO);
            }
            retry+=1;
            bLoanInfo = loanInfoMapper.selectByPrimaryKey(loanId);
            //虽然概率很低，但还是要判断下产品是否存在
            if (!ObjectUtils.allNotNull(bLoanInfo)){
                throw new ResultException(ResultEnum.LOAN_NOT_FOUND);
            }
            if (bLoanInfo.getLeftProductMoney()-bidMoney>=0){
                //如采用本方式，需要将数据库的隔离级别设置为读已提交，否则无法读取到最新的version
//                synchronized (this){
//                    BLoanInfo bLoanInfoLater = loanInfoMapper.selectByPrimaryKey(loanId);
//                    if (bLoanInfo.getVersion().equals(bLoanInfoLater.getVersion())&&bLoanInfoLater.getLeftProductMoney()-bidMoney>=0){
//                        bLoanInfoLater.setLeftProductMoney(bLoanInfoLater.getLeftProductMoney()-bidMoney);
//                        bLoanInfoLater.setVersion(bLoanInfoLater.getVersion()+1);
//                        loanInfoMapper.updateByPrimaryKey(bLoanInfoLater);
//                    }
//                }
                try {
                    //不需要设置数据库的隔离级别为读已提交，因为不存在查询出的version的对比，在更新语句执行的时候等式左边拿到的是最新的version
                    count = loanInfoMapper.updateLeftMoney(loanId,bLoanInfo.getLeftProductMoney()-bidMoney,bLoanInfo.getVersion());
                    //这种情况下并不需要版本号 sql:update b_loan_info set left_product_money=left_product_money-#{bidMoney} where id=#{loanId} and left_product_money-#{bidMoney}>0
                    //因为在执行SQL语句时直接判断left_product_money-#{bidMoney}>0，一条DDL语句本身是具有原子性的，不会产生并发问题，在判断余额时不会存在有误的情况
//                    count = loanInfoMapper.updateLeftMoneyRe(loanId,bidMoney,bLoanInfo.getVersion());
                } catch (Exception e) {
                    throw new ResultException(ResultEnum.INTERNAL_ERRO);
                }
            }else {
                throw new ResultException(ResultEnum.LEFTPRODUCTMONEY_NOT_ENOUGH);
            }
        }
        //修改账户余额
        count = financeAccountMapper.updateAccount(bidMoney,id);
        if (count!=1){
            throw new ResultException(ResultEnum.ACCOUNT_ERROR);
        }
        //添加交易记录
        BBidInfo bBidInfo = new BBidInfo();
        bBidInfo.setUid(id);
        bBidInfo.setLoanId(loanId);
        bBidInfo.setBidTime(new Date());
        bBidInfo.setBidMoney(bidMoney);
        bBidInfo.setBidStatus(1);
        try {
            count = bidInfoMapper.insertSelective(bBidInfo);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResultException(ResultEnum.BID_ERROR);
        }
        if (count!=1){
            throw new ResultException(ResultEnum.BID_ERROR);
        }
        //修改商品状态，上面的语句一旦修改成功，该产品就会被锁定，无法修改，所以可以直接使用上面更改前查询的结果
        if (bLoanInfo.getLeftProductMoney()-bidMoney==0){
            BLoanInfo bLoanInfo1 = new BLoanInfo();
            bLoanInfo1.setId(loanId);
            bLoanInfo1.setProductStatus(1);
            bLoanInfo1.setProductFullTime(new Date());
            try {
                count=loanInfoMapper.updateByPrimaryKey(bLoanInfo1);
            } catch (Exception e) {
                e.printStackTrace();
                throw new ResultException(ResultEnum.LOAN_STATUS_ERROR);
            }
        }
        if (count!=1){
            throw new ResultException(ResultEnum.LOAN_STATUS_ERROR);
        }
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

    @Override
    public List<BBidInfoVo> queryRecentBidRecord(Integer id) {
        return bidInfoMapper.selectRecentBidRecord(id, MyConstants.RECENT_BID_COUNT);
    }
}
