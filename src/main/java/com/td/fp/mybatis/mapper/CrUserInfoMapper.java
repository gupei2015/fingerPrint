package com.td.fp.mybatis.mapper;

/**
 * @author gupei
 * @date 2016/3/25.
 * @Description
 * Store reconciliation details in table
 *
 */

import com.td.fp.mybatis.domain.CrUserInfo;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class CrUserInfoMapper {

    @Autowired
    private transient SqlSessionTemplate sqlSessionTemplate;

    public CrUserInfo getCrUserInfoByLoginName(String loginName ) {

        Map<String,String> param = new HashMap<>();
        param.put("loginName", loginName);
        return this.sqlSessionTemplate.selectOne( "getCrUserInfoByLoginName", param );

    }

}
