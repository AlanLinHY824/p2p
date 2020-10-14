package com.powernode.p2p.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.powernode.p2p.constants.MyConstants;
import com.powernode.p2p.model.BLoanInfo;
import com.powernode.p2p.myutils.PageModel;
import com.powernode.p2p.service.LoanService;
import com.powernode.p2p.vo.BBidInfoVo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author AlanLin
 * @Description
 * @Date 2020/10/13
 */
@Controller
public class LoanController {

    @Reference(interfaceClass = LoanService.class,timeout = 20000,version = "1.0.0",check = false)
    private LoanService loanService;

    @RequestMapping("/loan/loan")
    public String loan(Model model,
                       @RequestParam(value = "ptype",required = false) Integer ptype,
                       @RequestParam(value = "currentPage",defaultValue = "1") Integer currentPage,
                       HttpSession session){
        PageModel pageModel=(PageModel)session.getAttribute("pageModel");
        if(pageModel==null){
            pageModel=new PageModel();
            session.setAttribute(MyConstants.PAGEMODEL,pageModel);
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
        return "loan";
    }

    @RequestMapping("/loan/loanInfo/{loanId}")
    public String loanInfo(Model model,@PathVariable Integer loanId){
        BLoanInfo bLoanInfo= loanService.queryLoanInfoById(loanId);
        List<BBidInfoVo> bBidInfos=loanService.queryBidByLoanId(loanId);
        model.addAttribute(MyConstants.BLOANINFOBYID,bLoanInfo);
        model.addAttribute(MyConstants.BBIDINFOS,bBidInfos);
        return "loanInfo";
    }

    @RequestMapping("/loan/bidInfo/{loanId}")
    @ResponseBody
    public Object loanInfo(Model model,HttpSession session,
                           @PathVariable Integer loanId,
                           @RequestParam(defaultValue = "1",required = false) Integer currentPage){
        PageModel pageModel = (PageModel)session.getAttribute(MyConstants.PAGEMODEL);
        if (pageModel==null){
            pageModel=new PageModel();
        }
        pageModel.setCurrentPage(currentPage);
        List<BBidInfoVo> bBidInfos=loanService.queryBidByLoanId(loanId,pageModel);
        Integer bidCount=loanService.queryBidCountByLoanId(loanId);
        pageModel.setTotalRecordCounts(bidCount);
        Map<String,Object> map=new HashMap<>();
        map.put("pageModel",pageModel);
        map.put("bBidInfos",bBidInfos);
        return map;
    }

}
