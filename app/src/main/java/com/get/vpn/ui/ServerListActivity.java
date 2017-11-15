package com.get.vpn.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.UUID;

/**
 * Created by istmedia-m1 on 2/16/17.
 */

public class ServerListActivity extends SingleFragmentActivity {
    public static final String TAG="SingleFragmentActivity";

    private static final String EXTRA_SERVER_ID_IN_LIST = "com.hello.escer.squidvpn.serverlist.id";

    public static Intent newIntent(Context packageContent, UUID serverId) {
        Intent intent = new Intent(packageContent, ServerListActivity.class);
        intent.putExtra(EXTRA_SERVER_ID_IN_LIST, serverId);
        return intent;
    }


    @Override
    protected Fragment createFragment() {
        return ServerListFragment.newInstance();
    }
}
