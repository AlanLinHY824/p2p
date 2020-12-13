package com.powernode.p2p.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.powernode.p2p.constants.MyConstants;
import com.powernode.p2p.exception.ResultException;
import com.powernode.p2p.model.UUser;
import com.powernode.p2p.myutils.PageModel;
import com.powernode.p2p.myutils.Result;
import com.powernode.p2p.myutils.ResultEnum;
import com.powernode.p2p.service.BidService;
import com.powernode.p2p.service.RedisService;
import com.powernode.p2p.vo.BBidInfoVo;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * @Author AlanLin
 * @Description
 * @Date 2020/10/19
 */
@Controller
public class BidController {
    @Reference(interfaceClass = BidService.class,timeout = 20000,version = "1.0.0",check = false,cluster = "failover",loadbalance = "random")
    private BidService bidService;

    @Reference(interfaceClass = RedisService.class,timeout = 20000,version = "1.0.0",check = false,cluster = "failover",loadbalance = "random")
    private RedisService redisService;

    @RequestMapping("/bid/invest")
    @ResponseBody
    public Object loanInfo(@RequestParam("loanId") Integer loanId,
                           @RequestParam("bidMoney") Double bidMoney,
                            HttpSession session){
        UUser user = (UUser)session.getAttribute(MyConstants.USER_SESSION);
        if (!ObjectUtils.allNotNull(user)){
            return Result.FAIL(new ResultException(ResultEnum.NOR_LOGGED));
        }
        bidService.buyLoan(user.getId(),loanId,bidMoney);
        //创建线程池，仅供测试使用
//        ExecutorService executorService= Executors.newFixedThreadPool(8);
//        for (int i = 0; i < 1000; i++) {
//            executorService.submit(new Runnable() {
//                @Override
//                public void run() {
//                    bidService.buyLoan(user.getId(),loanId,bidMoney);
//                }
//            });
//        }
        //将投资金额存入排行榜
        try {
            redisService.set(MyConstants.INVEST_RANGE, user.getPhone(),bidMoney);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.SUCCESS();
    }

    //查询交易记录并分页
    @RequestMapping("/loan/bidInfo/{loanId}")
    @ResponseBody
    public Object loanInfo(Model model, HttpSession session,
                           @PathVariable Integer loanId,
                           @RequestParam(defaultValue = "1",required = false) Integer currentPage){
        PageModel pageModel = (PageModel)session.getAttribute(MyConstants.PAGEMODEL);
        if (pageModel==null){
            pageModel=new PageModel();
        }
        pageModel.setCurrentPage(currentPage);
        List<BBidInfoVo> bBidInfos=bidService.queryBidByLoanId(loanId,pageModel);
        Integer bidCount=bidService.queryBidCountByLoanId(loanId);
        pageModel.setTotalRecordCounts(bidCount);
        Map<String,Object> map=new HashMap<>();
        map.put(MyConstants.PAGEMODEL,pageModel);
        map.put("bBidInfos",bBidInfos);
        return map;
    }
}
