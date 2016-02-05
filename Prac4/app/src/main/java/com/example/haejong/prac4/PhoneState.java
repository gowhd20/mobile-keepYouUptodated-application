package com.example.haejong.prac4;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;




/**
 * Created by haejong on 14/01/2016.
    */
    public class PhoneState extends PhoneStateListener {

        private static final String TAG = "PhoneState";
        private static Context context;
        public int isMobileCalling = 0;
        private String callerName;

        private static final String[] PROJECTION =
            {
                    ContactsContract.PhoneLookup.DISPLAY_NAME,
                    ContactsContract.PhoneLookup._ID

            };



    public PhoneState(Context context){
        super();
        this.context = context;

    }



    @Override
    public void onCallStateChanged(int state, String incomingNumber){
        super.onCallStateChanged(state, incomingNumber);
        Log.i(TAG, incomingNumber);

        Log.i(TAG, Integer.toString(state));
        switch (state) {

            case TelephonyManager.CALL_STATE_IDLE:
                //when Idle i.e no call
                isMobileCalling = 0;
                Log.i(TAG, "Phone state Idle");

                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                //when Off hook i.e in call
                //Make intent and start your service here
                Log.i(TAG, "Phone state Off hook");
                break;
            case TelephonyManager.CALL_STATE_RINGING:
                //when Ringing
                Log.i(TAG, "Phone state Ringing");
                callerName = queryNameByNumber(incomingNumber);
                callFacebookService();

                break;
            default:
                break;
        }

    }

    public void callFacebookService(){
        if(isMobileCalling == 0) {
            if(!callerName.isEmpty()) {
                Intent fbIntent = new Intent(context, FacebookService.class);
                fbIntent.putExtra("name", callerName);
                context.sendBroadcast(fbIntent);
            }
            else {
                Log.i(TAG, "cannot find caller's name");
            }
            isMobileCalling = 1;
        }
    }

    public String queryNameByNumber(String number){

        String incomingNumber = number;
        String name = null;
        String id = null;

        Cursor cur = null;
        ContentResolver cr = this.context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(incomingNumber));
        cur = cr.query(uri, PROJECTION, null, null, null);


        if (cur.moveToNext()) {

            id = cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup._ID));
            name = cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
            Log.i(TAG, name+" "+ id);
        }

        return name;
    }


}
