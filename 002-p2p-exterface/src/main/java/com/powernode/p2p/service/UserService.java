package com.powernode.p2p.service;

import com.powernode.p2p.vo.UUserVo;

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

    UUserVo register(String phone, String loginPassword);

    UUserVo login(String phone, String loginPassword);
}
