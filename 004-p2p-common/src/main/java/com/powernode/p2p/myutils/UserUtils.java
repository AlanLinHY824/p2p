package com.powernode.p2p.myutils;


import com.alibaba.fastjson.JSONObject;
import com.powernode.p2p.constants.MyConstants;
import com.powernode.p2p.exception.ResultException;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @Author AlanLin
 * @Description
 * @Date 2020/9/10
 */
public class UserUtils {
    /**
     * {
     *     "code": "0", //返回码，0：成功，非0：失败（详见错误码定义）
     *           //当code=0时，再判断下面result中的res；当code!=0时，表示调用已失败，无需再继续
     *     "message": "成功", //返回码说明
     *     "result": {
     *         "name": "冯天", //姓名
     *         "idcard": "350301198011129422", //身份证号
     *         "res": "1", //核验结果状态码，1 一致；2 不一致；3 无记录
     *         "description": "一致",  //核验结果状态描述
     *        "sex": "男",
     *         "birthday": "19940320",
     *         "address": "江西省南昌市东湖区"
     *     }
     * }
     * @param id
     * @param name
     * @return
     */
    public static Boolean idVerify(String id,String name){
        String appCode="2278acbdd94a42d6b9e8622b42119f7d";
        String url="https://eid.shumaidata.com/eid/check";
        OkHttpClient client = new OkHttpClient.Builder().build();
        FormBody.Builder formbuilder = new FormBody.Builder();
        formbuilder.add("idcard", id);
        formbuilder.add("name", name);
        FormBody body = formbuilder.build();
        Request request = new Request.Builder().url(url).addHeader("Authorization", "APPCODE " + appCode).post(body).build();
        Response response = null;
        String result= null;
        try {
            response = client.newCall(request).execute();
            System.out.println("返回状态码" + response.code() + ",message:" + response.message());
            if (response.body() == null) {
                throw new ResultException(ResultEnum.INTERNAL_ERRO);
            }
            result= response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            throw new ResultException(ResultEnum.INTERNAL_ERRO);
        }
        JSONObject jsonObject = JSONObject.parseObject(result);
        String res = jsonObject.getJSONObject("result").getString("res");
        if (!StringUtils.equals(res, MyConstants.ID_RIGHT_CODE)){
            return false;
        }
        return true;
    }

    public static String messageCode(String phone){
//        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", "LTAI4GFDV4wQqVK9nUEjjNuA", "uLOVkjgNUqQkWjUBJziPCaf2JnnhEx");
//        IAcsClient client = new DefaultAcsClient(profile);
        String scode = String.valueOf(new Random().nextInt(899999) + 100000);
//        int code=(int)(Math.random()*100000);
//        CommonRequest request = new CommonRequest();
//        request.setSysMethod(MethodType.POST);
//        request.setSysDomain("dysmsapi.aliyuncs.com");
//        request.setSysVersion("2017-05-25");
//        request.setSysAction("SendSms");
//        request.putQueryParameter("RegionId", "cn-hangzhou");
//        request.putQueryParameter("PhoneNumbers", phone);
//        request.putQueryParameter("SignName", "AlanLin");
//        request.putQueryParameter("TemplateCode", "SMS_201455240");
//        request.putQueryParameter("TemplateParam", "{\"code\":"+scode+"}");
//        try {
//            CommonResponse response = client.getCommonResponse(request);
//            System.out.println(response.getData());
//        } catch (ClientException e) {
//            throw new ResultException(ResultEnum.INTERNAL_ERRO);
//        }
        return scode;
    }

    /**
     * {
     *     "code": "10000",
     *     "charge": false,
     *     "remain": 0,
     *     "msg": "查询成功",
     *     "result": "<?xml version=\"1.0\" encoding=\"utf-8\" ?><returnsms>\n <returnstatus>Success</returnstatus>\n <message>ok</message>\n <remainpoint>-6668822</remainpoint>\n <taskID>158738761</taskID>\n <successCounts>1</successCounts></returnsms>"
     * }
     * @param phone
     * @return
     */
    public static String messageCodeByHttpClient(String phone){
        String scode = String.valueOf(new Random().nextInt(899999) + 100000);
        String url="https://way.jd.com/kaixintong/kaixintong";
        String appkey="55baa987c158c8b45a96a04d937f4d55";
        String mobile="mobile";
        String content="【凯信通】您的验证码是："+scode+"，请妥善保管【5分钟内有效】";
        Map<String,Object> map=new HashMap<>();
        map.put("appkey",appkey);
        map.put("mobile",mobile);
        map.put("content",content);
        String repsonse=null;
        try {
            repsonse = HttpClientUtils.doPost(url, map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject jsonObject = JSONObject.parseObject(repsonse);
        String code = jsonObject.getString("code");
        if (!StringUtils.equals(code, "10000")){
            throw new ResultException(ResultEnum.EXTERNAL_CALL_ERROR);
        }
        Document document = null;
        try {
            document = DocumentHelper.parseText(jsonObject.getString("result"));
            String returnstatus = document.selectSingleNode("returnstatus").getText();
            if (!StringUtils.equals("Success", returnstatus)){
                throw new ResultException(ResultEnum.MESSAGECODE_SEND_ERROR);
            }
        } catch ( DocumentException e) {
            e.printStackTrace();
            throw new ResultException(ResultEnum.EXTERNAL_CALL_ERROR);
        }
        return scode;
    }
}
