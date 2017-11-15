package com.get.vpn.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.get.vpn.R;
import com.get.vpn.androidservice.HeartBeatService;
import com.get.vpn.core.LocalVpnService;
import com.get.vpn.model.UserModel;
import com.get.vpn.restful.VpnRestful;
import com.get.vpn.service.VpnService;
import com.umeng.analytics.MobclickAgent;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity {

    private static String TAG="LoginActivity";
    // Data
    private boolean mIsSigning = false;
    private int     mLoginFailedCount = 0;
    private int     mLoginCap = 3;

    // UI references.
   // private View mProgressView;
    private Handler mHandler = new Handler();
    private HeartBeatService.onHeartBeatEventListener mHeartEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       boolean isForceLogin = getIntent().getBooleanExtra("force_login", false);

        if (!isForceLogin) {
            boolean isLessThan3Hours = System.currentTimeMillis() - VpnService.getLastLoginTime(getApplicationContext()) < 1000 * 60 * 60 * 3;
            if (LocalVpnService.IsRunning || isLessThan3Hours) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        }

        setContentView(R.layout.activity_login);
    //    mProgressView = findViewById(R.id.login_progress);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

    //    showProgress(true);
        UserModel userModel = VpnService.getFreeAccount();
        requestLogin(userModel.getEmail(), userModel.getPassword());

    }
    public void onStart() {
        super.onStart();
        mHeartEventListener = new HeartBeatService.onHeartBeatEventListener() {
            @Override
            public void onExitFromNoti() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                });
            }
        };
        HeartBeatService.addOnHeartEventListeners(mHeartEventListener);
    }

    public void onResume() {
        super.onResume();
        if (!mIsSigning) {
            showProgress(true);
            requestLoginDelay();
        }
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private void requestLoginDelay() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                UserModel userModel = VpnService.getFreeAccount();
                requestLogin(userModel.getEmail(), userModel.getPassword());
            }
        }, 500);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private void showProgress(final boolean show) {
    /*    int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
        */
    }

    private void requestLogin(final String userId, final String password) {
        mIsSigning = true;
        VpnRestful.login(userId, password, VpnService.getLastToken(getApplicationContext()), getApplicationContext(), new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                showProgress(false);
                mIsSigning = false;

                if (null == response) {
                    requestLoginDelay();
                    return;
                }

                String retBody = VpnRestful.DecryptResponse(response);
                if (null == retBody)  {
                    requestLoginDelay();
                    return;
                }

                UserModel userModel = UserModel.parseUserModel(retBody);

                if (userModel != null) {
                    VpnService.setLoginUserInfo(getApplicationContext(), retBody);
                    VpnService.setLoginUserPwd(getApplicationContext(), password);
                    if (userModel.getCode() == 0) {
                        Intent heartBeatService = new Intent(LoginActivity.this, HeartBeatService.class);
                        startService(heartBeatService);

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);

                        VpnService.setLoginTime(getApplicationContext(), System.currentTimeMillis());
                        finish();
                    } else if (userModel.getCode() == -1000) {
                        Toast.makeText(getApplicationContext(), R.string.error_login_expired, Toast.LENGTH_LONG).show();
                    } else if (userModel.getCode() == -999) {
                        Toast.makeText(getApplicationContext(), R.string.error_login_device_exceed, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.error_login_failed, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.error_login_failed, Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                showProgress(true);
                mIsSigning = false;
                mLoginFailedCount++;
                Log.i(TAG, "mLoginFailedCount:"+mLoginFailedCount);
                if (mLoginFailedCount >= mLoginCap) {
                    Toast.makeText(getApplicationContext(), R.string.error_login_net, Toast.LENGTH_LONG).show();
                    mLoginFailedCount = 0;
                    showProgress(false);
                    mIsSigning = false;
                } else {
                    requestLoginDelay();
                }

            }
        });
    }

}

