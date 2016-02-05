package com.example.haejong.prac4;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Created by haejong on 14/01/2016.
 */

public class Broadcast extends BroadcastReceiver {
    private static final String TAG = "Broadcast";
    @Override
    public void onReceive(Context context, Intent intent){
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(new PhoneState(context), PhoneStateListener.LISTEN_CALL_STATE);
        Log.i(TAG,"log test");
    }
}
