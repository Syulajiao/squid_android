package com.get.vpn.restful;

import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;

import com.get.vpn.tunnel.shadowsocks.AesCrypt;
import com.get.vpn.tunnel.shadowsocks.CryptBase;
import com.get.vpn.tunnel.shadowsocks.CryptFactory;
import com.get.vpn.tunnel.shadowsocks.ICrypt;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by istmedia-m1 on 4/10/17.
 */

public class ApiEncrypt {
    public ApiEncrypt() {
        strGeneKey = geneCryptKey();
        mCrypt = CryptFactory.get(AesCrypt.CIPHER_AES_256_CFB, strGeneKey);
    }

    private static final String strSrcKey = "SmJQ7z3x97BLEpyk";

    public byte[] Encrypt(byte[] src){
        if (src == null || strGeneKey.isEmpty() || null == mCrypt)  {
            return null;
        }

        byte[] ret = null;
        try {
            ret =  mCrypt.encryptOnce(src);
        }
        catch (Exception e) {
            return null;
        };

        String tRet = Base64.encodeToString(ret, Base64.DEFAULT);

        return ret;
    }
    public byte[] Decrypt(byte[] src){
        if (src == null || strGeneKey.isEmpty() || null == mCrypt)  {
            return null;
        }

        byte[] ret = null;
        try {
          ret = mCrypt.decryptOnce(src);
        }
        catch (Exception e) {
            return null;
        };
        return ret;
    }

    private ICrypt mCrypt = null;
    private String strGeneKey;

    private static String geneCryptKey() {
        String str = getMD5(strSrcKey);
        if (null == str){
            return null;
        }
        for (int i=0;i<3; ++i) {
            str = getMD5(str.substring(8+i, 18+i));
        }
        return str.substring(0, 10);
    }

    private static String getMD5( String src) {
        if (src == null) {
            return null;
        }
        MessageDigest md5;
        byte[] srcByte = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            srcByte = src.getBytes("UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            return null;
        }
        catch (NoSuchAlgorithmException e){
            return null;
        }
        byte[] m = md5.digest(srcByte);
        return byteArrayToHex(m);
    }
    private static String byteArrayToHex(byte[] byteArray) {
        char[] hexDigits = {'0','1','2','3','4','5','6','7','8','9', 'a','b','c','d','e','f' };
        char[] resultCharArray =new char[byteArray.length * 2];

        int index = 0;
        for (byte b : byteArray) {
            resultCharArray[index++] = hexDigits[b>>> 4 & 0xf];
            resultCharArray[index++] = hexDigits[b& 0xf];
        }

        return new String(resultCharArray);
    }
}
