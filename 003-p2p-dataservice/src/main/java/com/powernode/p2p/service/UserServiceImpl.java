package com.powernode.p2p.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.powernode.p2p.constants.MyConstants;
import com.powernode.p2p.exception.ResultException;
import com.powernode.p2p.mapper.UFinanceAccountMapper;
import com.powernode.p2p.mapper.UUserMapper;
import com.powernode.p2p.model.UFinanceAccount;
import com.powernode.p2p.model.UFinanceAccountExample;
import com.powernode.p2p.model.UUser;
import com.powernode.p2p.model.UUserExample;
import com.powernode.p2p.myutils.ResultEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * @Author AlanLin
 * @Description
 * @Date 2020/10/12
 */
@Service(interfaceClass = UserService.class,timeout = 20000,version = "1.0.0",retries = 5,weight = 5)
@Component
public class UserServiceImpl implements UserService{

    @Autowired
    private UUserMapper userMapper;

    @Autowired
    private UFinanceAccountMapper accountMapper;

    @Autowired
    @Qualifier( "taskExecutor")
    private Executor taskExecutor;

    @Autowired
    private  RedisTemplate<String, Object> redisTemplate;

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
        //添加用户
        UUser uUser = new UUser();
        uUser.setAddTime(new Date());
        uUser.setLoginPassword(loginPassword);
        uUser.setPhone(phone);
        userMapper.insertSelective(uUser);
        //添加账户并绑定用户id
        UFinanceAccount uFinanceAccount = new UFinanceAccount();
        uFinanceAccount.setAvailableMoney(MyConstants.BONUS);
        uFinanceAccount.setUid(uUser.getId());
        accountMapper.insertSelective(uFinanceAccount);
        return uUser;
    }

    @Override
    public UFinanceAccount queryAccount(Integer id) throws ResultException{
        UFinanceAccountExample uFinanceAccountExample = new UFinanceAccountExample();
        UFinanceAccountExample.Criteria criteria = uFinanceAccountExample.createCriteria();
        criteria.andUidEqualTo(id);
        List<UFinanceAccount> uFinanceAccounts = accountMapper.selectByExample(uFinanceAccountExample);
        if (uFinanceAccounts==null&&uFinanceAccounts.size()==0){
            throw new ResultException(ResultEnum.NOT_FOUND);
        }
        return uFinanceAccounts.get(0);
    }

    @Override
    public UUser login(String phone, String loginPassword)throws ResultException {
        UUserExample uUserExample = new UUserExample();
        UUserExample.Criteria criteria = uUserExample.createCriteria();
        criteria.andPhoneEqualTo(phone);
        List<UUser> uUsers = userMapper.selectByExample(uUserExample);
        if (uUsers==null||uUsers.size()==0){
            throw new ResultException(ResultEnum.USER_NOT_FOUND);
        }
        if (!StringUtils.equals(uUsers.get(0).getLoginPassword(),loginPassword)){
            throw new ResultException(ResultEnum.PASSWORD_ERRO);
        }
        //修改登录时间，交给另外一个线程，修改不成功也没关系
        UUser user = new UUser();
        user.setId(uUsers.get(0).getId());
        user.setLastLoginTime(new Date());
        taskExecutor.execute(new Runnable(){
            @Override
            public void run() {
                userMapper.updateByPrimaryKeySelective(user);
                System.out.println("独立线程插入登录时间："+Thread.currentThread().getName());
            }
        });
        System.out.println("请求主线程："+Thread.currentThread().getName());
        return uUsers.get(0);
    }

    @Override
    public Integer putIdAndRealName(UUser user) {
        return userMapper.updateByPrimaryKeySelective(user);
    }

    @Override
    public void putPhoto(Integer id, String path) {
        UUser user = new UUser();
        user.setId(id);
        user.setHeaderImage(path);
        userMapper.updateByPrimaryKeySelective(user);
    }

    @Override
    public Integer updatePassword(Integer id, String newLoginPassword) {
        UUser user = new UUser();
        user.setId(id);
        user.setLoginPassword(newLoginPassword);
        int count = 0;
        try {
            count = userMapper.updateByPrimaryKeySelective(user);
        } catch (Exception e) {
            throw new ResultException(ResultEnum.PASSWORD_UPDATE_FAIL);
        }
        if (count==0){
            throw new ResultException(ResultEnum.PASSWORD_UPDATE_FAIL);
        }
        return count;
    }
}
