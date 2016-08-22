package com.td.fp.service;

import com.td.fp.mybatis.domain.CrUserInfo;
import com.td.fp.mybatis.domain.UserEnrollment;
import com.td.fp.mybatis.domain.UserInfo;

import java.util.List;

/**
 * Created by Gupei on 2016/4/7.
 * Identify/verify finger print with enrolled user FMD
 */
public interface FingerPrintService {

    /**
     * enroll user finger print and persist into database
     * @param userInfo
     */
    void enrollUserFingerPrint(UserInfo userInfo );

    /**
     * get user user finger print enrollment info
     * @param userInfo
     */
    UserEnrollment getUserEnrollment(UserInfo userInfo );

    /**
     * load enrolled user finger print
     *
     * @param userId  user ID
     * @return
     */
    String[] loadUserFingerPrint(String userId );

    /**
     * verify fingerprint with single user registered fingerprint
     * @param fmdXmlToIdentify
     * @param fmdXmlSample
     * @return
     */
    boolean verifyFingerPrint( String fmdXmlToIdentify, String fmdXmlSample );

    /**
     * verify fingerprint with user registered fingerprints
     * @param fmdXmlToIdentify
     * @param userId
     * @return matched User info
     */
    UserInfo verifyUserFingerPrint( String fmdXmlToIdentify, String userId );

    /**
     * identify user fingerprint against enrollments
     *
     * @param fmdXmlToIdentify
     * @param enrolledFmdXml
     * @return
     */
    boolean identifyFingerPrint( String fmdXmlToIdentify, String[] enrolledFmdXml );

    /**
     * verify user and password against cr user info and return UserInfo object
     * @param crUserInfo
     * @return
     */
    UserInfo getUserInfoFromCrUser(CrUserInfo crUserInfo);

    /**
     * get system user info by login name
     * @param loginName
     * @return
     */
    CrUserInfo getCrUserInfo(String loginName);

    /**
     * query user finger print info by user Id or user name
     * @param userKeyword
     * @return
     */
    List<UserInfo> queryUserInfo(String userKeyword);

}
