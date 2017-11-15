package com.get.vpn.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author yu.jingye
 * @version created at 2016/10/8.
 */
public class UserModel extends BaseModel {

    private String userName, email, token, password;
    private int keepAliveInterval, maxDevices;
    private long expiredOn, lastDeviceLogin;

    public static UserModel parseUserModel(String json) {
        if (json == null) {
            return null;
        }

        UserModel userModel = new UserModel();
        try {
            JSONObject jsonObject = new JSONObject(json);
            userModel.setCode(jsonObject.optInt("code"));
            if (userModel.getCode() == 0) {
                JSONObject userJsonObj = jsonObject.optJSONObject("data");
                userModel.setEmail(userJsonObj.optString("email"));
                userModel.setExpiredOn(userJsonObj.optInt("expiredon"));
                userModel.setKeepAliveInterval(userJsonObj.optInt("keepaliveinterval"));
                userModel.setLastDeviceLogin(userJsonObj.optLong("lastdevicelogin"));
                userModel.setMaxDevices(userJsonObj.optInt("maxdevices"));
                userModel.setToken(userJsonObj.optString("token"));
                userModel.setUserName(userJsonObj.optString("username"));

                return userModel;
            } else {
                return userModel;
            }
        } catch (JSONException e) {
            return null;
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getKeepAliveInterval() {
        return keepAliveInterval;
    }

    public void setKeepAliveInterval(int keepAliveInterval) {
        this.keepAliveInterval = keepAliveInterval;
    }

    public int getMaxDevices() {
        return maxDevices;
    }

    public void setMaxDevices(int maxDevices) {
        this.maxDevices = maxDevices;
    }

    public long getExpiredOn() {
        return expiredOn;
    }

    public void setExpiredOn(long expiredOn) {
        this.expiredOn = expiredOn;
    }

    public long getLastDeviceLogin() {
        return lastDeviceLogin;
    }

    public void setLastDeviceLogin(long lastDeviceLogin) {
        this.lastDeviceLogin = lastDeviceLogin;
    }

}
