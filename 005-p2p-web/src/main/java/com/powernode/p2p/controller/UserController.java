package com.powernode.p2p.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.powernode.p2p.constants.MyConstants;
import com.powernode.p2p.exception.ResultException;
import com.powernode.p2p.model.BRechargeRecord;
import com.powernode.p2p.model.UFinanceAccount;
import com.powernode.p2p.model.UUser;
import com.powernode.p2p.myutils.*;
import com.powernode.p2p.service.*;
import com.powernode.p2p.vo.BBidInfoVo;
import com.powernode.p2p.vo.BIncomeRecordVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author AlanLin
 * @Description
 * @Date 2020/10/11
 */
@Controller
@Slf4j
public class UserController {

    @Reference(interfaceClass = UserService.class,timeout = 20000,check = false,version = "1.0.0",cluster = "failover",loadbalance = "random")
    UserService userService;
    @Reference(interfaceClass = RedisService.class,version = "1.0.0",timeout = 20000,check = false,cluster = "failover",loadbalance = "random")
    RedisService redisService;
    @Reference(interfaceClass = BidService.class,timeout = 20000,version = "1.0.0",check = false,cluster = "failover",loadbalance = "random")
    private BidService bidService;
    @Reference(interfaceClass = IncomeService.class,timeout = 20000,version = "1.0.0",check = false,cluster = "failover",loadbalance = "random")
    private IncomeService incomeService;
    @Reference(interfaceClass = RechargeService.class,timeout = 20000,version = "1.0.0",check = false,cluster = "failover",loadbalance = "random")
    private RechargeService rechargeService;


    //跳转至注册页面
    @GetMapping("/loan/page/register")
    public String register(HttpSession session){
        return "register";
    }

    //检查手机号是否已经注册
    @RequestMapping("/loan/page/checkPhone")
    @ResponseBody
    public Result checkPhone(@RequestParam(value = "phone",required = true) String phone){
        Boolean isExist=userService.checkPhone(phone);
        if (isExist){
            return Result.FAIL(new ResultException(ResultEnum.PHONE_EXISTS));
        }
        return Result.SUCCESS();
    }

    //检查原密码
    @RequestMapping("/loan/page/checkPassword")
    @ResponseBody
    public Result checkPassword(@RequestParam(value = "loginPassword",required = true) String loginPassword,
                                HttpSession session){
        UUser user = (UUser)session.getAttribute(MyConstants.USER_SESSION);
        if (!user.getLoginPassword().equals(loginPassword)){
            return Result.FAIL(new ResultException(ResultEnum.OLD_PASSWORD_ERRO));
        }
        return Result.SUCCESS();
    }

    //获取手机验证码
    @RequestMapping("/loan/page/messageCode")
    @ResponseBody
    public Result messageCode(String phone, HttpSession session){
        String scode = UserUtils.messageCode(phone);
        log.info("手机号"+phone+"获取验证码:"+scode);
        redisService.set(phone, scode, 10,TimeUnit.MINUTES);
        return Result.SUCCESS(scode);
    }

    //用户注册
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
        return Result.SUCCESS();
    }

    //修改密码
    @PostMapping("/loan/page/updatePassword")
    @ResponseBody
    public Result updatePassword(@RequestParam(value = "phone",required = false) String phone,
                           @RequestParam(value = "loginPassword",required = true) String loginPassword,
                           @RequestParam(value = "messageCode",required = false) String messageCode,
                           @RequestParam(value = "newLoginPassword",required = true) String newLoginPassword,
                           HttpSession session){
        if (!phone.equals("")){
            //校验手机验证码
            String messageCode_redis=redisService.get(phone);
            if (messageCode_redis==null){
                return Result.FAIL(new ResultException(ResultEnum.MESSAGECODE_EXPIRE));
            }
            if (!messageCode_redis.equals(messageCode)){
                return Result.FAIL(new ResultException(ResultEnum.MESSAGECODE_ERROR));
            }
        }
        UUser user = (UUser)session.getAttribute(MyConstants.USER_SESSION);
        if (!ObjectUtils.allNotNull(user)){
            return Result.FAIL(new ResultException(ResultEnum.NOR_LOGGED));
        }
        if (user.getLoginPassword().equals(newLoginPassword)){
            return Result.FAIL(new ResultException(ResultEnum.NEW_PASSWORD_ERRO));
        }
        //验证码校验成功，调用修改密码的服务接口
        try {
            Integer count = userService.updatePassword(user.getId(), newLoginPassword);
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof ResultException){
                return Result.FAIL((ResultException)e);
            }
            return Result.FAIL(new ResultException(ResultEnum.INTERNAL_ERRO));
        }
        user.setLoginPassword(newLoginPassword);
        session.setAttribute(MyConstants.USER_SESSION, user);
        return Result.SUCCESS();
    }

    @GetMapping("/loan/page/toUpdatePassword")
    public String toUpdatePassword(HttpSession session,Model model){
        UUser user = (UUser)session.getAttribute(MyConstants.USER_SESSION);
        if (!ObjectUtils.allNotNull(user)){
            throw new ResultException(ResultEnum.NOR_LOGGED);
        }
        model.addAttribute("phone", user.getPhone());
        return "updatePassword";
    }



    //跳转至登录页面
    @GetMapping("/loan/page/login")
    public String login(HttpSession session, Model model,String redictURL){
        if (StringUtils.equals(redictURL, "")){
            model.addAttribute("redictURL",session.getServletContext().getContextPath()+"/index" );
        }else {
            model.addAttribute("redictURL", redictURL);
        }
        return "login";
    }

    //用户登录
    @PostMapping("/loan/page/login")
    @ResponseBody
    public Result login(@RequestParam(value = "phone",required = true) String phone,
                        @RequestParam(value = "loginPassword",required = true) String loginPassword,
                        @RequestParam(value = "captcha",required = true) String captcha,
                        HttpSession session){
        String captchaRedis=redisService.get(session.getId());
        if (captchaRedis==null){
            return Result.FAIL(new ResultException(ResultEnum.MESSAGECODE_EXPIRE));
        }
        if (!StringUtils.equalsIgnoreCase(captchaRedis,captcha)){
            return Result.FAIL(new ResultException(ResultEnum.MESSAGECODE_ERROR));
        }
        //验证码校验成功，调用登录服务接口
        UUser user=userService.login(phone, MD5Util.getMD5(loginPassword));
        session.setAttribute(MyConstants.USER_SESSION, user);
        return Result.SUCCESS();
    }

    //跳转至实名认证页面
    @GetMapping("/loan/page/realName")
    public String realName(HttpSession session, Model model,String redictURL){
        model.addAttribute("redictURL", redictURL);
        return "realName";
    }

    //获取用户的余额账户
    @RequestMapping("/loan/page/getAccount")
    @ResponseBody
    public Result getAccount(HttpSession session){
        UUser user = (UUser)session.getAttribute(MyConstants.USER_SESSION);
        //登录成功，查询账户信息存入session
        UFinanceAccount uFinanceAccount = userService.queryAccount(user.getId());
        return Result.SUCCESS(uFinanceAccount);
    }

    //实名认证
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
        Boolean result = UserUtils.idVerify(idCard, realName);
        if (!result){
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
        session.setAttribute(MyConstants.USER_SESSION, user);
        return Result.SUCCESS();
    }

    //跳转至个人中心
    @RequestMapping("/loan/myCenter")
    public String myCenter(HttpSession session,Model model){
        UUser user = (UUser)session.getAttribute(MyConstants.USER_SESSION);
        if (!ObjectUtils.allNotNull(user)){
            return "login";
        }
        UFinanceAccount uFinanceAccount = userService.queryAccount(user.getId());
        model.addAttribute(MyConstants.USER_ACCOUNT, uFinanceAccount);
        //获取最近投资5条记录
        List<BBidInfoVo> bBidInfos = bidService.queryRecentBidRecord(user.getId());
        model.addAttribute(MyConstants.BBIDINFOS, bBidInfos);
        List<BIncomeRecordVo> bIncomeRecords = incomeService.queryRecentIncomeRecord(user.getId());
        model.addAttribute(MyConstants.BINCOMERECORDS, bIncomeRecords);
        List<BRechargeRecord> rechargeRecords = rechargeService.queryRecentRechargeRecord(user.getId());
        model.addAttribute(MyConstants.RECHARGERECORDS, rechargeRecords);
        return "myCenter";
    }

    //上传头像
    @PutMapping("/myCenter/photo")
    @ResponseBody
    public Result myInvest(@RequestParam("photo") MultipartFile photo, HttpSession session, Model model){
        String originalFilename1 = photo.getOriginalFilename();
        int index = originalFilename1.lastIndexOf(".");
        String substring = originalFilename1.substring(index + 1 );
        FastDFSClient fastDFSClient =null;
        String url =null;
        try {
            fastDFSClient = new FastDFSClient("fastdfs.conf");
            url = fastDFSClient.uploadFile(photo.getBytes(), substring);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResultException(ResultEnum.INTERNAL_ERRO);
        }
        System.out.println(url);
        UUser user = (UUser)session.getAttribute(MyConstants.USER_SESSION);
        if (!ObjectUtils.allNotNull(user)){
            throw new ResultException(ResultEnum.NOR_LOGGED);
        }
//        String originalFilename = photo.getOriginalFilename();
//        //项目访问路径,最终存储到数据库
//        String contextDir = generateDir(originalFilename.substring(originalFilename.lastIndexOf(".")+1), "/image/");
//        //本地存储路径，使用\作为路径分隔符，防止在Linux系统下不识别
//        String fileDir = generateDir(originalFilename.substring(originalFilename.lastIndexOf(".")+1), "\\image\\");
//        try {
//            String path = ResourceUtils.getURL("classpath:").getPath()+fileDir;
////            System.out.println(path);
////            ApplicationHome h = new ApplicationHome(getClass());
////            String path = h.getSource().getAbsolutePath()+"/BOOT-INF/classes/"+fileDir;
////            System.out.println(path);
////            String contextPath = session.getServletContext().getRealPath("/image");
////            File file = new File(contextPath, "resources/static/image/");
//            File file = new File(path);
//            if (!file.exists()){
//                file.mkdirs();
//            }
//            File file1 = new File(path,originalFilename);
//            photo.transferTo(file1.getAbsoluteFile());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        userService.putPhoto(user.getId(),"http://192.168.172.35/"+url);
        user.setHeaderImage("http://192.168.172.35/"+url);
        session.setAttribute(MyConstants.USER_SESSION, user);
        return Result.SUCCESS();
    }

    private String generateDir(String filename,String savePath){
        //得到文件名的hashCode的值，得到的就是filename这个字符串对象在内存中的地址
        int hashcode = filename.hashCode();
        int dir1 = hashcode&0xf;  //0--15
        int dir2 = (hashcode&0xf0)>>4;  //0-15
        //构造新的保存目录
        String dir = savePath +  dir1 + "\\" + dir2+"\\";  //upload\2\3  upload\3\5
        //File既可以代表文件也可以代表目录
        File file = new File(dir);
        //如果目录不存在
        if(!file.exists()){
            //创建目录
            file.mkdirs();
        }
        return dir;
    }

    //跳转至投资记录页面
    @RequestMapping("/loan/myInvest")
    public String myInvest(HttpSession session,Model model){
        UUser user = (UUser)session.getAttribute(MyConstants.USER_SESSION);
        if (!ObjectUtils.allNotNull(user)){
            return "login";
        }
        return "myInvest";
    }


    //退出登录
    @GetMapping("/loan/logout")
    public String logout(HttpSession session,String redictURL){
        session.invalidate();
        return "redirect:"+redictURL;
    }

    //测试页面跳转
    @GetMapping("/redirectTest")
    public String redirectTest(HttpServletResponse response, String redictURL) throws Exception {
//        response.sendRedirect("/005-p2p-web/index.html");
        return "redirect:/loan/myCenter";
    }

}
