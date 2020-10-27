package com.powernode.p2p.pay;

import com.github.wxpay.sdk.WXPayUtil;
import com.powernode.p2p.myutils.HttpClientUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author AlanLin
 * @Description
 * @Date 2020/10/26
 */
@Controller
public class WXPayController {

    @RequestMapping("/payByWXPay")
    @ResponseBody
    public Object payByWXPay(@RequestParam(value = "out_trade_no",required = true) String out_trade_no,
                             @RequestParam(value = "total_fee",required = true) Double total_fee,
                             @RequestParam(value = "body",required = true) String body){
        Map<String,String> parasMap=new HashMap<>();
        parasMap.put("appid", "wx8a3fcf509313fd74");
        parasMap.put("mch_id", "1361137902");
        //随机数字符串，增加签名的不确定性
        parasMap.put("nonce_str", WXPayUtil.generateNonceStr());
        parasMap.put("body", body);
        parasMap.put("out_trade_no", out_trade_no);

        //转换金额，不会丢失精度
        BigDecimal bigDecimal = new BigDecimal(total_fee);
        BigDecimal multiply = bigDecimal.multiply(new BigDecimal(100));
        int i = multiply.intValue();
        //传入金额，单位为分
        parasMap.put("total_fee", i+"");
        parasMap.put("spbill_create_ip", "127.0.0.1");
        parasMap.put("notify_url", "http://localhost:9090/pay/wxpay");
        parasMap.put("trade_type", "NATIVE");
        //生成签名值:generateSignature保证数据安全
        try {
            parasMap.put("sign", WXPayUtil.generateSignature(parasMap, "367151c5fd0d50f1e34a68a802d6bbca"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        String parasXml =null;
        try {
            parasXml = WXPayUtil.mapToXml(parasMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String resultXml = HttpClientUtils.doPostByXml("https://api.mch.weixin.qq.com/pay/unifiedorder", parasXml);
        Map<String, String> resultMap=null;
        try {
            resultMap = WXPayUtil.xmlToMap(resultXml);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultMap;
    }

    @RequestMapping("/pay/WXPayQuery")
    @ResponseBody
    public Object aliPayQuery(@RequestParam("out_trade_no") String out_trade_no , HttpServletResponse httpResponse,
                              HttpServletRequest request, Model model) {
        Map<String,String> parasMap=new HashMap<>();
        parasMap.put("appid", "wx8a3fcf509313fd74");
        parasMap.put("mch_id", "1361137902");
        parasMap.put("out_trade_no", out_trade_no);
        parasMap.put("nonce_str", WXPayUtil.generateNonceStr());
        try {
            parasMap.put("sign", WXPayUtil.generateSignature(parasMap, "367151c5fd0d50f1e34a68a802d6bbca"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        String parasXml =null;
        try {
            parasXml = WXPayUtil.mapToXml(parasMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String resultXml = HttpClientUtils.doPostByXml("https://api.mch.weixin.qq.com/pay/orderquery", parasXml);
        Map<String, String> stringMap =null;
        try {
            stringMap = WXPayUtil.xmlToMap(resultXml);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringMap;
    }
}
