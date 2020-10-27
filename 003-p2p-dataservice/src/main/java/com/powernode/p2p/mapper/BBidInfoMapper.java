package com.powernode.p2p.mapper;

import com.powernode.p2p.model.BBidInfo;
import com.powernode.p2p.model.BBidInfoExample;
import com.powernode.p2p.vo.BBidInfoVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface BBidInfoMapper {
    int countByExample(BBidInfoExample example);

    int deleteByExample(BBidInfoExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(BBidInfo record);

    int insertSelective(BBidInfo record);

    List<BBidInfo> selectByExample(BBidInfoExample example);

    BBidInfo selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") BBidInfo record, @Param("example") BBidInfoExample example);

    int updateByExample(@Param("record") BBidInfo record, @Param("example") BBidInfoExample example);

    int updateByPrimaryKeySelective(BBidInfo record);

    int updateByPrimaryKey(BBidInfo record);

    Double selectTotalDealAmount();

    List<BBidInfoVo> selectBidByLoanId(Integer loanId);

    List<BBidInfoVo> selectBidByLoanIdPage(Integer loanId, int start, int pageSize);

    List<BBidInfoVo> selectRecentBidRecord(Integer id, Integer recentBidCount);
}