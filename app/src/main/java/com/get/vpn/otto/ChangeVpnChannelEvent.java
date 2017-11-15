package com.get.vpn.otto;

import com.get.vpn.model.VpnModel;

/**
 * @author yu.jingye
 * @version created at 2016/10/13.
 */
public class ChangeVpnChannelEvent {

    private VpnModel vpnModel;

    public ChangeVpnChannelEvent(VpnModel vpnModel) {
        this.vpnModel = vpnModel;
    }

    public VpnModel getVpnModel() {
        return vpnModel;
    }
}
