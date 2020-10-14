package com.powernode.p2p.mapper;

import com.powernode.p2p.model.BRechargeRecord;
import com.powernode.p2p.model.BRechargeRecordExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BRechargeRecordMapper {
    int countByExample(BRechargeRecordExample example);

    int deleteByExample(BRechargeRecordExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(BRechargeRecord record);

    int insertSelective(BRechargeRecord record);

    List<BRechargeRecord> selectByExample(BRechargeRecordExample example);

    BRechargeRecord selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") BRechargeRecord record, @Param("example") BRechargeRecordExample example);

    int updateByExample(@Param("record") BRechargeRecord record, @Param("example") BRechargeRecordExample example);

    int updateByPrimaryKeySelective(BRechargeRecord record);

    int updateByPrimaryKey(BRechargeRecord record);
}