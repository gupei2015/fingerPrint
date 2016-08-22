//package com.td.fp;
//
//import ch.qos.logback.core.encoder.ByteArrayUtil;
//import com.td.fp.utils.FidFileHelper;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
///**
// * Created by Administrator on 2016/4/15.
// */
//@Component
//public class InitFingerPrintApp implements CommandLineRunner {
//
//    @Value("${fidTemplate.dataFile}")
//    private String fidTemplatePath;
//
//    @Value("${fidTemplate.width}")
//    public static int fidWidth;
//
//    @Value("${fidTemplate.height}")
//    public static int fidHeight;
//
//    @Value("${fidTemplate.dpi}")
//    public static int fidDpi;
//
//    @Value("${fidTemplate.position}")
//    public static int fidPosition;
//
//    @Value("${fidTemplate.cbeff}")
//    public static int fidCbeff;
//
//    public void run(String... args) {
//
//        FidFileHelper.FidTemplateData = FidFileHelper.readRawFile( fidTemplatePath );
//
//        System.out.println("FidFileHelper.FidTemplateData:" + FidFileHelper.FidTemplateData );
//        System.out.println( "bytetohex:" + ByteArrayUtil.toHexString(FidFileHelper.FidTemplateData) );
//
//    }
//
//}