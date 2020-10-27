package com.powernode.p2p.config;

import java.io.FileWriter;
import java.io.IOException;

/**
 * @Author AlanLin
 * @Description
 * @Date 2020/10/23
 */
/* *
 *类名：AlipayConfig
 *功能：基础配置类
 *详细：设置帐户有关信息及返回路径
 *修改日期：2017-04-05
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
 */

public class AlipayConfig {

//↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

    // 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
    public static String app_id = "2016102600767397";

    // 商户私钥，您的PKCS8格式RSA2私钥
    public static String merchant_private_key = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDRfj20iIBMBSO/n/hGJW4btyw/mQvwkKbYhoqdvV4ArXi6nUTTwXgpL8F7A4O4l28yn1mlm8C8JkCDTtqaAQg93lLsJVOtwzCliMFcVnP1dnmU6ehdQ5lQBC6txw5vNHpyPNQwXPTd7BazDLPZYSW3i3lnXoGHj/DA4QZuNLg6sMGQfC0aXOdhP8VURgytIFQtg6eDzV4RglQW0/yBF3dm7BiHvIISGOvt+l7JWJVdJfTEZR2fJqQxDSnWkGDcLx9Ft7quFHwYQgslXlC2VJdiG/8W2ZAHtstH3ObO5Aqedvjp4clx7WXtwssQjzNETVeQFUWgHTHsLCfYnT4A5r2NAgMBAAECggEADxtha8FkLxc3yrvIdZaBaDI/WJ66w7BKt9aL7HAZdUcNK7fnnLWY6q9s7mgEea1B8Y6YMl8W+PpxMIbhMLXeWEfOBwVyEVfADE/eUcBSQGM4xUpQ3Vjgl2qVyyZNnitUoSRN0OcDKXW69VJ3LbfZtnCyYofoI1R38hDIG+5lUqYG9AAS+9Q7TbdfAVaA0bIWMxasig19X7oGIJikHAb/RRnU1oq/dZxzo4m/F5yvk0E3T1+xVvqZ3epAl58J16wqbdvRlJsUHX9ykH5s8NjJOe5FBHQpDbgH5OSxH3GWxTmIcJKigVZKyjdwsVQTVObKB0Q5e3YRs1NnVlfBE2fyKQKBgQD4WpqSkhPlG76tFOSzn2POhpOM7yWMa0r9b5cKQm/oF9ZqdbK0i0U9rt0tlooeDQpH6QPtP4lgbWOoVG/fX+yRJR30n9iAOlq0hH23JPGeHz0TtvHqtJuMTVVhwK+vug76JSly0Xr2V/4jGmI8RGc6DW76IMnkkYn2PtlGsqOLdwKBgQDX8VtSKyhyS20fJIoS8u2attDsSA9Py7u76iYMiM4qcSaxMSdFOC3biCqXgNIxjBV4LFx96AN9J6aNph1koRgU5abprOmAHWmqDQ1fpakQTFLJ2/6/jHdacjW5e7HMKyI+krf0eZ6FqH986QZ4MUtAZE0zkmq/kuivZLrJ9UM4GwKBgGCYTNqJOne37NfA8pryeY1QRn9gXIhbCtW721FAOepZqynJEXKrYIj3REuA68N4avFBLifOuI3qvgIwpTqKQqbJevc0XK6zA6iGh6b+FYPcvpK2ay1QhddyFK1qlmqQwVaFsbNYyqyZ2NyqXF74c/XeV7WtnxjpinFeTH/05ytNAoGAXoa4gu+BdQavbcKA7mqIuADu0393tGZqd4e3wCTimpGNbmMl3ZcJ+99PRaKOYEIB4+kHU9YMMOz6a2cb1sDt2bftqLyYOgymBFpSUFwj9Y5zbwluKktCs8Tq35bV7Zm0E9AF1m6mhbuMkxEuU87AMh+1661cLSAR2zUsztsd4aECgYEAio1fY1BLw2lXwhd9Qcf/60lfzGYbiSWAa6qPkmW/d9MVoeR1O1C/oaS0/pmd218ipgeo06kOAjOXJU7xfJwIVB4FKa7LABB3qC7nyqwEVEXKxGzcTmHTpvKhe0prx4cNFGF/Od14B+zxhQaOfOLoWky87ymeftGHc6NsGHO1So0=";

    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAihVEibnCR0eQQezOWqTzSo1x5gEcY0XSn5gcRAYAT1cGLIrUoXtlduT8PwEWR9HHJRWVbtUhaDubhTbjH2Y3nrJ28q2cnSgJWrLY1k1XnDPy8R6yMle+ZJRj5Obe1kdNbbJm7o8tbuOZEIQShRcCe5N3eTPJeX1TR8rLGz76tW6Wlcva8viTAC6920AqkRBJDiZeWY7oKRrLLRblIVpME8tWuR34M7IPxfnPxo8cmp+z5pdrpsDYAZ4buk8suuL67V01N42KJUjb8wFlF1Wcfc9W1KqXQm8XVfQJwH/YJTlZ+rbdZuCgI6y2L+0rvuJqF38yZB/KpZj2mQR7dPG/TwIDAQAB";

    // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String notify_url = "http://工程公网访问地址/alipay.trade.page.pay-JAVA-UTF-8/notify_url.jsp";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String return_url = "http://192.168.172.1:8092/007-p2p-pay/loan/returnUrl";

    // 签名方式
    public static String sign_type = "RSA2";

    // 字符编码格式
    public static String charset = "utf-8";

    // 支付宝网关
    public static String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    // 支付宝网关
    public static String log_path = "C:\\";


//↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

    /**
     * 写日志，方便测试（看网站需求，也可以改成把记录存入数据库）
     * @param sWord 要写入日志里的文本内容
     */
    public static void logResult(String sWord) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(log_path + "alipay_log_" + System.currentTimeMillis()+".txt");
            writer.write(sWord);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
