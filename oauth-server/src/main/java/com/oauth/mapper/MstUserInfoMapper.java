package com.oauth.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;

import com.oauth.table.MstUserInfo;
import com.oauth.table.MstUserInfoExample;

public interface MstUserInfoMapper {
    long countByExample(MstUserInfoExample example);

    int deleteByExample(MstUserInfoExample example);

    int insert(MstUserInfo record);

    int insertSelective(MstUserInfo record);

    List<MstUserInfo> selectByExample(MstUserInfoExample example);

    int updateByExampleSelective(@Param("record") MstUserInfo record, @Param("example") MstUserInfoExample example);

    int updateByExample(@Param("record") MstUserInfo record, @Param("example") MstUserInfoExample example);
}