package com.get.vpn.ui;

import android.support.v4.app.Fragment;

/**
 * Created by istmedia-m1 on 2/18/17.
 */

public class AboutUsActivity extends SingleFragmentActivity {

    public static final String TAG="AboutUsActivity";

    @Override
    protected Fragment createFragment() {
        return AboutUsFragment.newInstance();
    }

}
