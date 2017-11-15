package com.get.vpn.ui;

import android.support.v4.app.Fragment;

/**
 * Created by istmedia-m1 on 2/16/17.
 */

public class MainActivity extends SingleFragmentActivity {
    public static final String TAG="MainActivity";
    public static final int    NOTI_ID=1;


    @Override
    protected Fragment createFragment() {
        MainFragment mfg = MainFragment.newInstance();
        mOnExitFromNotiListener = mfg.GetExitListener();
        return mfg;
    }
}
