package com.powernode.p2p.service;

import com.powernode.p2p.model.UFinanceAccount;
import com.powernode.p2p.model.UUser;

/**
 * @Author AlanLin
 * @Description
 * @Date 2020/10/12
 */
public interface UserService {
    /**
     * 查询注册用户总数
     * @return
     */
    Long queryUserCount();

    /**
     * 校验手机号是否已被注册
     * @param phone
     * @return
     */
    Boolean checkPhone(String phone);

    /**
     * 用户注册接口方法
     * @param phone
     * @param loginPassword
     * @return
     */
    UUser register(String phone, String loginPassword);

    /**
     * 根据用户id查询账户
     * @param id
     * @return
     */
    UFinanceAccount queryAccount(Integer id);

    /**
     * 用户登录接口方法
     * @param phone
     * @param loginPassword
     * @return
     */
    UUser login(String phone, String loginPassword);

    /**
     * 更新用户的身份信息
     * @param user
     * @return
     */
    Integer putIdAndRealName(UUser user);
}
