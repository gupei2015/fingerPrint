package com.td.fp.jni;

/**
 * Created by Gu pei on 2016/4/23.
 */
@Deprecated
public class FmdVerification {

    public native boolean CompareFmd(String xml1, String xml2);

    public native int identifyFmd(String xml1, String[] enrollmentXml);

    public native int CompareFmdAsScore(String xml1, String xml2);

    public FmdVerification()
    {
        System.loadLibrary("fmdVerification_jni");
    }

}
