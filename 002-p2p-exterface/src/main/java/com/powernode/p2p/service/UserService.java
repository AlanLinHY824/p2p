package com.powernode.p2p.service;

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

    Boolean checkPhone(String phone);

    UUser register(String phone, String loginPassword);
}
