package com.td.fp.controller;

import com.td.fp.common.UserRole;
import com.td.fp.mybatis.domain.CrUserInfo;
import com.td.fp.mybatis.domain.UserEnrollment;
import com.td.fp.mybatis.domain.UserInfo;
import com.td.fp.service.FingerPrintService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Gu pei on 2016/4/7.
 *
 */
@Controller
@Slf4j
@RequestMapping("/fp/*")
@SessionAttributes( {"loginUser","adminUser"})
public class FingerPrintController {

    @Value("${activex.codeBase}")
    private String activeXUrl;

    @Autowired
    @Qualifier("fingerPrintCourtService")
    private transient FingerPrintService fingerPrintService;

    @RequestMapping(value="/info" )
    public String showUserInfo(Model model){

        if ( validateSession(model) ){
            if (  model.asMap().containsKey("adminUser") ){
                UserInfo adminUser = (UserInfo) model.asMap().get("adminUser");
                model.addAttribute( "loginUser", adminUser);
                initPagination(model);
                return "admin";
            }
            else{
                return "index";
            }
        }
        else{
            return "login_fp";
        }

    }

    private boolean validateSession(Model model){

        if (model.containsAttribute("loginUser")){
            UserInfo userInfo = (UserInfo) model.asMap().get("loginUser");
            if ( userInfo==null ){
                return false;
            }
            else{
                return true;
            }
        }
        else{
            return false;
        }

    }

    @RequestMapping(value="/index", method = RequestMethod.POST )
    public String logon( UserInfo userInfo, Model model ) {

        log.info("Fmd to be verified:" + userInfo.getFmdXml());
        UserInfo matchedUserInfo = fingerPrintService.verifyUserFingerPrint( userInfo.getFmdXml(), userInfo.getUserId());

//        String[] fpEnrollment = fingerPrintService.loadUserFingerPrint( userInfo.getUserId() );
//        if ( fpEnrollment==null||fpEnrollment.length == 0 ) {
//            return "cannot find fingerprint on server for user:" + userInfo.getUserId();
//        }
//
//        String fpToVerify = userInfo.getFmdXml();

        if ( matchedUserInfo!=null ) {
            model.addAttribute("loginUser", matchedUserInfo);

            CrUserInfo crUserInfo = fingerPrintService.getCrUserInfo(matchedUserInfo.getUserId() );
            if ( crUserInfo!=null && UserRole.ADMIN.equalsIgnoreCase(crUserInfo.getRole()) ){
//                List<UserInfo> userInfoList = fingerPrintService.queryUserInfo("");
//                model.addAttribute("userInfoList", userInfoList);
                initPagination(model);
                model.addAttribute("adminUser", matchedUserInfo);
                return "admin";
            }
            else {
                return "index";
            }
        }
        else {
            model.addAttribute("loginFailInfo", "指纹验证失败,请重试");
            return "login_fp";
        }

    }

    private void initPagination(Model model){

        long page = 1;
        int offset = 0;
        int pageSize = 6;
        int lastPage = 0;

        List<UserInfo> userInfoList = fingerPrintService.queryUserInfo("");
        int totalList =0;
        List<UserInfo> resultList = new ArrayList();

        if (userInfoList!=null){
            totalList = userInfoList.size();
            for ( int i= offset; i< (offset+pageSize)&&i<totalList; i++ ){
                resultList.add( userInfoList.get(i) );
            }
        }

        model.addAttribute("userInfoList", resultList);

        model.addAttribute("userKeyword", "");
        model.addAttribute("currPageNo", page);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalList", totalList);
        model.addAttribute("totalPage", (int) Math.ceil((double) totalList / pageSize));
        if (page == (int) Math.ceil((double) totalList / pageSize)) {
            lastPage = 1;
        }
        model.addAttribute("lastPage", lastPage);

    }

    @RequestMapping("/enroll")
    public String enrollFingerPrint( @RequestParam(value="userId", required = false) String userId,
                                     Model model ) {

        if ( !validateSession(model) ){
            return "login_fp";
        }

        UserEnrollment userEnrollment;
        if (!StringUtils.isEmpty(userId)) {
            UserInfo userInfo = new UserInfo();
            userInfo.setUserId(userId);
            userEnrollment = fingerPrintService.getUserEnrollment(userInfo);
            userInfo.setUserName( userEnrollment.getUserName() );
            userInfo.setFingerIndex( userEnrollment.getFmdXmls().size() );
            model.addAttribute("loginUser", userInfo );
        }
        else{
            UserInfo userInfo = (UserInfo) model.asMap().get("loginUser");
            userEnrollment = fingerPrintService.getUserEnrollment(userInfo);
        }
        model.addAttribute("enrollment", userEnrollment );
        return "enrollment";

    }

    @RequestMapping("/enrollUser")
    public String enrollUser(CrUserInfo crUserInfo, Model model ) {

        CrUserInfo matchedCrUserInfo = fingerPrintService.getCrUserInfo(crUserInfo.getLoginName());

        if ( matchedCrUserInfo!=null ) {
            UserInfo verifiedUserInfo = fingerPrintService.getUserInfoFromCrUser(matchedCrUserInfo);

            if (verifiedUserInfo !=null){
                model.addAttribute("loginUser", verifiedUserInfo);
                //model.addAttribute("userId", matchedUserInfo.getUserId() );

                if (UserRole.ADMIN.equalsIgnoreCase(matchedCrUserInfo.getRole()) ){
//                    List<UserInfo> userInfoList = fingerPrintService.queryUserInfo("");
//                    model.addAttribute("userInfoList", userInfoList);
                    initPagination(model);
                    model.addAttribute("adminUser", verifiedUserInfo);
                    return "admin";
                }
                else{
//                    UserEnrollment userEnrollment = fingerPrintService.getUserEnrollment(verifiedUserInfo);
//                    model.addAttribute("enrollment", userEnrollment );
                    return "index";
                }
            }

        }

        model.addAttribute("loginFailInfo", "用户或密码错误,请重试");
        return "login_pw";

    }


    @RequestMapping("/login")
    public String index(HttpServletRequest request, Model model, SessionStatus status) {

        status.setComplete();
        //model.addAttribute("activeXUrl", activeXUrl);
        model.asMap().remove("loginUser");
        model.asMap().remove("adminUser");

        model.addAttribute("loginFailInfo", "");
        return "login_fp";

    }

    @RequestMapping("/login_pw")
    public String indexPassword(HttpServletRequest request, Model model, SessionStatus status) {

        status.setComplete();
        model.asMap().remove("loginUser");
        model.asMap().remove("adminUser");

        model.addAttribute("loginFailInfo", "");
        return "login_pw";

    }


    /**
     * Register finger print with user info
     *
     * @param fingerIndex
     * @return
     */
    @RequestMapping(value="/capture", method = RequestMethod.GET )
    public String captureFingerPrint( @NotBlank @RequestParam("fingerIndex") final int fingerIndex,
                                      Model model ) {

        if ( !validateSession(model) ){
            return "login_fp";
        }

        int imgIndex = fingerIndex>4? 15-fingerIndex:fingerIndex+1;
        model.addAttribute("fingerIndex", fingerIndex);
        model.addAttribute("imgIndex", imgIndex);
        return "capture";

    }

    /**
     * Register finger print with user info
     *
     * @param userInfo
     * @return
     */
    @RequestMapping(value="/register", method = RequestMethod.POST )
    public String registerFingerPrint( UserInfo userInfo, Model model) {

        if ( !validateSession(model) ){
            return "login_fp";
        }

        fingerPrintService.enrollUserFingerPrint( userInfo );

        UserEnrollment userEnrollment = fingerPrintService.getUserEnrollment(userInfo);

        /**Update registered finger count */
        UserInfo loginUser = (UserInfo) model.asMap().get("loginUser");
        loginUser.setFingerIndex( userEnrollment.getFmdXmls().size() );
        model.addAttribute("loginUser", loginUser);

        model.addAttribute("enrollment", userEnrollment );

        return "enrollment";

    }

    @RequestMapping(value="/adminsearch")
    public String search(@RequestParam(value="userKeyword", required = false)String userKeyword,
                         @RequestParam(value = "currPageNo", required = false, defaultValue = "1") int currPageNo,
                         @RequestParam(value = "type", required = false, defaultValue = "1") int type,
                         Model model) {

        if ( !validateSession(model) ){
            return "login_fp";
        }

        long page = currPageNo;
        int offset = 0;
        int pageSize = 6;
        int lastPage = 0;
        if (page < 1) {
            page = 1;
        }
        if (type == 2) {
            offset = (currPageNo - 2) * pageSize;
            page = currPageNo - 1;
        } else if (type == 3) {
            offset = (currPageNo) * pageSize;
            page = currPageNo + 1;
        } else if (type == 4) {
            offset = (currPageNo - 1) * pageSize;
            page = currPageNo;
        }

        List<UserInfo> userInfoList = fingerPrintService.queryUserInfo(userKeyword);
        int totalList =0;
        List<UserInfo> resultList = new ArrayList();

        if (userInfoList!=null){
            totalList = userInfoList.size();
            for ( int i= offset; i< (offset+pageSize)&&i<totalList; i++ ){
                resultList.add( userInfoList.get(i) );
            }
        }
        model.addAttribute("userInfoList", resultList);

        model.addAttribute("userKeyword", userKeyword);
        model.addAttribute("currPageNo", page);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalList", totalList);
        model.addAttribute("totalPage", (int) Math.ceil((double) totalList / pageSize));
        if (page == (int) Math.ceil((double) totalList / pageSize)) {
            lastPage = 1;
        }
        model.addAttribute("lastPage", lastPage);
        return "admin";

    }


    /**
     * demo page for registering finger print
     *
     * @Deprecated
     * @return
     */
    @RequestMapping(value="/reg" )
    public String registerDemo(Model model) {

        int fingerIndex = 8;
        int imgIndex = fingerIndex>4? 15-fingerIndex:fingerIndex+1;
        model.addAttribute("fingerIndex", fingerIndex);
        model.addAttribute("imgIndex", imgIndex);

        UserInfo sampleUser = new UserInfo();
        sampleUser.setUserId( "abc" );
        sampleUser.setUserName( "测试");
        model.addAttribute("loginUser", sampleUser);
        return "register";

    }

    /**
     * demo page for registering finger print
     *
     * @Deprecated
     * @return
     */
    @RequestMapping(value="/test" )
    public String testOCX() {

        //return "testOCX";
        return "OcxDemo";

    }

}
