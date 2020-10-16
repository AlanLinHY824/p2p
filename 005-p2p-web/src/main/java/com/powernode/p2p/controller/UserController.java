package com.powernode.p2p.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.powernode.p2p.constants.MyConstants;
import com.powernode.p2p.exception.ResultException;
import com.powernode.p2p.model.UFinanceAccount;
import com.powernode.p2p.model.UUser;
import com.powernode.p2p.myutils.MD5Util;
import com.powernode.p2p.myutils.Result;
import com.powernode.p2p.myutils.ResultEnum;
import com.powernode.p2p.myutils.UserUtils;
import com.powernode.p2p.service.RedisService;
import com.powernode.p2p.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.concurrent.TimeUnit;

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
    @Reference(interfaceClass = RedisService.class,version = "1.0.0",timeout = 20000,check = false)
    RedisService redisService;

    @GetMapping("/loan/page/register")
    public String register(HttpSession session){
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
        redisService.set(phone, scode, 10,TimeUnit.MINUTES);
        return Result.SUCCESS();
    }

    @PostMapping("/loan/page/register")
    @ResponseBody
    public Result register(@RequestParam(value = "phone",required = true) String phone,
                            @RequestParam(value = "loginPassword",required = true) String loginPassword,
                            @RequestParam(value = "messageCode",required = true) String messageCode,
                           HttpSession session){
        //校验手机验证码
        String messageCode_redis=redisService.get(phone);
        if (messageCode_redis==null){
            throw new ResultException(ResultEnum.MESSAGECODE_EXPIRE);
        }
        if (!messageCode_redis.equals(messageCode)){
            throw new ResultException(ResultEnum.MESSAGECODE_ERROR);
        }
        //验证码校验成功，调用注册服务接口
        UUser user=userService.register(phone,loginPassword);
        session.setAttribute(MyConstants.USER_SESSION, user);
        //注册成功，查询账户信息存入session
        UFinanceAccount uFinanceAccount = userService.queryAccount(user.getId());
        session.setAttribute(MyConstants.USER_ACCOUNT_SESSION, uFinanceAccount);
        return Result.SUCCESS();
    }

    @GetMapping("/loan/page/login")
    public String login(HttpSession session){
        return "login";
    }

    @PostMapping("/loan/page/login")
    @ResponseBody
    public Result login(@RequestParam(value = "phone",required = true) String phone,
                        @RequestParam(value = "loginPassword",required = true) String loginPassword,
                        @RequestParam(value = "captcha",required = true) String captcha,
                        HttpSession session){
        String captchaRedis=redisService.get(session.getId());
        if (captchaRedis==null){
            throw new ResultException(ResultEnum.MESSAGECODE_EXPIRE);
        }
        if (!StringUtils.equals(captchaRedis,captcha)){
            throw new ResultException(ResultEnum.MESSAGECODE_ERROR);
        }
        //验证码校验成功，调用登录服务接口
        UUser user=userService.login(phone, MD5Util.getMD5(loginPassword));
        session.setAttribute(MyConstants.USER_SESSION, user);
        //登录成功，查询账户信息存入session
        UFinanceAccount uFinanceAccount = userService.queryAccount(user.getId());
        session.setAttribute(MyConstants.USER_ACCOUNT_SESSION, uFinanceAccount);
        return Result.SUCCESS();
    }

    @GetMapping("/loan/page/realName")
    public String realName(HttpSession session){
        return "realName";
    }

    @PostMapping("/loan/page/realName")
    @ResponseBody
    public Result realName(@RequestParam(value = "phone",required = true) String phone,
                           @RequestParam(value = "realName",required = true) String realName,
                           @RequestParam(value = "messageCode",required = true) String messageCode,
                           @RequestParam(value = "idCard",required = true) String idCard,
                           HttpSession session){
        //校验手机验证码
        String messageCode_redis=redisService.get(phone);
        if (messageCode_redis==null){
            throw new ResultException(ResultEnum.MESSAGECODE_EXPIRE);
        }
        if (!messageCode_redis.equals(messageCode)){
            throw new ResultException(ResultEnum.MESSAGECODE_ERROR);
        }
        //调用身份验证工具
        String result = UserUtils.idVerify(idCard, realName);
        JSONObject jsonObject = JSONObject.parseObject(result);
        String res = jsonObject.getString("res");
        if (!StringUtils.equals(res, MyConstants.ID_RIGHT_CODE)){
            throw new ResultException(ResultEnum.ID_INCONSISTENT);
        }
        //身份校验成功，更新身份信息
        UUser user = (UUser)session.getAttribute(MyConstants.USER_SESSION);
        UUser user_temp=new UUser();
        user_temp.setId(user.getId());
        user_temp.setIdCard(idCard);
        user_temp.setName(realName);
        Integer integer = userService.putIdAndRealName(user_temp);
        if (integer==1){
            user.setName(realName);
            user.setIdCard(idCard);
        }
        return Result.SUCCESS();
    }


}
