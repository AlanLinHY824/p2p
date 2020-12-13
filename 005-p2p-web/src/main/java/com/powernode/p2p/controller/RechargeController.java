package com.powernode.p2p.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.powernode.p2p.config.AlipayConfig;
import com.powernode.p2p.constants.MyConstants;
import com.powernode.p2p.exception.ResultException;
import com.powernode.p2p.model.BRechargeRecord;
import com.powernode.p2p.model.UUser;
import com.powernode.p2p.myutils.HttpClientUtils;
import com.powernode.p2p.myutils.ResultEnum;
import com.powernode.p2p.service.RechargeService;
import com.powernode.p2p.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author AlanLin
 * @Description
 * @Date 2020/10/23
 */
@Controller
@Slf4j
public class RechargeController {

    @Reference(interfaceClass = RechargeService.class,timeout = 20000,version = "1.0.0",check = false,cluster = "failover",loadbalance = "random")
    private RechargeService rechargeService;

    @Reference(interfaceClass = RedisService.class,timeout = 20000,version = "1.0.0",check = false,cluster = "failover",loadbalance = "random")
    private RedisService redisService;

    @RequestMapping("/loan/page/toRecharge")
    public String toRecharge(HttpSession session){
        UUser user = (UUser)session.getAttribute(MyConstants.USER_SESSION);
        if (!ObjectUtils.allNotNull(user)){
            return "login";
        }
        return "toRecharge";
    }

    @RequestMapping("/pay/aliPay")
    public String aliPay(@RequestParam("rechargeMoney") Double rechargeMoney , HttpServletResponse httpResponse,
                         HttpServletRequest request, Model model) {
        System.out.println("这是005的session:"+request.getSession().getId());
        UUser user = (UUser)request.getSession().getAttribute(MyConstants.USER_SESSION);
        if (!ObjectUtils.allNotNull(user)){
            return "login";
        }
        BRechargeRecord bRechargeRecord = rechargeService.makeOrder(user.getId(), rechargeMoney);
        //充值页面
        String total_amount=rechargeMoney.toString();
        String body="理财账户金额";
        String subject="理财账户金额";
        model.addAttribute("out_trade_no",bRechargeRecord.getRechargeNo());
        model.addAttribute("total_amount",total_amount);
        model.addAttribute("subject",subject);
        model.addAttribute("body",body);
//        HttpClient方式：
//        String url="http://localhost:8092/007-p2p-pay/payByAlipay";
//        Map<String,Object> map=new HashMap<>();
//        map.put("out_trade_no",out_trade_no);
//        map.put("total_amount",total_amount);
//        map.put("subject",subject);
//        map.put("body",body);
//        String repsonse=null;
//        try {
//            repsonse = HttpClientUtils.doPost(url, map);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        JSONObject jsonObject = JSONObject.parseObject(repsonse);
//        String tradeStatus = jsonObject.getString("trade_status");
//        if (!StringUtils.equals(tradeStatus, "TRADE_CLOSED")){
//            System.out.println("失败");
//        }
//        System.out.println("成功");
//        httpResponse.setContentType( "text/html;charset="  + "utf-8");
//        httpResponse.getWriter().write(repsonse); //直接将完整的表单html输出到页面
//        httpResponse.getWriter().flush();
//        httpResponse.getWriter().close();
        return "payToAlipay";
    }

    //回调函数
    @RequestMapping("/loan/alipayBack")
    public String alipayBack(HttpServletRequest request,Model model) throws AlipayApiException {
        //获取支付宝GET过来反馈信息
        Map<String,String> params = new HashMap<String,String>();
        Map<String,String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
//            valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }
        boolean signVerified = AlipaySignature.rsaCheckV1(params, AlipayConfig.alipay_public_key, AlipayConfig.charset, AlipayConfig.sign_type); //调用SDK验证签名
        String result=null;

        //商户订单号
        String out_trade_no = request.getParameter("out_trade_no");
        //支付宝交易号
        String trade_no =request.getParameter("trade_no");
        //付款金额
        String total_amount = request.getParameter("total_amount");
        System.out.println("trade_no:"+trade_no+"<br/>out_trade_no:"+out_trade_no+"<br/>total_amount:"+total_amount);
        if(signVerified) {
            //验签成功，查询支付结果
            Map<String,Object> paramMap=new HashMap<>();
            paramMap.put("out_trade_no",out_trade_no);
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
                model.addAttribute("trade_msg","网络拥堵，第三方支付状态查询失败，请在半小时内确认账户余额，如有问题请联系客服" );
                log.error("订单号"+out_trade_no+"与支付宝通讯失败，等待定时器再次查询订单状态");
                return "toRechargeBack";
            }
            //订单关闭，修改订单状态为2，对于用户来说不重要，不需抛出异常，等待定时器关闭订单即可
            if (StringUtils.equals(tradeStatus,"TRADE_CLOSED")){
                model.addAttribute("trade_msg","等待超时，交易已关闭，请重新充值" );
                rechargeService.closeOrder(out_trade_no);
                log.error("订单号"+out_trade_no+"超时订单关闭");
                return "toRechargeBack";
            }
            //扣款成功，修改订单状态为1，并增加用户的账户余额
            if (StringUtils.equals(tradeStatus,"TRADE_SUCCESS")){
                UUser user = (UUser)request.getSession().getAttribute(MyConstants.USER_SESSION);
                try {
                    rechargeService.recharge(user.getId(),out_trade_no,Double.parseDouble(total_amount));
                    return "redirect:/loan/myCenter";
                } catch (Exception e) {
                    e.printStackTrace();
                    model.addAttribute("trade_msg","付款成功，将于半小时内到账" );
                    return "toRechargeBack";
                }
            }
        }else {
            model.addAttribute("trade_msg","数据异常，存在安全风险，交易已关闭" );
            log.error("订单号"+out_trade_no+"支付宝验签失败");
            return "toRechargeBack";
        }
        return "redirect:/loan/myCenter";
    }

    @RequestMapping("/pay/WXPayMakeOrder")
    public String weChatPayMakeOrder(@RequestParam("rechargeMoney") Double rechargeMoney , HttpServletResponse httpResponse,
                                     HttpServletRequest request, Model model){
        UUser user = (UUser)request.getSession().getAttribute(MyConstants.USER_SESSION);
        if (!ObjectUtils.allNotNull(user)){
            return "login";
        }
        BRechargeRecord bRechargeRecord = rechargeService.makeOrder(user.getId(), rechargeMoney);
        //充值页面
        String body="理财账户充值";
        String subject="微信支付方式";
        model.addAttribute("out_trade_no",bRechargeRecord.getRechargeNo());
        model.addAttribute("total_fee",rechargeMoney);
        model.addAttribute("subject",subject);
        model.addAttribute("body",body);
        redisService.set(user.getId()+"out_trade_no",bRechargeRecord.getRechargeNo(),30L, TimeUnit.MINUTES);
        return "weChatPay";
    }

    @RequestMapping("/pay/generateWXPayQRCode")
    public void generateWeChatPayQRCode( HttpServletResponse response, HttpServletRequest request,Model model
//                                         @RequestParam("out_trade_no") String out_trade_no ,
                                       ) throws Exception {
        UUser user = (UUser)request.getSession().getAttribute(MyConstants.USER_SESSION);
        String out_trade_no = redisService.get(user.getId() + "out_trade_no");
        BRechargeRecord bRechargeRecord = rechargeService.queryRechargeRecordByOrderId(out_trade_no);
        Map<String,Object> parasMap=new HashMap<>();
        parasMap.put("body", "理财账户充值");
        parasMap.put("out_trade_no", out_trade_no);
        parasMap.put("total_fee", bRechargeRecord.getRechargeMoney());
        String result =null;
        try {
            result = HttpClientUtils.doPost("http://10.10.10.18:8092/007-p2p-pay/payByWXPay", parasMap);
        } catch (Exception e) {
            log.error("订单号"+out_trade_no+"与pay工程通讯失败");
            throw e;
        }
        JSONObject jsonObject = JSONObject.parseObject(result);
        if (!StringUtils.equals(jsonObject.getString("return_code"), "SUCCESS")){
            log.error("订单号"+out_trade_no+"获取微信code_url时通讯失败");
            throw new ResultException(ResultEnum.INTERNAL_ERRO);
        }

        if (!StringUtils.equals(jsonObject.getString("result_code"), "SUCCESS")){
            //参数错误
            log.error("订单号"+out_trade_no+"二维码传递参数错误");
            throw new ResultException(ResultEnum.INTERNAL_ERRO);
        }
        //生成二维码
        if (ObjectUtils.allNotNull(jsonObject.getString("code_url"))){
            //设置字符集
            Map<EncodeHintType,Object> map= new HashMap<EncodeHintType, Object>();
            map.put(EncodeHintType.CHARACTER_SET,"UTF-8");
            //创建一个矩阵对象
            BitMatrix bitMatrix= null;
            try {
                //生成二维码对应的矩阵对象
                bitMatrix = new MultiFormatWriter().encode(jsonObject.getString("code_url"), BarcodeFormat.QR_CODE,200,200, map);
                //将矩阵对象写入流中
                ServletOutputStream outputStream = response.getOutputStream();
                MatrixToImageWriter.writeToStream(bitMatrix, "png",outputStream);
                outputStream.flush();
            } catch (WriterException | IOException e) {
                e.printStackTrace();
            }
        }
    }

}
