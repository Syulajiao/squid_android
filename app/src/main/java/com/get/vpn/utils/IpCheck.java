package com.get.vpn.utils;

import java.util.regex.Pattern;

public class IpCheck {
    public static boolean isIPv4(String str){
        if(!Pattern.matches("[0-9]*[.][0-9]*[.][0-9]*[.][0-9]*", str))
            return false;
        else {
            String[] arrays = str.split("\\.");

            if(Integer.parseInt(arrays[0]) < 256 && arrays[0].length() <= 3
                    &&Integer.parseInt(arrays[1]) < 256 && arrays[0].length() <= 3
                    &&Integer.parseInt(arrays[2]) < 256 && arrays[0].length() <= 3
                    &&Integer.parseInt(arrays[3]) < 256 && arrays[0].length() <= 3)

                return true;

            else return false;
        }

    }

    public static boolean isIPv6(String str){
        if(!Pattern.matches( "^(((?=(?>.*?::)(?!.*::)))(::)?([0-9A-F]{1,4}::?){0,5}"
                + "|([0-9A-F]{1,4}:){6})(\\2([0-9A-F]{1,4}(::?|$)){0,2}|((25[0-5]"
                + "|(2[0-4]|1\\d|[1-9])?\\d)(\\.|$)){4}|[0-9A-F]{1,4}:[0-9A-F]{1,"
                + "4})(?<![^:]:|\\.)\\z", str))
            return false;

        return true;

    }

}
