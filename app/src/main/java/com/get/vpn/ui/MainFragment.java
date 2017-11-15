package com.get.vpn.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.facebook.appevents.AppEventsLogger;
import com.get.vpn.R;
import com.get.vpn.ServerTester.ServerTester;
import com.get.vpn.androidservice.HeartBeatService;
import com.get.vpn.core.LocalVpnService;
import com.get.vpn.model.UserModel;
import com.get.vpn.model.VpnModel;
import com.get.vpn.otto.BusProvider;
import com.get.vpn.otto.ChangeVpnChannelEvent;
import com.get.vpn.restful.IstEventRestful;
import com.get.vpn.restful.VpnRestful;
import com.get.vpn.service.VpnService;
import com.get.vpn.utils.AppInfo;
import com.get.vpn.utils.FirebaseHelper;
import com.get.vpn.utils.Installation;
import com.get.vpn.utils.ServerLogoUpdater;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.otto.Subscribe;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

/**
 * Created by istmedia-m1 on 2/16/17.
 */

public class MainFragment extends Fragment
        implements  NavigationView.OnNavigationItemSelectedListener {

    public static MainFragment newInstance() {
        Bundle args = new Bundle();

        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    // Constants
    private static final String TAG = "MainFragment";
    private static final int START_VPN_SERVICE_REQUEST_CODE = 1985;
    boolean isExitAdsShown = false;
    // Data
    private UserModel mUserModel;
    private VpnModel mLastUsedVpnModel;
    private ArrayList<VpnModel> mVpnModels = new ArrayList<>();
    private boolean mLoginSilentlyEver = false;
    private Handler mHandler = null;
    private boolean mIsCurPos2Stop = false;
    private boolean mBConnected = false;
    private long    mSend = 0;
    private long    mRec = 0;

    private boolean bVss0x06 = true;
    private String  mStr0x06Prefix;

    // UI
    private Toolbar mToolbar;
    private TextView    mTextVpnStatus;
    private ImageView   mImageViewVpnGeo;
    private TextView    mTextMainVpnDes;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView mNavigationView;
    private Button mBtnMainDiscon;
    private Button mBtnMainCon;
    private TextView    mTextUpSpeed;
    private TextView    mTextDlSpeed;

    private LinearLayout mServerChangeLayout;

    private ImageView   mbg;
    private ImageView   mC1;
    private ImageView   mC2;

    private LocalVpnService.onStatusChangedListener mListener;

    // Firebase
    private FirebaseAnalytics mFirebaseAnalytics;

    // testServer
    ServerTester mServerTester;

    @Override
    public void onCreate(@Nullable  Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this.getActivity());
        Fabric.with(this.getActivity(), new Crashlytics());

        initHandler();

        initData();
        BusProvider.getInstance().register(this);

        istEventLog();

        mServerTester = new ServerTester(mHandler);
        mServerTester.getLooper();
        mServerTester.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent heartBeatService = new Intent(getActivity(), HeartBeatService.class);
        getActivity().startService(heartBeatService);
        MobclickAgent.onResume(getActivity());
        AppEventsLogger.activateApp(getActivity());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(getActivity());
        AppEventsLogger.deactivateApp(getActivity());
    }
    @Override
    public void onDestroy() {
        BusProvider.getInstance().unregister(this);
        LocalVpnService.removeOnStatusChangedListener(mListener);
        super.onDestroy();
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item){
        int id = item.getItemId();
        switch (id) {
            /*
            case R.id.drawer_menu_account:
                Log.i(TAG, "click account menu");
                break;
            case R.id.drawer_menu_setting:
                Log.i(TAG, "click setting menu");
                break;
                */
            case R.id.drawer_menu_about:
                Intent intent = new Intent(getActivity(), AboutUsActivity.class);
                startActivity(intent);
                break;
            /*
            case R.id.drawer_menu_help:
                Log.i(TAG, "click help menu");
                break;
                */
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_main, container, false);

        //
        mTextVpnStatus = (TextView)v.findViewById(R.id.main_text_connect_status);
        // toolbar
        mToolbar = (Toolbar) v.findViewById(R.id.id_toolbar_main);
        mToolbar.setTitle("");
        mToolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);

        //enable home btn & back btn
        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //
        mDrawerLayout = (DrawerLayout)v.findViewById(R.id.drawer_left);
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),
                mDrawerLayout,
                mToolbar,
                R.string.drawer_open,
                R.string.drawer_close ) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                Log.i(TAG, "onDrawerOpened");
            }
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                Log.i(TAG, "onDrawerClosed");
            }
        };
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        // left menu
        mNavigationView = (NavigationView)v.findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        // btn main
        mBtnMainCon = (Button) v.findViewById(R.id.btn_main_connect);
        mBtnMainDiscon= (Button) v.findViewById(R.id.btn_main_disconnect);
/*
        // arabic
        ImageView imgNext = (ImageView) v.findViewById(R.id.btn_main_next);
        if (AppInfo.isArabic()) {
            imgNext.setImageResource(R.drawable.next_ar);
        }
*/
        mServerChangeLayout = (LinearLayout)v.findViewById(R.id.id_server_change);

        // main vpn geo&img
        mTextMainVpnDes = (TextView)v.findViewById(R.id.id_main_vpn_des);
        mImageViewVpnGeo = (ImageView)v.findViewById(R.id.id_main_vpn_geo);

        //
        mTextDlSpeed = (TextView)v.findViewById(R.id.id_dl_speed);
        mTextUpSpeed = (TextView)v.findViewById(R.id.id_up_speed);

        initActions();

        initCurrentMainStatus();

        updateCurrentServerInfo(mLastUsedVpnModel);

        mbg = (ImageView)v.findViewById(R.id.id_bg_geo);
        mC1 = (ImageView)v.findViewById(R.id.c1);
        mC2 = (ImageView)v.findViewById(R.id.c2);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateCurPos();
                if (LocalVpnService.IsRunning && LocalVpnService.RunningVpnModel != null)
                    updateCurPos2();
            }
        }, 1000);

        return v;
    }

    private void initHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case ServerTester.MSG_NOTI_CONNECTTIME:
                        long nConnectTime = (long)msg.obj;
                        Log.i(TAG, String.valueOf(nConnectTime));
                        if (null == getActivity()) {
                            return;
                        }
                        // log ttl to log.fastvd.com
                        long ttl = 0;
                        if (nConnectTime == -1){
                            ttl = -1;
                        }else if (nConnectTime == -2) {
                            ttl = -2;
                        }else {
                            ttl = nConnectTime / 1000000;
                        }
                        String strTtl = String.valueOf(ttl);
                        String strServer = mLastUsedVpnModel.getDesc();
                        String strIp = mLastUsedVpnModel.getIp();
                        int iPort = mLastUsedVpnModel.getPort();

                        VpnRestful.detectInfo(strIp, iPort, ttl, getActivity().getApplicationContext(), new retrofit2.Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (null != response.body()) {
                                    String body = VpnRestful.DecryptResponse(response);
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                            }
                        });

                        //success
                        if (nConnectTime>0) {
                            Intent intent = LocalVpnService.prepare(getActivity().getApplicationContext());
                            if (intent == null) {
                                stopVPNService();
                                mHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startVPNService(mLastUsedVpnModel);
                                    }
                                }, 101);

                                mHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        updateConnectingStatusText(R.string.main_text_connected);
                                        updateConnectedBtn(true);
                                        stopCurPos2();
                                        mBConnected = true;
                                        mC2.setVisibility(View.VISIBLE);
                                    }
                                }, 2000 + new Random().nextInt(3000));
                            } else {
                                startActivityForResult(intent, START_VPN_SERVICE_REQUEST_CODE);
                            }
                        }else {
                            mIsCurPos2Stop = true;
                            mBConnected = false;
                            mC2.setVisibility(View.INVISIBLE);

                            updateConnectingStatusText(R.string.main_text_failed_connected);
                            updateConnectedBtn(false);
                        }

                        break;
                    default:
                        break;
                }
            }
        };
    }

    private void initData() {
        mUserModel = VpnService.getLoginUserModel(getActivity());
        mLastUsedVpnModel = VpnService.getLastVpnModel(getActivity());

        FirebaseHelper.logUserLanguage(AppInfo.getLanguage(), mFirebaseAnalytics);

        if (null != mLastUsedVpnModel) {
            FirebaseHelper.logServerSelectAuto(mLastUsedVpnModel.getDesc(), mFirebaseAnalytics);
        }

        mListener =  new LocalVpnService.onStatusChangedListener() {
            @Override
            public void onStatusChanged(String status, Boolean isRunning) {
                if (isRunning) {
                    FirebaseHelper.logServerConnected(mLastUsedVpnModel.getDesc(), mFirebaseAnalytics);
                }
            }

            @Override
            public void onLogReceived(String logString) {

            }

            @Override
            public void onSpeedChanged(long nSend, long nRec) {
                mSend = nSend;
                mRec = nRec;
                //Log.i(TAG, String.format("Send:%d, Rec:%d", nSend, nRec));
                updateSpeedStatus();
            }
        };

        LocalVpnService.addOnStatusChangedListener(mListener);

    }

    public SingleFragmentActivity.OnExitFromNoti GetExitListener() {
        return new SingleFragmentActivity.OnExitFromNoti() {
            @Override
            public void onExit() {
                disconnectVpn();
            }
        };
    }

    private void updateConnectingStatusText(int textRes) {
        mTextVpnStatus.setText(textRes);
    }
    private void updateCurrentServerInfo(VpnModel vpnModel) {
        if (vpnModel == null)
            return ;

        mImageViewVpnGeo.setImageResource(ServerLogoUpdater.GetServerLogoRid(vpnModel));
        mTextMainVpnDes.setText(vpnModel.getDesc());
    }
    private void updateSpeedStatus() {
        String strDl = String.format("%.2f", mRec/1000.0);
        String strUp = String.format("%.2f", mSend/1000.0);

        if (null != mTextDlSpeed)
            mTextDlSpeed.setText(strDl);
        if (null != mTextUpSpeed)
            mTextUpSpeed.setText(strUp);
    }
    private void updateConnectedBtn(boolean bConnect) {
        if (bConnect) {
            mBtnMainCon.setVisibility(View.GONE);
            mBtnMainDiscon.setVisibility(View.VISIBLE);
            mBtnMainDiscon.setEnabled(true);
        } else {
            mBtnMainCon.setVisibility(View.VISIBLE);
            mBtnMainCon.setEnabled(true);
            mBtnMainDiscon.setVisibility(View.GONE);
        }
    }
    private void updateCurPos() {
        if (null == mLastUsedVpnModel)
            return;

        FrameLayout.LayoutParams mParams = (FrameLayout.LayoutParams)mC1 .getLayoutParams();

        int w2 = mbg.getWidth();
        float scale = (float)w2/720;
        mParams.leftMargin = (int)(mLastUsedVpnModel.getX()*scale);
        mParams.topMargin = (int)(mLastUsedVpnModel.getY()*scale);
        mC1.setLayoutParams(mParams);

        mC1.setVisibility(View.VISIBLE);
    }

    private void updateCurPos2() {
        if (null == mLastUsedVpnModel)
            return;

        FrameLayout.LayoutParams mParams2= (FrameLayout.LayoutParams)mC2 .getLayoutParams();

        int w2 = mbg.getWidth();
        float scale = (float)w2/720;
        mParams2.leftMargin = (int)(mLastUsedVpnModel.getX()*scale);
        mParams2.topMargin = (int)(mLastUsedVpnModel.getY()*scale);
        mC2.setLayoutParams(mParams2);
        mC2.setVisibility(View.VISIBLE);
    }

    private void initActions() {
        mBtnMainCon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mBtnMainDiscon.setVisibility(View.VISIBLE);
                mBtnMainCon.setVisibility(View.GONE);
                mBtnMainDiscon.setEnabled(false);

                if (!LocalVpnService.IsRunning) {

                    mServerTester.testConnection(mLastUsedVpnModel);
/*
                    // sandbox
                    mHandler.obtainMessage(mServerTester.MSG_NOTI_CONNECTTIME, (long)123)
                            .sendToTarget();
*/
                    startConnecting();

                    if (null != mLastUsedVpnModel) {
                        FirebaseHelper.logServerConnect(mLastUsedVpnModel.getDesc(), mFirebaseAnalytics);
                    }
                }
            }
        });

        mBtnMainDiscon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mBtnMainDiscon.setVisibility(View.GONE);
                mBtnMainCon.setVisibility(View.VISIBLE);
                mBtnMainCon.setEnabled(false);
                if (LocalVpnService.IsRunning) {
                    disconnectVpn();

                    if (null != mLastUsedVpnModel) {
                        FirebaseHelper.logServerDisconnect(mLastUsedVpnModel.getDesc(), mFirebaseAnalytics);
                    }
                }
            }
        });

        mServerChangeLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ServerListActivity.class);
                intent.putExtra("VpnModels", mVpnModels);
                if (mLastUsedVpnModel != null) {
                    intent.putExtra("CurrentDefaultVpnName", mLastUsedVpnModel.getDesc());
                    FirebaseHelper.logServerSelectFrom(mLastUsedVpnModel.getDesc(), mFirebaseAnalytics);

                } else {
                    intent.putExtra("CurrentDefaultVpnName", "");
                }


                startActivity(intent);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == START_VPN_SERVICE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                startVPNService(mLastUsedVpnModel);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //rotationAnimator.cancel();
                        //mProgressDot.setVisibility(View.INVISIBLE);
                        //mStartStopVpnBtn.setImageResource(R.drawable.bg_connected);
                        updateConnectingStatusText(R.string.main_text_connected);
                        updateConnectedBtn(true);
                        stopCurPos2();
                        mBConnected = true;
                        mC2.setVisibility(View.VISIBLE);

                        //showConnectedAds();
                    }
                }, 2000 + new Random().nextInt(3000));
            }
            return;
        }

        super.onActivityResult(requestCode, resultCode, intent);
    }

    private void initCurrentMainStatus() {
        if (LocalVpnService.IsRunning && LocalVpnService.RunningVpnModel != null) {
            //doneRefreshForFirstTime();
            Log.i(TAG, "doneRefreshForFirstTime");
        } else if (mLastUsedVpnModel != null) {
            requestVpn();
        } else {
            //mProgressBox.setVisibility(View.VISIBLE);
            //mContentBox.setVisibility(View.GONE);
            requestVpn();
        }

        if (LocalVpnService.IsRunning) {
            updateConnectedBtn(true);
            updateConnectingStatusText(R.string.main_text_connected);
        } else {
            updateConnectedBtn(false);
            updateConnectingStatusText(R.string.main_text_disconnected);
        }
    }
    private void startConnecting() {
       Log.i(TAG, "starConnecting");
        updateConnectingStatusText(R.string.main_text_connecting);
        updateCurPos2();
        mIsCurPos2Stop = false;
        startCurPos2();
    }

    private void startCurPos2() {
        if (mIsCurPos2Stop)
            return;

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (View.VISIBLE == mC2.getVisibility()) {
                    mC2.setVisibility(View.INVISIBLE);
                } else {
                    mC2.setVisibility(View.VISIBLE);
                }
                if (mBConnected) {
                    mC2.setVisibility(View.VISIBLE);
                }else {
                    startCurPos2();
                }
            }

        }, 500);
    }
    private void stopCurPos2(){
        mIsCurPos2Stop = true;
        Log.i(TAG, "stopCurPos2");
    }
    private void disconnectVpn() {
        Log.i(TAG, "disconnnectVpn");

        mIsCurPos2Stop = true;
        mBConnected = false;
        mC2.setVisibility(View.INVISIBLE);

        // reset speed status
        mSend = 0;
        mRec = 0;
        updateSpeedStatus();

        stopVPNService();
        updateConnectingStatusText(R.string.main_text_disconnected);
        updateConnectedBtn(false);
    }

    private void stopVPNService() {
        LocalVpnService.IsRunning = false;
        LocalVpnService.RunningVpnModel = null;
    }
    private void startVPNService(VpnModel vpnModel) {
        Intent heartBeatService = new Intent(getActivity(), HeartBeatService.class);
        heartBeatService.putExtra("command", HeartBeatService.CommandNoti);
        getActivity().startService(heartBeatService);

        LocalVpnService.RunningVpnModel = vpnModel;
        if (bVss0x06) {
            mStr0x06Prefix = AppInfo.get0x06Prefix(getActivity().getApplicationContext());
            LocalVpnService.StrPrefixSS = mStr0x06Prefix;
        }
        getActivity().startService(new Intent(getActivity(), LocalVpnService.class));
    }

    private void istEventLog() {
        if (!Installation.isFirstInstall(getActivity().getApplicationContext())) {
            IstEventRestful.LogInstall(AppInfo.getUUID(getActivity().getApplicationContext()),
            new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (null != response.body()) {
                        String body = response.body().toString();
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                }
            });
        }
        IstEventRestful.LogStart(AppInfo.getUUID(getActivity().getApplicationContext()),
           new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (null != response.body()) {
                        String body = response.body().toString();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
        });
    }

    private void requestVpn() {
        VpnRestful.queryServers(mUserModel.getEmail(), mUserModel.getToken(), this.getActivity().getApplicationContext(),
                new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String body;
                    if (response.body() == null){
                        //doneRefreshForFirstTime();
                        return;
                    }

                    body = VpnRestful.DecryptResponse(response);
                    if (null == body) {
                        return;
                    }

                    updateCurPos();

                    JSONObject jsonObject = JSONObject.parseObject(body);
                    if (jsonObject.getIntValue("code") == -9975
                        || jsonObject.getIntValue("code") == -9983) {
                        if (mLoginSilentlyEver) {
                            goToLogin();
                        } else {
                            mLoginSilentlyEver = true;
                            loginSilently();
                        }
                        return;
                    }

                    List<VpnModel> models = VpnModel.parseVpnModels(body);
                    if (models != null) {
                        mVpnModels.clear();
                        mVpnModels.addAll(models);

                        if (mLastUsedVpnModel == null) {
                            if (!mVpnModels.isEmpty()) {
                                mLastUsedVpnModel = mVpnModels.get(0);

                                if (null != mLastUsedVpnModel) {
                                    FirebaseHelper.logServerSelectAuto(mLastUsedVpnModel.getDesc(), mFirebaseAnalytics);
                                }

                                VpnService.setLastVpnModel(getContext(), mLastUsedVpnModel);
                                updateCurrentServerInfo(mLastUsedVpnModel);
                                updateCurPos();
                            }
                        }
                    }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //doneRefreshForFirstTime();
            }
        });
    }
    private void goToLogin() {
        Intent intent = new Intent(this.getActivity(), LoginActivity.class);
        intent.putExtra("force_login", true);
        startActivity(intent);
        getActivity().finish();
    }

    @Subscribe
    public void onVpnChannelChanged(ChangeVpnChannelEvent event) {
        String strDes = event.getVpnModel().getDesc();
        if( !strDes.equals(mLastUsedVpnModel.getDesc()) ){
            mLastUsedVpnModel = event.getVpnModel();
            LocalVpnService.RunningVpnModel = mLastUsedVpnModel;
            VpnService.setLastVpnModel(getActivity().getApplicationContext(), mLastUsedVpnModel);
            updateCurrentServerInfo(mLastUsedVpnModel);
            updateCurPos();

            FirebaseHelper.logServerSelectUser(mLastUsedVpnModel.getDesc(), mFirebaseAnalytics);

            disconnectVpn();
        }

        // change vpn channel
       // startConnecting();
    }

    private void loginSilently() {
        VpnRestful.login(mUserModel.getEmail(), VpnService.getLoginUserPwd(this.getActivity().getApplicationContext()), mUserModel.getToken(),
                    this.getActivity().getApplicationContext(),
                    new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String body = null;
                body = VpnRestful.DecryptResponse(response);
                if (null == body){
                    goToLogin();
                    return;
                }

                UserModel userModel = UserModel.parseUserModel(body);
                if (userModel != null) {
                    VpnService.setLoginUserInfo(getActivity().getApplicationContext(), body);
                    if (userModel.getCode() != 0) {
                        goToLogin();
                        return;
                    }
                    requestVpn();
                } else {
                    goToLogin();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                goToLogin();
            }
        });
    }
}
