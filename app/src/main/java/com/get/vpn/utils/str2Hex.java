package com.get.vpn.utils;

/**
 * Created by wanyuan on 2017/6/22.
 */

public class str2Hex {
    public static byte[] hexStr2Bytes(String strHex) {
        int m = 0, n = 0;
        int l = strHex.length()/2;

        byte[] ret = new byte[l];
        for (int i = 0; i < l; i++)
        {
            m = i*2+1;
            n = m+1;
            ret[i] = Byte.decode("0x" + strHex.substring(i*2, m) + strHex.substring(m,n));
        }
        return ret;
    }

    public static String bytes2HexString(byte[] b) {
        StringBuffer result = new StringBuffer();
        String hex;
        for (int i = 0; i < b.length; i++) {
            hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            result.append(hex.toUpperCase());
        }
        return result.toString();
    }

    public static byte[] addBytes(byte[] data1, byte[] data2) {
        byte[] data3 = new byte[data1.length + data2.length];
        System.arraycopy(data1, 0, data3, 0, data1.length);
        System.arraycopy(data2, 0, data3, data1.length, data2.length);
        return data3;

    }
}
