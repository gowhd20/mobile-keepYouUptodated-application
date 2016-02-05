package com.example.haejong.prac4;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;


import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;


import com.facebook.GraphRequestBatch;

import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;


import java.util.Arrays;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = "MyActivity";

    public AccessToken access_token;
    CallbackManager callbackManager;
    private LoginButton loginButton;
    private GraphRequestBatch batch;
    private String callerFbId;
    private String nameToFind;
    private MainActivity ins;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_main);

        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton)findViewById(R.id.login_button);
        List<String> permissionNeeds = Arrays.asList("user_photos", "email", "user_birthday", "public_profile", "user_friends", "user_posts");
        loginButton.setReadPermissions(permissionNeeds);


        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                access_token = loginResult.getAccessToken();

                Log.i(TAG, "Content User ID: " + loginResult.getAccessToken().getUserId() + "\n" + "Auth Token: " + access_token.getToken());
                MainActivity context = getInstance();

                Intent fbIntent = new Intent(context, FacebookService.class);
                //fbIntent.putExtra("access_token_obj", access_token);
                //sendBroadcast(fbIntent);


            }

            @Override
            public void onCancel() {
                //"If login attempt canceled.";
            }

            @Override
            public void onError(FacebookException e) {
                //"If login attempt Failed.";
            }
        });

       // registerReceiver(new FacebookLookUp(), new IntentFilter("com.hmkcode.android.USER_ACTION"));


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,
                resultCode, data);
    }

    private MainActivity getInstance(){
        return this.ins = this;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

}

