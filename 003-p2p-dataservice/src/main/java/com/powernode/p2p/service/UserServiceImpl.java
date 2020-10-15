package com.powernode.p2p.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.powernode.p2p.constants.MyConstants;
import com.powernode.p2p.exception.ResultException;
import com.powernode.p2p.mapper.UFinanceAccountMapper;
import com.powernode.p2p.mapper.UUserMapper;
import com.powernode.p2p.model.UFinanceAccount;
import com.powernode.p2p.model.UUser;
import com.powernode.p2p.model.UUserExample;
import com.powernode.p2p.myutils.ResultEnum;
import com.powernode.p2p.vo.UUserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

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
    public UUserVo register(String phone, String loginPassword) {
        UUser uUser = new UUser();
        uUser.setAddTime(new Date());
        uUser.setLoginPassword(loginPassword);
        uUser.setPhone(phone);
        userMapper.insertSelective(uUser);
        UFinanceAccount uFinanceAccount = new UFinanceAccount();
        uFinanceAccount.setAvailableMoney(MyConstants.BONUS);
        uFinanceAccount.setUid(uUser.getId());
        accountMapper.insertSelective(uFinanceAccount);
        List<UUserVo> uUserVos = userMapper.selectByPhoneAndPwd(phone, loginPassword);
        return uUserVos.get(0);
    }

    @Override
    public UUserVo login(String phone, String loginPassword) {
        UUserExample uUserExample = new UUserExample();
        UUserExample.Criteria criteria = uUserExample.createCriteria();
        criteria.andPhoneEqualTo(phone);
        List<UUser> uUsers = userMapper.selectByExample(uUserExample);
        if (uUsers==null||uUsers.size()==0){
            throw new ResultException(ResultEnum.USER_NOT_FOUND);
        }
        List<UUserVo> uUserVos = userMapper.selectByPhoneAndPwd(phone,loginPassword);
        if (uUserVos==null||uUserVos.size()==0){
            throw new ResultException(ResultEnum.PASSWORD_ERRO);
        }
        return uUserVos.get(0);
    }
}
