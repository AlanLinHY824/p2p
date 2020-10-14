package com.powernode.p2p.myutils;

import com.powernode.p2p.exception.ResultException;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
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
    public static String idVerify(String id,String name){
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
            result= response.body().string();
            if (response.code()!=1){
                throw new ResultException(ResultEnum.ID_INCONSISTENT);
            }
        } catch (IOException e) {
            throw new ResultException(ResultEnum.INTERNAL_ERRO);
        }
        return result;
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
}
