package com.td.fp.activeX;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.SafeArray;
import com.jacob.com.Variant;
import lombok.extern.slf4j.Slf4j;


/**
 * Created by Gu pei on 2016/4/23.
 */
@Slf4j
public class FmdVerification {

    final static ActiveXComponent FMD_APP;

    static {
        log.info( "start to create ActiveX component");

        /*
        Dispatch.callN( fmdCompare, "Compare", new Object[]{fmd1, new Variant(0), fmd2, new Variant(0)} );
        Dispatch compareResult = Dispatch.call( fmdCompare, "Compare", fmd1, new Variant(0), fmd2, new Variant(0) ).toDispatch();
        Dispatch.invoke(fmdCompare, "Compare",Dispatch.Method, new Object[]{fmd1, new Variant(0), fmd2, new Variant(0)}, new int[1] );
        double score = Dispatch.call(compareResult, "Score").toDouble();
        if ( score < (2147483647/100000) ){
            log.info("matched");
        }
        */

        FMD_APP=new ActiveXComponent("UruCSharp.UruCSharpClass");
        log.info( "initialize ActiveX component success");

    }

    public static boolean verifyFingerPrint(String fmd1, String fmd2){

        boolean isVerified = Dispatch.call( FMD_APP, "Compare1", fmd1, fmd2 ).toBoolean();
        return isVerified;

    }

    public static int identifyFingerPrint( String fmdXml, String[] enrolledFmd ){

        SafeArray sa = new SafeArray(Variant.VariantString, enrolledFmd.length );
        sa.fromStringArray( enrolledFmd );
        Variant var = new Variant();
        var.putSafeArrayRef(sa);
        return Dispatch.call( FMD_APP, "Identify", fmdXml, var ).toInt();

    }

}
