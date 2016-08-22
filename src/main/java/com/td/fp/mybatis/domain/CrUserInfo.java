package com.td.fp.mybatis.domain;

import lombok.Data;


@Data
public class CrUserInfo {

    private String id;
    private String loginName;
    private String userName;
    private String password;
    private String photo;
    private String role;

}
