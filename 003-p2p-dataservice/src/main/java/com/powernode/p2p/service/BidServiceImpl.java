package com.powernode.p2p.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.powernode.p2p.mapper.BBidInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author AlanLin
 * @Description
 * @Date 2020/10/12
 */
@Service(interfaceClass = BidService.class,timeout = 20000,version = "1.0.0")
@Component
public class BidServiceImpl implements BidService {

    @Autowired
    private BBidInfoMapper bidInfoMapper;

    @Override
    public Double queryTotalDealAmount() {
        return bidInfoMapper.selectTotalDealAmount();
    }
}
