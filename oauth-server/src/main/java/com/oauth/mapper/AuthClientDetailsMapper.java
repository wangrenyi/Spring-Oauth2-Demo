package com.oauth.mapper;

import com.oauth.table.AuthClientDetails;
import com.oauth.table.AuthClientDetailsExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface AuthClientDetailsMapper {
    long countByExample(AuthClientDetailsExample example);

    int deleteByExample(AuthClientDetailsExample example);

    int insert(AuthClientDetails record);

    int insertSelective(AuthClientDetails record);

    List<AuthClientDetails> selectByExample(AuthClientDetailsExample example);

    int updateByExampleSelective(@Param("record") AuthClientDetails record, @Param("example") AuthClientDetailsExample example);

    int updateByExample(@Param("record") AuthClientDetails record, @Param("example") AuthClientDetailsExample example);
}