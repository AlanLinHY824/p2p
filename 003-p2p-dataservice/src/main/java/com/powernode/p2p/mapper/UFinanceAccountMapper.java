package com.powernode.p2p.mapper;

import com.powernode.p2p.model.UFinanceAccount;
import com.powernode.p2p.model.UFinanceAccountExample;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface UFinanceAccountMapper {
    int countByExample(UFinanceAccountExample example);

    int deleteByExample(UFinanceAccountExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(UFinanceAccount record);

    int insertSelective(UFinanceAccount record);

    List<UFinanceAccount> selectByExample(UFinanceAccountExample example);

    UFinanceAccount selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") UFinanceAccount record, @Param("example") UFinanceAccountExample example);

    int updateByExample(@Param("record") UFinanceAccount record, @Param("example") UFinanceAccountExample example);

    int updateByPrimaryKeySelective(UFinanceAccount record);

    int updateByPrimaryKey(UFinanceAccount record);
}