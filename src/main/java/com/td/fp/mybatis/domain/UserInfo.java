package com.td.fp.mybatis.domain;

import lombok.Data;


@Data
public class UserInfo {

    private int id;
    private String userId;
    private String userName;
    private int fingerIndex;
    private String fmdXml;
    private String modifyDate;

}
