package com.powernode.p2p.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.powernode.p2p.exception.ResultException;
import com.powernode.p2p.model.UUser;
import com.powernode.p2p.myutils.Result;
import com.powernode.p2p.myutils.ResultEnum;
import com.powernode.p2p.myutils.UserUtils;
import com.powernode.p2p.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * @Author AlanLin
 * @Description
 * @Date 2020/10/11
 */
@Controller
@Slf4j
public class UserController {

    @Reference(interfaceClass = UserService.class,timeout = 20000,check = false,version = "1.0.0")
    UserService userService;

    @GetMapping("/loan/page/register")
    public String register(){
        return "register";
    }

    @RequestMapping("/loan/page/checkPhone")
    @ResponseBody
    public Result checkPhone(@RequestParam(value = "phone",required = true) String phone){
        Boolean isExist=userService.checkPhone(phone);
        if (isExist){
            return Result.FAIL(new ResultException(ResultEnum.PHONE_EXISTS));
        }
        return Result.SUCCESS();
    }

    @RequestMapping("/loan/page/messageCode")
    @ResponseBody
    public Result messageCode(String phone, HttpSession session){
        String scode = UserUtils.messageCode(phone);
        session.setAttribute("messageCode",scode);
        return Result.SUCCESS();
    }

    @PostMapping("/loan/page/register")
    @ResponseBody
    public Result register(@RequestParam(value = "phone",required = true) String phone,
                            @RequestParam(value = "loginPassword",required = true) String loginPassword,
                            @RequestParam(value = "messageCode",required = true) String messageCode,
                           HttpSession session){
        String messageCodeRight = (String)session.getAttribute("messageCode");
        if (messageCodeRight==null){
            throw new ResultException(ResultEnum.MESSAGECODE_EXPIRE);
        }
        if (!messageCodeRight.equals(messageCode)){
            throw new ResultException(ResultEnum.MESSAGECODE_ERROR);
        }
        UUser user=userService.register(phone,loginPassword);
        return Result.SUCCESS();
    }
}