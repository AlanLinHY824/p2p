package com.powernode.p2p.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.powernode.p2p.constants.MyConstants;
import com.powernode.p2p.mapper.UFinanceAccountMapper;
import com.powernode.p2p.mapper.UUserMapper;
import com.powernode.p2p.model.UFinanceAccount;
import com.powernode.p2p.model.UUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

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

    @Autowired
    private UFinanceAccountMapper accountMapper;

    @Override
    public Long queryUserCount() {
        return userMapper.selectUserCount();
    }

    @Override
    public Boolean checkPhone(String phone) {
        return userMapper.selectUserByPhone(phone);
    }

    @Override
    @Transactional
    public UUser register(String phone, String loginPassword) {
        UUser uUser = new UUser();
        uUser.setAddTime(new Date());
        uUser.setLoginPassword(loginPassword);
        uUser.setPhone(phone);
        userMapper.insertSelective(uUser);
        UFinanceAccount uFinanceAccount = new UFinanceAccount();
        uFinanceAccount.setAvailableMoney(MyConstants.BONUS);
        uFinanceAccount.setUid(uUser.getId());
        accountMapper.insertSelective(uFinanceAccount);
        return uUser;
    }
}
