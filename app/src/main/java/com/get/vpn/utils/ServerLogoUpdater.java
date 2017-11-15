package com.get.vpn.utils;

import com.get.vpn.R;
import com.get.vpn.model.VpnModel;

/**
 * Created by istmedia-m1 on 4/10/17.
 */

public class ServerLogoUpdater {
    public static int GetServerLogoRid(VpnModel vpnModel){
        if (vpnModel == null)
            return (R.drawable.ic_launcher);

        if (vpnModel.getCountryCode().equalsIgnoreCase("at")) {
            return (R.drawable.at);
        } else if (vpnModel.getCountryCode().equalsIgnoreCase("au")) {
            return (R.drawable.au);
        } else if (vpnModel.getCountryCode().equalsIgnoreCase("br")) {
            return (R.drawable.br);
        } else if (vpnModel.getCountryCode().equalsIgnoreCase("ca")) {
            return (R.drawable.ca);
        } else if (vpnModel.getCountryCode().equalsIgnoreCase("cn")) {
            return (R.drawable.cn);
        } else if (vpnModel.getCountryCode().equalsIgnoreCase("de")) {
            return (R.drawable.de);
        } else if (vpnModel.getCountryCode().equalsIgnoreCase("es")) {
            return (R.drawable.es);
        } else if (vpnModel.getCountryCode().equalsIgnoreCase("fr")) {
            return (R.drawable.fr);
        } else if (vpnModel.getCountryCode().equalsIgnoreCase("gb")) {
            return (R.drawable.gb);
        } else if (vpnModel.getCountryCode().equalsIgnoreCase("hk")) {
            return (R.drawable.hk);
        } else if (vpnModel.getCountryCode().equalsIgnoreCase("in")) {
            return (R.drawable.in);
        } else if (vpnModel.getCountryCode().equalsIgnoreCase("jp")) {
            return (R.drawable.jp);
        } else if (vpnModel.getCountryCode().equalsIgnoreCase("kr")) {
            return (R.drawable.kr);
        } else if (vpnModel.getCountryCode().equalsIgnoreCase("nl")) {
            return (R.drawable.nl);
        } else if (vpnModel.getCountryCode().equalsIgnoreCase("pl")) {
            return (R.drawable.pl);
        } else if (vpnModel.getCountryCode().equalsIgnoreCase("ru")) {
            return (R.drawable.ru);
        } else if (vpnModel.getCountryCode().equalsIgnoreCase("sg")) {
            return (R.drawable.sg);
        } else if (vpnModel.getCountryCode().equalsIgnoreCase("us")) {
            return (R.drawable.us);
        } else if (vpnModel.getCountryCode().equalsIgnoreCase("lt")) {
            return (R.drawable.lt);
        } else if (vpnModel.getCountryCode().equalsIgnoreCase("lu")) {
            return (R.drawable.lu);
        } else if (vpnModel.getCountryCode().equalsIgnoreCase("no")) {
            return (R.drawable.no);
        } else if (vpnModel.getCountryCode().equalsIgnoreCase("ro")) {
            return (R.drawable.ro);
        } else {
            return (R.drawable.ic_launcher);
        }

    }
}
