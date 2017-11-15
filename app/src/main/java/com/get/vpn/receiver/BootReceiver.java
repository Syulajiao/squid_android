package com.get.vpn.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.get.vpn.androidservice.HeartBeatService;

/**
 * @author yu.jingye
 * @version created at 2016/10/9.
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
    /*    if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            startHeartBeatService(context);
        }

        if (Intent.ACTION_USER_PRESENT.equals(intent.getAction())) {
            startHeartBeatService(context);
        }

        if (Intent.ACTION_PACKAGE_RESTARTED.equals(intent.getAction())) {
            startHeartBeatService(context);
        }
    */
    }

    private void startHeartBeatService(Context context) {
        Intent intent = new Intent(context, HeartBeatService.class);
        context.startService(intent);
    }

}
