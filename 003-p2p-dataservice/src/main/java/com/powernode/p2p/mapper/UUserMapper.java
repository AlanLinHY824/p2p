package com.powernode.p2p.mapper;

import com.powernode.p2p.model.UUser;
import com.powernode.p2p.model.UUserExample;
import com.powernode.p2p.vo.UUserVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UUserMapper {
    int countByExample(UUserExample example);

    int deleteByExample(UUserExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(UUser record);

    int insertSelective(UUser record);

    List<UUser> selectByExample(UUserExample example);

    UUser selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") UUser record, @Param("example") UUserExample example);

    int updateByExample(@Param("record") UUser record, @Param("example") UUserExample example);

    int updateByPrimaryKeySelective(UUser record);

    int updateByPrimaryKey(UUser record);

    Long selectUserCount();

    Boolean selectUserByPhone(String phone);

    List<UUserVo> selectByPhoneAndPwd(String phone, String loginPassword);
}