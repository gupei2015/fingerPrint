package com.td.fp.service.impl;

import com.td.fp.activeX.FmdVerification;
import com.td.fp.mybatis.domain.CrUserInfo;
import com.td.fp.mybatis.domain.UserEnrollment;
import com.td.fp.mybatis.domain.UserInfo;
import com.td.fp.mybatis.mapper.CrUserInfoMapper;
import com.td.fp.mybatis.mapper.UserInfoMapper;
import com.td.fp.service.FingerPrintService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;

/**
 * @author  by Gupei on 2016/4/7.
 */
@Service
@Slf4j
public class FingerPrintCourtService implements FingerPrintService {

    @Autowired
    transient UserInfoMapper userInfoMapper;

    @Autowired
    transient CrUserInfoMapper crUserInfoMapper;
    //private final FmdVerification fmdVerification = new FmdVerification();

    @Override
    public void enrollUserFingerPrint(UserInfo userInfo ) {

        if (userInfoMapper.updateUserInfo(userInfo) ==0 )
            userInfoMapper.insertUserInfo( userInfo );

    }

    @Override
    public String[] loadUserFingerPrint(String userId) {

        List<UserInfo> userInfoList = userInfoMapper.getUserInfoListById( userId );
        if ( userInfoList == null&& userInfoList.size()==0 ) return null;

        String[] fmdXmls = new String[userInfoList.size()];
        int fIndex=0;
        for ( UserInfo userInfo: userInfoList){
            fmdXmls[fIndex] = userInfo.getFmdXml();
            fIndex++;
        }
        return fmdXmls;

    }

    @Override
    public UserEnrollment getUserEnrollment(UserInfo userInfo ){

        UserEnrollment userEnrollment = new UserEnrollment();
        userEnrollment.setUserId( userInfo.getUserId() );
        userEnrollment.setUserName( userInfo.getUserName());
        userEnrollment.setFmdXmls( new HashMap<>());
        List<UserInfo> userInfoList = userInfoMapper.getUserInfoListById( userInfo.getUserId() );

        if (userInfoList!=null&&userInfoList.size()>0){
            userEnrollment.setUserName( userInfoList.get(0).getUserName());
            for (UserInfo registerUserInfo: userInfoList ){
                userEnrollment.getFmdXmls().put( registerUserInfo.getFingerIndex(), registerUserInfo.getFmdXml() );
            }
        }
        return userEnrollment;

    }

    @Override
    public UserInfo verifyUserFingerPrint(String fmdXmlToIdentify, String userId) {
        List<UserInfo> userInfoList = userInfoMapper.getUserInfoListById( userId );
        if ( userInfoList == null&& userInfoList.isEmpty() ) return null;

        for ( UserInfo userInfo: userInfoList){
            if ( !StringUtils.isEmpty(userInfo.getFmdXml()) ) {
                if (FmdVerification.verifyFingerPrint(fmdXmlToIdentify, userInfo.getFmdXml())) {
                    /** set registered finger print count */
                    userInfo.setFingerIndex( userInfoList.size() );
                    return userInfo;
                }
            }
        }
        return null;

    }

    @Override
    public boolean verifyFingerPrint(String fmdXmlToIdentify, String fmdXmlSample) {

        return FmdVerification.verifyFingerPrint( fmdXmlToIdentify, fmdXmlSample);

    }

    @Override
    public boolean identifyFingerPrint(String fmdXmlToIdentify, String[] enrolledFmdXml) {

        int matchCount = FmdVerification.identifyFingerPrint( fmdXmlToIdentify, enrolledFmdXml);
        return matchCount>0;
    }

    @Override
    public UserInfo getUserInfoFromCrUser(CrUserInfo crUserInfo){

        //TODO: need to decrypt password got from db
        if (!crUserInfo.getPassword().equals(crUserInfo.getPassword())) return null;


        UserInfo verifiedUserInfo = new UserInfo();
        verifiedUserInfo.setUserId( crUserInfo.getLoginName() );
        verifiedUserInfo.setUserName( crUserInfo.getUserName() );

        List<UserInfo> userList = userInfoMapper.getUserInfoListById(crUserInfo.getLoginName());
        if ( userList==null || userList.isEmpty() ){
            verifiedUserInfo.setFingerIndex(0);
        }
        else{
            verifiedUserInfo.setFingerIndex(userList.size());
        }
        return verifiedUserInfo;
    }

    @Override
    public CrUserInfo getCrUserInfo(String loginName){

        CrUserInfo crUserInfo = crUserInfoMapper.getCrUserInfoByLoginName( loginName );
        return crUserInfo;
    }

    public List<UserInfo> queryUserInfo(String userKeyword){
        return userInfoMapper.listUserInfo(userKeyword);
    }


}
