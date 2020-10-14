package com.powernode.p2p.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.powernode.p2p.mapper.UUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author AlanLin
 * @Description
 * @Date 2020/10/12
 */
@Service(interfaceClass = UserService.class,timeout = 20000,version = "1.0.0")
@Component
public class UserServiceImpl implements UserService{

    @Autowired
    private UUserMapper userMapper;

    @Override
    public Long queryUserCount() {
        return userMapper.selectUserCount();
    }

    @Override
    public Boolean checkPhone(String phone) {
        return userMapper.selectUserByPhone(phone);
    }
}
