package com.powernode.p2p.scheduletask;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.powernode.p2p.constants.MyConstants;
import com.powernode.p2p.exception.ResultException;
import com.powernode.p2p.model.BRechargeRecord;
import com.powernode.p2p.myutils.HttpClientUtils;
import com.powernode.p2p.myutils.ResultEnum;
import com.powernode.p2p.service.IncomeService;
import com.powernode.p2p.service.RechargeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author AlanLin
 * @Description
 * @Date 2020/10/22
 */
@Component
@Slf4j
public class ScheduleTask {

    @Reference(interfaceClass = IncomeService.class,timeout = 20000,version = "1.0.0",check = false)
    IncomeService incomeService;

    @Reference(interfaceClass = RechargeService.class,timeout = 20000,version = "1.0.0",check = false)
    RechargeService rechargeService;

    //每天凌晨3点15分执行
//    @Scheduled(cron = "0 2 21 ? * *")
    @Scheduled(cron = "0 15 3 ? * *")
    public void generatePlan(){
        log.info("开始生成收益计划");
        incomeService.generatePlan();
        log.info("收益计划生成完毕");
    }

    //每天凌晨3点15分执行

//    @Scheduled(cron = "0 3 21 ? * *")
    @Scheduled(cron = "0 15 4 ? * *")
    public void generatePayBack(){
        log.info("开始返还收益");
        incomeService.generatePayBack();
        log.info("收益返还结束");
    }

    @Scheduled(cron = "0 0/10 * ? * *")
//    @Scheduled(cron = "0/10 * * ? * *")
    public void checkRechargeOrder(){
        log.info("支付宝订单查询开始");
        List<BRechargeRecord> rechargeRecords= rechargeService.queryRechargeRecordByStatus(MyConstants.RECHARGE_STATUS_PAYING);
        if (rechargeRecords==null||rechargeRecords.size()==0){
            return;
        }
        for (BRechargeRecord rechargeRecord : rechargeRecords) {
            String out_trade_no = rechargeRecord.getRechargeNo();
            Map<String,Object> paramMap=new HashMap<>();
            paramMap.put("out_trade_no",out_trade_no);
            String result=null;
            try {
                result = HttpClientUtils.doPost("http://localhost:8092/007-p2p-pay/pay/aliPayQuery", paramMap);
            } catch (Exception e) {
                throw new ResultException(ResultEnum.PAY_STATUS_QUERY_FAIL);
            }
            JSONObject responseBody = JSONObject.parseObject(result).getJSONObject("alipay_trade_query_response");
            String code = responseBody.getString("code");
            String tradeStatus = responseBody.getString("trade_status");
            //与支付宝通讯失败，不做操作，等待定时器继续与支付宝通信
            if (!StringUtils.equals("10000", code)){
                continue;
            }
            //订单关闭，修改订单状态为2，对于用户来说不重要，不需抛出异常，等待定时器关闭订单即可
            if (StringUtils.equals(tradeStatus,"TRADE_CLOSED")){
                rechargeService.closeOrder(out_trade_no);
                log.error("订单号"+out_trade_no+"超时订单关闭");
            }
            //扣款成功，修改订单状态为1，并增加用户的账户余额
            if (StringUtils.equals(tradeStatus,"TRADE_SUCCESS")){
                try {
                    rechargeService.recharge(rechargeRecord.getUid(),out_trade_no,rechargeRecord.getRechargeMoney());
                    log.info(rechargeRecord.getId()+"充值成功");
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("订单号"+out_trade_no+e.getMessage());
                }
            }
        }
        log.info("支付宝订单查询结束");
    }

    @Scheduled(cron = "0 0/10 * ? * *")
//    @Scheduled(cron = "0/10 * * ? * *")
    public void checkWXOrder(){
        log.info("微信订单查询开始");
        List<BRechargeRecord> rechargeRecords= rechargeService.queryRechargeRecordByStatus(MyConstants.RECHARGE_STATUS_PAYING);
        if (rechargeRecords==null||rechargeRecords.size()==0){
            return;
        }
        for (BRechargeRecord rechargeRecord : rechargeRecords) {
            String out_trade_no = rechargeRecord.getRechargeNo();
            Map<String,Object> paramMap=new HashMap<>();
            paramMap.put("out_trade_no",out_trade_no);
            String result=null;
            try {
                result = HttpClientUtils.doPost("http://localhost:8092/007-p2p-pay/pay/aliPayQuery", paramMap);
            } catch (Exception e) {
                throw new ResultException(ResultEnum.PAY_STATUS_QUERY_FAIL);
            }
            JSONObject responseBody = JSONObject.parseObject(result);
            String return_code = responseBody.getString("return_code");
            String result_code = responseBody.getString("result_code");
            String trade_state = responseBody.getString("trade_state");
            if (!StringUtils.equals(return_code, "SUCCESS")){
                log.error("订单号"+out_trade_no+"获取微信code_url时通讯失败");
                throw new ResultException(ResultEnum.INTERNAL_ERRO);
            }

            if (!StringUtils.equals(result_code, "SUCCESS")){
                //参数错误
                log.error("订单号"+out_trade_no+"二维码传递参数错误");
                throw new ResultException(ResultEnum.INTERNAL_ERRO);
            }
            //订单关闭，修改订单状态为2，对于用户来说不重要，不需抛出异常，等待定时器关闭订单即可
            if (StringUtils.equals(trade_state,"CLOSED")){
                rechargeService.closeOrder(out_trade_no);
                log.error("订单号"+out_trade_no+"超时订单关闭");
            }
            //订单关闭，修改订单状态为2，对于用户来说不重要，不需抛出异常，等待定时器关闭订单即可
            if (StringUtils.equals(trade_state,"REVOKED")){
                rechargeService.closeOrder(out_trade_no);
                log.error("订单号"+out_trade_no+"订单已撤销");
            }
            //订单关闭，修改订单状态为2，对于用户来说不重要，不需抛出异常，等待定时器关闭订单即可
            if (StringUtils.equals(trade_state,"PAYERROR")){
                rechargeService.closeOrder(out_trade_no);
                log.error("订单号"+out_trade_no+"支付失败");
            }
            //扣款成功，修改订单状态为1，并增加用户的账户余额
            if (StringUtils.equals(trade_state,"TRADE_SUCCESS")){
                try {
                    rechargeService.recharge(rechargeRecord.getUid(),out_trade_no,rechargeRecord.getRechargeMoney());
                    log.info(rechargeRecord.getId()+"充值成功");
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("订单号"+out_trade_no+e.getMessage());
                }
            }
        }
        log.info("微信订单查询结束");
    }

}
