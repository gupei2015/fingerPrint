package com.td.fp.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2016/4/15.
 */
public class FidFileHelper {

    public static byte[] FidTemplateData = null;

    public static byte[] readRawFile(String filePath){

        File file = new File(filePath);
        if (!file.exists()){
            System.out.println("file not exists:" + filePath );
            return null;
        }

        byte[] buffer = new byte[(int) file.length() ];

        FileInputStream fi=null;
        try{
            fi = new FileInputStream(file);
            int offset = 0;

            int numRead = 0;
            while (offset < buffer.length &&
                    (numRead = fi.read(buffer, offset, buffer.length - offset)) >= 0) {
                offset += numRead;
            }

            // 确保所有数据均被读取
            if (offset != buffer.length) {
                throw new IOException("Could not completely read file "
                        + file.getName());
            }

        }
        catch(IOException ioe){
            System.out.println( "File access error:" + ioe.getMessage() );
        }
        finally{
            if ( fi != null ){
                try {
                    fi.close();
                } catch (IOException e) {
                    System.out.println( "File close error" );
                }
            }
        }

        return buffer;

    }

    public static void writeFidRawFile( byte[] fidData, String filePath ){

        try{
            FileOutputStream fos = new FileOutputStream( filePath );
            fos.write( fidData );
            fos.close();
        }
        catch(IOException ioe){
            System.out.println("write Fid data file error:" + ioe.getMessage() );
        }

    }

}
