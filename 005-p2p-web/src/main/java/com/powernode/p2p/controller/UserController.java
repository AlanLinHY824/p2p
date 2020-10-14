package com.powernode.p2p.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.powernode.p2p.exception.ResultException;
import com.powernode.p2p.myutils.Result;
import com.powernode.p2p.myutils.ResultEnum;
import com.powernode.p2p.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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

    @RequestMapping("/loan/page/register")
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

}
