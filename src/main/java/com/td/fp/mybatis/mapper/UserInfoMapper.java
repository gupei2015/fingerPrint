package com.td.fp.mybatis.mapper;

/**
 * @author gupei
 * @date 2016/3/25.
 * @Description
 * Store reconciliation details in table
 *
 */

import com.td.fp.mybatis.domain.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class UserInfoMapper {

    @Autowired
    private transient SqlSessionTemplate sqlSessionTemplate;

    public int insertUserInfo(UserInfo userInfo) {

        return this.sqlSessionTemplate.insert("insertUserInfo", userInfo );

    }

    public int updateUserInfo(UserInfo userInfo) {

        Map<String,Object> param = new HashMap<>();
        param.put("userId", userInfo.getUserId());
        param.put("fingerIndex", userInfo.getFingerIndex() );
        param.put("fmdXml", userInfo.getFmdXml() );
        return this.sqlSessionTemplate.update("updateUserInfo", param );

    }


    public List<UserInfo> getUserInfoListById(String userId ) {

        Map<String,String> param = new HashMap<>();
        param.put("userId", userId);
        return this.sqlSessionTemplate.selectList( "getUserInfoById", param );

    }


    public List<UserInfo> listUserInfo( String userKeyword ) {

        Map<String,String> param = new HashMap<>();
        if ( !StringUtils.isEmpty(userKeyword) ){
            param.put("userId", userKeyword);
            param.put("userName", userKeyword);
        }

        return this.sqlSessionTemplate.selectList( "listUserInfo", param );

    }


}
