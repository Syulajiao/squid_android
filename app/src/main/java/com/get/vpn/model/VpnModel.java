package com.get.vpn.model;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author yu.jingye
 * @version created at 2016/10/8.
 */
public class VpnModel extends BaseModel {

    private String ip, password, method, countryCode, desc;
    private int port;
    private boolean auth;
    private int x, y;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setXY(int x, int y) { this.x = x; this.y = y; }
    public int  getX() { return this.x; }
    public int  getY() { return this.y; }



    public boolean isAuth() {
        return auth;
    }

    public void setAuth(boolean auth) {
        this.auth = auth;
    }

    public static List<VpnModel> parseVpnModels(String json) {
        JSONObject jsonObject = JSONObject.parseObject(json);
        if (jsonObject.getIntValue("code") == 0) {
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            Set<String> vpnSets = new LinkedHashSet<String>();
            List<VpnModel> vpnModels = new ArrayList<>();
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject vpnJson = jsonArray.getJSONObject(i);
                VpnModel tmp = parseVpnModel(vpnJson);
                if (!vpnSets.contains(tmp.desc)) {
                    vpnSets.add(tmp.desc);
                    vpnModels.add(tmp);
                }
            }
            vpnSets.clear();

            return vpnModels;
        } else {
            return null;
        }
    }

    public static VpnModel parseVpnModel(JSONObject vpnJson) {
        VpnModel vpnModel = new VpnModel();
        vpnModel.setIp(vpnJson.getString("ip"));
        vpnModel.setPort(vpnJson.getIntValue("port"));
        vpnModel.setPassword(vpnJson.getString("password"));
        vpnModel.setMethod(vpnJson.getString("method"));
        vpnModel.setAuth(vpnJson.getBoolean("auth"));
        vpnModel.setCountryCode(vpnJson.getString("countrycode"));
        vpnModel.setDesc(vpnJson.getString("desc"));
        vpnModel.setXY(vpnJson.getIntValue("android-x"),
                       vpnJson.getIntValue("android-y"));

        return vpnModel;
    }

    @Override
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ip", ip);
        jsonObject.put("port", port);
        jsonObject.put("password", password);
        jsonObject.put("method", method);
        jsonObject.put("auth", auth);
        jsonObject.put("countrycode", countryCode);
        jsonObject.put("desc", desc);
        jsonObject.put("android-x", x);
        jsonObject.put("android-y", y);
        return jsonObject.toString();
    }


}
