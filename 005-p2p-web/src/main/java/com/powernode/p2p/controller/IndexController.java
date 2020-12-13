package com.powernode.p2p.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.powernode.p2p.constants.MyConstants;
import com.powernode.p2p.model.BLoanInfo;
import com.powernode.p2p.service.BidService;
import com.powernode.p2p.service.LoanService;
import com.powernode.p2p.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author AlanLin
 * @Description
 * @Date 2020/10/12
 */
@Controller
public class IndexController {

    @Reference(interfaceClass = LoanService.class,timeout = 20000,version = "1.0.0",check = false,cluster = "failover",loadbalance = "random")
    private LoanService loanService;

    @Reference(interfaceClass = UserService.class,timeout = 20000,version = "1.0.0",check = false,cluster = "failover",loadbalance = "random")
    private UserService userService;

    @Reference(interfaceClass = BidService.class,timeout = 20000,version = "1.0.0",check = false,cluster = "failover",loadbalance = "random")
    private BidService bidService;

    /**
     * 查询首页所需数据并跳转到页面
     * @param model
     * @return
     */
    @RequestMapping("/index")
    public String index(Model model, HttpSession session){
        //查询历史平均利率
        Double hisAvgRate = loanService.queryHisAvgRate();
        model.addAttribute(MyConstants.HISAVGRATE,hisAvgRate);
        //用户总数
        Long userCount = userService.queryUserCount();
        model.addAttribute(MyConstants.USERCOUNT,userCount);
        //查询交易总额
        Double totalDealAmount=bidService.queryTotalDealAmount();
        model.addAttribute(MyConstants.TOTALDEALAMOUNT,totalDealAmount);
        Map<String,Object> condition =new HashMap<>();
        condition.put("type", 0);
        condition.put("start", 0);
        condition.put("length", 1);
        List<BLoanInfo> bLoanInfos_X = loanService.queryLoanInfoByTypeAndNum(condition);
        model.addAttribute(MyConstants.BLOANINFOS_X, bLoanInfos_X);
        condition.put("type", 1);
        condition.put("start", 0);
        condition.put("length", 4);
        List<BLoanInfo> bLoanInfos_Y = loanService.queryLoanInfoByTypeAndNum(condition);
        model.addAttribute(MyConstants.BLOANINFOS_Y, bLoanInfos_Y);
        condition.put("type", 2);
        condition.put("start", 0);
        condition.put("length", 8);
        List<BLoanInfo> bLoanInfos_S = loanService.queryLoanInfoByTypeAndNum(condition);
        model.addAttribute(MyConstants.BLOANINFOS_S, bLoanInfos_S);
        return "index";
    }

}
