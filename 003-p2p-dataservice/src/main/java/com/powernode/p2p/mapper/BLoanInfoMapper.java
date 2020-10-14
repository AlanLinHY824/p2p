package com.powernode.p2p.mapper;

import com.powernode.p2p.model.BLoanInfo;
import com.powernode.p2p.model.BLoanInfoExample;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface BLoanInfoMapper {
    Integer countByExample(BLoanInfoExample example);

    int deleteByExample(BLoanInfoExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(BLoanInfo record);

    int insertSelective(BLoanInfo record);

    List<BLoanInfo> selectByExample(BLoanInfoExample example);

    BLoanInfo selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") BLoanInfo record, @Param("example") BLoanInfoExample example);

    int updateByExample(@Param("record") BLoanInfo record, @Param("example") BLoanInfoExample example);

    int updateByPrimaryKeySelective(BLoanInfo record);

    int updateByPrimaryKey(BLoanInfo record);

    Double selectHisAvgRate();

    List<BLoanInfo> queryLoanInfoByTypeAndNum(Map condtion);

    List<BLoanInfo> queryLoanInfoByPage(Integer ptype, Integer start, Integer pageSize);

}