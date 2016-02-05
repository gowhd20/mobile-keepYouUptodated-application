package com.example.haejong.prac4;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestBatch;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by haejong on 23/01/2016.
 */
public class FacebookService extends BroadcastReceiver implements Parcelable  {

    private static final String TAG = "FacebookService";


    public AccessToken access_token;
    public String search_access_token ;
    private GraphRequestBatch batch;
    public String callerFbId;
    public String errorMsg;
    public String recentPost;
    public String callerName;
    private int numMessages = 1;
    private Context context;
    private String callerFbLink;



    @Override
    public void onReceive(Context context, Intent intent){
        //access_token = intent.getParcelableExtra("access_token_obj");
        this.context = context;
        access_token = AccessToken.getCurrentAccessToken();
        Bundle nameBundle;
        nameBundle = intent.getExtras();
        callerName = nameBundle.getString("name");
        fetchData();
    }



    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO Auto-generated method stub

    }

    public void fetchData(){
        getBatches();
        // read me
        GraphRequest request_me = batch.get(0);
        // search a user
        GraphRequest request_person = batch.get(1);
        // get test users
        GraphRequest request_test = batch.get(2);
        // query app access token
        GraphRequest request_app_access = batch.get(3);

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name, email, birthday,friends,posts");
        request_me.setParameters(parameters);
        //request_me.executeAsync();


        Bundle bundle = new Bundle();

        String appId = "942077259214805";
        String appSecret = "5a5dbc7cbea58d9a2a64a69d3a045796";

        bundle.putString("client_id", appId);
        bundle.putString("client_secret", appSecret);
        bundle.putString("grant_type", "client_credentials");
        request_app_access.setParameters(bundle);


        Bundle params = new Bundle();

        params.putString("type", "user");
        params.putString("q", "Denzil Ferreira");        // should be replaced by params.putString("q",callerName); when testing real users
        params.putString("fields", "id, name, email, feed,birthday,link");
        request_person.setParameters(params);
        //request_person.executeAsync();


        Bundle params2 = new Bundle();
        params2.putString("installed", "true");
        params2.putString("access_token", "942077259214805|gNQ60t15oAyC5AdIF8tOIAlcNhg");
        request_test.setParameters(params2);
    }

    public void getBatches() {
        batch = new GraphRequestBatch(
                // read me
                GraphRequest.newMeRequest(
                        AccessToken.getCurrentAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject jsonObject,
                                    GraphResponse response) {
                                try {
                                    Log.i(TAG, response.toString());
                                    JSONObject friends = jsonObject.getJSONObject("friends");

                                    Log.i(TAG, friends.toString());


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                // Application code for user
                            }
                        }),

                // search a user
                new GraphRequest(
                        AccessToken.getCurrentAccessToken(),
                        "/search",
                        null,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                                Log.i(TAG, "search a person completed");
                                Log.i(TAG, response.toString());
                                //Log.i(TAG, AccessToken.getCurrentAccessToken().getToken().toString());
                                try {
                                    JSONObject getObj = response.getJSONObject();
                                    JSONArray getArr = getObj.getJSONArray("data");
                                    JSONObject getIdObj = getArr.getJSONObject(0);

                                    callerFbId = getIdObj.get("id").toString();
                                    //callerFbLink = getIdObj.get("link").toString();
                                    search_access_token = getIdObj.get("access_token").toString();   // this to get access token from caller
                                    Log.i(TAG, getArr.get(0).toString());
                                } catch (Exception e) {
                                    errorMsg = "user not exist or has not permitted the app accessing to user info";
                                    e.printStackTrace();
                                }


                            }
                        }),

                        /* make the API call */
                // search test users
                new GraphRequest(
                        AccessToken.getCurrentAccessToken(),
                        "942077259214805/accounts/test-users",
                        null,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                                Log.i(TAG, response.toString());
                                try {
                                    JSONObject getObj = response.getJSONObject();
                                    JSONArray getArr = getObj.getJSONArray("data");
                                    JSONObject getIdObj = getArr.getJSONObject(0);
                                    Log.i(TAG, getIdObj.toString());
                                    search_access_token = getIdObj.get("access_token").toString();            // need this to access to the feed
                                    callerFbLink = getIdObj.get("login_url").toString();     // will be removed when testing real user

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        }),

                // get auth
                new GraphRequest(

                        AccessToken.getCurrentAccessToken(),
                        "oauth/access_token",
                        null,
                        HttpMethod.POST,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                                Log.i(TAG, response.toString());
                                try {
                                    JSONObject getObj = response.getJSONObject();
                                    Log.i(TAG, getObj.get("access_token").toString());
                                    //JSONArray getObjArr = getObj.getJSONArray("access_token");
                                    //AccessToken getObj2 = (AccessToken)getObjArr.get(1);

                                }catch(Exception e){
                                    Log.i(TAG, "something wrong with getting auth");
                                }

                          /* handle the result */
                            }

                        })

        );
        batch.addCallback(new GraphRequestBatch.Callback() {
            @Override
            public void onBatchCompleted(GraphRequestBatch graphRequests) {
                // Application code for when the batch finishes
                //if (callerFbId != null) {

                // read user
                Bundle params = new Bundle();
                //params.putString("installed", "true");
                params.putString("access_token", search_access_token);
                Log.i(TAG, "user query");

                new GraphRequest(
                        null,
                        "/124035871312808/feed",        // "/"+ callerFbId +"/feed"
                        params,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                                Log.i(TAG, response.toString());
                                try{
                                    JSONObject getObj = response.getJSONObject();
                                    JSONArray getArr = getObj.getJSONArray("data");
                                    JSONObject getIdObj = getArr.getJSONObject(0);
                                    Log.i(TAG, getIdObj.toString());
                                    recentPost = getIdObj.get("message").toString();
                                    //
                                    displayNotification(context, recentPost);
                                }catch(Exception e){
                                    Log.i(TAG, "something wrong with fetching post");
                                }
                            }
                        }
                ).executeAsync();

                }

        });
        batch.executeAsync();
    }

    protected void displayNotification(Context context, String text) {
        Log.i("Start", "notification");
   /* Invoking the default notification service */
        NotificationCompat.Builder  mBuilder = new NotificationCompat.Builder(context);

        mBuilder.setContentTitle(text);
        mBuilder.setContentText("caller's status on facebook");
        mBuilder.setTicker("New Message Alert!");
        mBuilder.setSmallIcon(R.drawable.dog);

   /* Increase notification number every time a new notification arrives */
        mBuilder.setNumber(++numMessages);

   /* Add Big View Specific Configuration */
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        String[] events = new String[numMessages];
        events[0] = new String(text);


        // Sets a title for the Inbox style big view
        inboxStyle.setBigContentTitle("Big Title Details:");

        // Moves events into the big view
        for (int i=0; i < events.length; i++) {
            inboxStyle.addLine(events[i]);
        }

        mBuilder.setStyle(inboxStyle);

         /* Creates an explicit intent for an Activity in your app */
        /* Adds the Intent that starts the Activity to the top of the stack */
        if(!callerFbLink.isEmpty()) {

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(callerFbLink));
            //browserIntent.putExtra("access_token_obj", AccessToken.getCurrentAccessToken());
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addNextIntent(browserIntent);
            PendingIntent resultPendingIntent =stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            mBuilder.setContentIntent(resultPendingIntent);

        }else{
            // if link is empty, open an empty activity instead
            Intent resultIntent = new Intent(context, ResultActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

            stackBuilder.addParentStack(ResultActivity.class);

            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            mBuilder.setContentIntent(resultPendingIntent);

        }


        NotificationManager mNotificationManager;

        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

   /* notificationID allows you to update the notification later on. */
        mNotificationManager.notify(9999, mBuilder.build());
    }

}
