package com.get.vpn.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.get.vpn.R;
import com.get.vpn.androidservice.HeartBeatService;
import com.get.vpn.utils.AppInfo;

import java.util.Locale;

/**
 * Created by istmedia-m1 on 2/16/17.
 */

public abstract class SingleFragmentActivity extends AppCompatActivity {

    protected abstract Fragment createFragment() ;

    protected OnExitFromNoti    mOnExitFromNotiListener;
    protected interface OnExitFromNoti {
        void onExit();
    }

    private Handler mHandlerSingle;
    private HeartBeatService.onHeartBeatEventListener mHeartEventListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (AppInfo.isArabic()) {
            setTheme(R.style.AppThemeAR);
        }


        mHandlerSingle = new Handler();

        setContentView(R.layout.tmp_single_fragment_activity);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if (null == fragment) {
            fragment = createFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mHeartEventListener = new HeartBeatService.onHeartBeatEventListener() {
            @Override
            public void onExitFromNoti() {
                mHandlerSingle.post(new Runnable() {
                    @Override
                    public void run() {
                        if (null != mOnExitFromNotiListener) {
                            mOnExitFromNotiListener.onExit();
                        }
                        finish();
                    }
                });
            }
        };
        HeartBeatService.addOnHeartEventListeners(mHeartEventListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        HeartBeatService.removeOnHeartEventListeners(mHeartEventListener);
    }

}
