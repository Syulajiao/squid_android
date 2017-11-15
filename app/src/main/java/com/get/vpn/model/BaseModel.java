package com.get.vpn.model;

import java.io.Serializable;

/**
 * @author yu.jingye
 * @version created at 2016/10/8.
 */
public class BaseModel implements Serializable{

    private int code;
    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


}
