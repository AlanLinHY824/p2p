package com.powernode.p2p.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.powernode.p2p.constants.MyConstants;
import com.powernode.p2p.exception.ResultException;
import com.powernode.p2p.mapper.BRechargeRecordMapper;
import com.powernode.p2p.mapper.UFinanceAccountMapper;
import com.powernode.p2p.model.BRechargeRecord;
import com.powernode.p2p.model.BRechargeRecordExample;
import com.powernode.p2p.myutils.ResultEnum;
import org.apache.commons.lang3.StringUtils;
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
@Service(interfaceClass = RechargeService.class,timeout = 20000,version = "1.0.0",retries = 5,weight = 5)
@Component
public class RechargeServiceImpl implements RechargeService {

    @Autowired
    private BRechargeRecordMapper rechargeRecordMapper;

    @Autowired
    private UFinanceAccountMapper financeAccountMapper;

    @Autowired
    private RedisService redisService;

    @Override
    public List<BRechargeRecord> queryRecentRechargeRecord(Integer id) {
        return rechargeRecordMapper.selectRecentRechargeRecord(id, MyConstants.RECENT_RECHARGE_COUNT);
    }

    @Override
    public BRechargeRecord makeOrder(Integer uid, Double rechargeMoney) {
        Date date = new Date();
        BRechargeRecord bRechargeRecord = new BRechargeRecord();
        bRechargeRecord.setUid(uid);
        bRechargeRecord.setRechargeTime(date);
        bRechargeRecord.setRechargeStatus("0");
        bRechargeRecord.setRechargeMoney(rechargeMoney);
        bRechargeRecord.setRechargeNo(redisService.generateId(MyConstants.ORDERSEQUENCE, uid));
        bRechargeRecord.setRechargeDesc("支付购买理财产品");
        int count = rechargeRecordMapper.insertSelective(bRechargeRecord);
        if (count<1){
            throw new ResultException(ResultEnum.ORDER_ADD_ERROR);
        }
        return bRechargeRecord;
    }

    @Override
    public BRechargeRecord queryRechargeRecordByOrderId(String rechargeNo) {
        BRechargeRecordExample bRechargeRecordExample = new BRechargeRecordExample();
        bRechargeRecordExample.createCriteria().andRechargeNoEqualTo(rechargeNo);
        List<BRechargeRecord> rechargeRecords = rechargeRecordMapper.selectByExample(bRechargeRecordExample);
        if (rechargeRecords==null||rechargeRecords.size()==0){
            throw new ResultException(ResultEnum.INTERNAL_ERRO);
        }
        return rechargeRecords.get(0);
    }

    @Override
    @Transactional
    public void recharge(Integer userId, String rechargeNo, Double rechargeMoney) {
        int count = rechargeRecordMapper.updateStatusByRechargeNo(rechargeNo, MyConstants.RECHARGE_STATUS_SUCCESS);
        if (count==0){
            BRechargeRecordExample bRechargeRecordExample = new BRechargeRecordExample();
            bRechargeRecordExample.createCriteria().andRechargeNoEqualTo(rechargeNo);
            List<BRechargeRecord> rechargeRecords = rechargeRecordMapper.selectByExample(bRechargeRecordExample);
            //查询订单状态，看是否已经被修改
            BRechargeRecord rechargeRecord= rechargeRecords.get(0);
            if (!StringUtils.equals(rechargeRecord.getRechargeStatus(), "1")){
                throw new ResultException(ResultEnum.RECHARGE_FAIL);
            }
            return;
        }
        count = financeAccountMapper.updateMoneyByUid(userId, rechargeMoney);
        if (count!=1){
            throw new ResultException(ResultEnum.RECHARGE_FAIL);
        }
    }

    @Override
    public void closeOrder(String rechargeNo) {
        rechargeRecordMapper.updateStatusByRechargeNo(rechargeNo, MyConstants.RECHARGE_STATUS_FAIL);
    }

    @Override
    public List<BRechargeRecord> queryRechargeRecordByStatus(String rechargeStatus) {
        BRechargeRecordExample bRechargeRecordExample = new BRechargeRecordExample();
        bRechargeRecordExample.createCriteria().andRechargeStatusEqualTo(rechargeStatus);
        List<BRechargeRecord> rechargeRecords = rechargeRecordMapper.selectByExample(bRechargeRecordExample);
        return rechargeRecords;
    }
}
