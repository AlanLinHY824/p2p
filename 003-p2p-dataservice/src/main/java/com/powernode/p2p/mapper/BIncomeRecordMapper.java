package com.powernode.p2p.mapper;

import com.powernode.p2p.model.BIncomeRecord;
import com.powernode.p2p.model.BIncomeRecordExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BIncomeRecordMapper {
    int countByExample(BIncomeRecordExample example);

    int deleteByExample(BIncomeRecordExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(BIncomeRecord record);

    int insertSelective(BIncomeRecord record);

    List<BIncomeRecord> selectByExample(BIncomeRecordExample example);

    BIncomeRecord selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") BIncomeRecord record, @Param("example") BIncomeRecordExample example);

    int updateByExample(@Param("record") BIncomeRecord record, @Param("example") BIncomeRecordExample example);

    int updateByPrimaryKeySelective(BIncomeRecord record);

    int updateByPrimaryKey(BIncomeRecord record);
}