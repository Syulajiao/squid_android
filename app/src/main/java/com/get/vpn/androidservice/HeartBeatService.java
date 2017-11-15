package com.get.vpn.androidservice;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.get.vpn.R;
import com.get.vpn.core.LocalVpnService;
import com.get.vpn.model.UserModel;
import com.get.vpn.restful.VpnRestful;
import com.get.vpn.ui.MainActivity;
import com.get.vpn.utils.ServerLogoUpdater;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.get.vpn.core.LocalVpnService.RunningVpnModel;

public class HeartBeatService extends Service implements Runnable {

    private Thread mHeartBeatThread;
    private Handler mHandler;
    public static boolean mIsStop;

    public static final int CommandClose = 1;
    public static final int CommandNoti = 2;
    public static final int MSG_SPEED_UPDATE = 11;

    private long mRec=0;
    private long mSend=0;
    private boolean mClose = false;


    private long mNotificationPeriod = 0;
    private long mNotificationPeriodMax = 4;
    Notification            mNoti;
    NotificationManager     mManager;

    private static ConcurrentHashMap<onHeartBeatEventListener, Object> mOnHeartBeatEventListeners = new ConcurrentHashMap<>();

    public static void addOnHeartEventListeners(onHeartBeatEventListener listener) {
        if (!mOnHeartBeatEventListeners.containsKey(listener)) {
            mOnHeartBeatEventListeners.put(listener,1);
        }
    }
    public static void removeOnHeartEventListeners(onHeartBeatEventListener listener) {
        if (mOnHeartBeatEventListeners.containsKey(listener)) {
            mOnHeartBeatEventListeners.remove(listener);
        }
    }

    private void onExitFromNoti() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                for (Map.Entry<onHeartBeatEventListener, Object> entry:mOnHeartBeatEventListeners.entrySet()) {
                    entry.getKey().onExitFromNoti();
                }
            }
        });
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null; }

    @Override
    public void onCreate() {
        super.onCreate();

        mHeartBeatThread = new Thread(this, "SquidvpnService");
        mHeartBeatThread.start();
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MSG_SPEED_UPDATE && null != mNoti) {

                    mNotificationPeriod++;
                    if (mNotificationPeriod >=mNotificationPeriodMax) {
                        mNotificationPeriod = 0;

                        String strDl = String.format("%.2f", mRec / 1000.0);
                        String strUp = String.format("%.2f", mSend / 1000.0);
                        mNoti.contentView.setTextViewText(R.id.id_noti_dl_speed, strDl);
                        mNoti.contentView.setTextViewText(R.id.id_noti_up_speed, strUp);
                        mManager.notify(MainActivity.NOTI_ID, mNoti);
                    }

                }
            }
        };

        mManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        showNotifycation();

        LocalVpnService.addOnStatusChangedListener(new LocalVpnService.onStatusChangedListener() {
            @Override
            public void onStatusChanged(String status, Boolean isRunning) {
                if (mClose) {
                    return;
                }
                if (isRunning && null!=LocalVpnService.RunningVpnModel) {
                    //LocalVpnService.RunningVpnModel.getCountryCode();
                    mNotificationPeriod++;

                    mNoti.contentView.setTextViewText(R.id.id_noti_server,
                                LocalVpnService.RunningVpnModel.getDesc());
                    mNoti.contentView.setImageViewResource(R.id.id_noti_serverLogo,
                                ServerLogoUpdater.GetServerLogoRid(LocalVpnService.RunningVpnModel));
                }else {
                    mNotificationPeriod = 0;
                    mNoti.contentView.setTextViewText(R.id.id_noti_up_speed, "00.00");;
                    mNoti.contentView.setTextViewText(R.id.id_noti_dl_speed, "00.00");;
                    mNoti.contentView.setTextViewText(R.id.id_noti_server, getResources().getString(R.string.main_text_disconnected));
                    mNoti.contentView.setImageViewResource(R.id.id_noti_serverLogo,
                                ServerLogoUpdater.GetServerLogoRid(null));
                }
                    mManager.notify(MainActivity.NOTI_ID, mNoti);
            }

            @Override
            public void onLogReceived(String logString) {}

            @Override
            public void onSpeedChanged(long nSend, long nRec) {
                if (mClose) {
                    return;
                }

                mSend = nSend;
                mRec = nRec;
                mHandler.obtainMessage(MSG_SPEED_UPDATE, 0)
                        .sendToTarget();
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    //    flags = START_STICKY_COMPATIBILITY
        if (intent == null) {
            return super.onStartCommand(intent, flags, startId);
        }
        mClose = false;

        int command = intent.getIntExtra("command", 0);
        if ( CommandClose == command) {
            mClose = true;
            onExitFromNoti();
            mManager.cancel(MainActivity.NOTI_ID);
            stopSelf();
        } else if (CommandNoti == command) {
        //    showNotifycation();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (mHeartBeatThread != null) {
            mHeartBeatThread.interrupt();
        }
        Log.i("HeartService", "onDestroy");
/*
        if (!mIsStop) {
            Intent intent = new Intent(getApplicationContext(), HeartBeatService.class);
            startService(intent);
        }
        */
        super.onDestroy();
    }

    @Override
    public void run() {
        while (true) {
            if (mIsStop)
                break;

            sendHeartBeat();

            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    private void sendHeartBeat() {
        UserModel userModel = com.get.vpn.service.VpnService.getLoginUserModel(getApplicationContext());

        if (userModel == null)
            return;

        String ip = "";
        if (RunningVpnModel != null) {
            ip = RunningVpnModel.getIp() + "";
        }

        VpnRestful.heartBeatReport(userModel.getEmail(), userModel.getToken(), ip, getApplicationContext(),
                new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                // TODO: 2016/10/9 suppose all requests return code 0
                if (null == response) {
                    return;
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void showNotifycation() {
        NotificationCompat.Builder  builder = new NotificationCompat.Builder(this);
        builder.setContentText("squidvpn text");
        builder.setContentTitle("squidvpn title");
        builder.setSmallIcon(R.drawable.ic_launcher_m);
        builder.setAutoCancel(false);
        builder.setOngoing(true);
        builder.setShowWhen(false);

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.custom_noti_view);

        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        intent.putExtra("from", "noti");

        PendingIntent pIntent = PendingIntent.getActivity(this, 1, intent, 0);
        builder.setContentIntent(pIntent);

        Intent  intent1 = new Intent(this, HeartBeatService.class);
        intent1.putExtra("command", CommandClose);
        PendingIntent pIntent1 = PendingIntent.getService(this, 2, intent1, 0);
        remoteViews.setOnClickPendingIntent(R.id.id_noti_close, pIntent1);

        remoteViews.setTextViewText(R.id.id_noti_server, getResources().getString(R.string.main_text_disconnected));

        builder.setContent(remoteViews);
        mNoti = builder.build();
        mManager.notify(MainActivity.NOTI_ID, mNoti);
    }

    public interface onHeartBeatEventListener {
        void onExitFromNoti();
    }
}
