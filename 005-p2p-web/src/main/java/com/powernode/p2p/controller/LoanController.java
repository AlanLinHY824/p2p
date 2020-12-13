package com.powernode.p2p.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.powernode.p2p.constants.MyConstants;
import com.powernode.p2p.model.BLoanInfo;
import com.powernode.p2p.model.UFinanceAccount;
import com.powernode.p2p.model.UUser;
import com.powernode.p2p.myutils.PageModel;
import com.powernode.p2p.service.LoanService;
import com.powernode.p2p.service.RedisService;
import com.powernode.p2p.service.UserService;
import com.powernode.p2p.vo.BBidInfoVo;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @Author AlanLin
 * @Description
 * @Date 2020/10/13
 */
@Controller
public class LoanController {

    @Reference(interfaceClass = LoanService.class,timeout = 20000,version = "1.0.0",check = false,cluster = "failover",loadbalance = "random")
    private LoanService loanService;

    @Reference(interfaceClass = UserService.class,timeout = 20000,version = "1.0.0",check = false,cluster = "failover",loadbalance = "random")
    private UserService userService;

    @Reference(interfaceClass = RedisService.class,timeout = 20000,version = "1.0.0",check = false,cluster = "failover",loadbalance = "random")
    private RedisService redisService;

    @RequestMapping("/loan/loan")
    public String loan(Model model,
                       @RequestParam(value = "ptype",required = false) Integer ptype,
                       @RequestParam(value = "currentPage",defaultValue = "1") Integer currentPage,
                       HttpSession session){
        PageModel pageModel=(PageModel)session.getAttribute(MyConstants.PAGEMODEL);
        if(pageModel==null){
            pageModel=new PageModel();
        }
        pageModel.setCurrentPage(currentPage);
        //查询总记录数
        int loanCount=loanService.queryLoanCountByType(ptype);
        pageModel.setTotalRecordCounts(loanCount);
        model.addAttribute("ptype",ptype);
        //校验页码是否正确，为了减轻数据库压力
        if (currentPage>=1&&currentPage<=loanCount){
            //查询产品数据
            List<BLoanInfo> loanInfos = loanService.queryLoanInfoByType(ptype, pageModel);
            model.addAttribute(MyConstants.LOANINFOSBYTYPEANDPAGE,loanInfos);
        }
        model.addAttribute(MyConstants.INVEST_RANGE, redisService.get(MyConstants.INVEST_RANGE, MyConstants.INVEST_RANGE_COUNT));
        session.setAttribute(MyConstants.PAGEMODEL,pageModel);
        return "loan";
    }

    //显示产品详情
    @RequestMapping("/loan/loanInfo/{loanId}")
    public String loanInfo(Model model,@PathVariable Integer loanId,HttpSession session){
        BLoanInfo bLoanInfo= loanService.queryLoanInfoById(loanId);
        List<BBidInfoVo> bBidInfos=loanService.queryBidByLoanId(loanId);
        model.addAttribute(MyConstants.BLOANINFOBYID,bLoanInfo);
        model.addAttribute(MyConstants.BBIDINFOS,bBidInfos);
        UUser user = (UUser)session.getAttribute(MyConstants.USER_SESSION);
        if (ObjectUtils.allNotNull(user)){
            UFinanceAccount uFinanceAccount = userService.queryAccount(user.getId());
            model.addAttribute(MyConstants.USER_ACCOUNT,uFinanceAccount);
        }
        return "loanInfo";
    }


}
