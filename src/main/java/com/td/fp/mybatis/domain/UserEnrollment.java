package com.td.fp.mybatis.domain;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gu pei on 2016/4/24.
 * User fingerprint enrollment object
 */
@Data
public class UserEnrollment {

    private int id;
    private String userId;
    private String userName;
    private String userIcon;

    private Map<Integer, String> fmdXmls = new HashMap<>();

}
