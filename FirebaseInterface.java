package com.lpointdsc.aos;

import android.app.Activity;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

public class FirebaseInterface {
    private Activity mActivity;
    private FirebaseAnalytics mFirebaseAnalytics;

    public FirebaseInterface(Activity activity)
    {
        mActivity = activity;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(activity.getApplicationContext());
    }
    public FirebaseInterface(Activity activity, String user_id)
    {
        mActivity = activity;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(activity.getApplicationContext());
        mFirebaseAnalytics.setUserId(user_id);

    }

    public void setUserProperty(String user_key, String user_value)
    {
        if (mFirebaseAnalytics != null) {
            mFirebaseAnalytics.setUserProperty(user_key, user_value);
        }
    }

    public void SendFirebaseEvent(String type, String param1, String param2, String param3)
    {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(mActivity);

        Bundle params = new Bundle();



        if (type.equals("PAGEVIEW")) {
            params.putString("page_location", param1);
            params.putString("page_title", param2);
        } else if (type.equals("EVENT")) {
            params.putString("eventCategory", param1);
            params.putString("eventAction", param2);
            params.putString("eventLabel", param3);
        }

        mFirebaseAnalytics.logEvent(type, params);
    }
}