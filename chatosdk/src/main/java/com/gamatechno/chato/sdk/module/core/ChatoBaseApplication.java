package com.gamatechno.chato.sdk.module.core;

import android.app.Application;
import android.util.Log;

import androidx.multidex.MultiDexApplication;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

//import cat.ereza.customactivityoncrash.config.CaocConfig;

/**
 * Created by root on 2/27/18.
 */

public class ChatoBaseApplication extends Application {
//    private static final int TIMEOUT_MS = 25000; // 45second
    private static final int TIMEOUT_MS = 60000; // 45second

    private RequestQueue requestQueue;
    private static ChatoBaseApplication instance;

    public ChatoBaseApplication() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        CaocConfig.Builder.create()
//                .backgroundMode(CaocConfig.BACKGROUND_MODE_SILENT) //default: CaocConfig.BACKGROUND_MODE_SHOW_CUSTOM
//                .errorDrawable(R.drawable.ic_bug_fixed)
////                .enabled(false) //default: true
//                .showErrorDetails(false) //default: true
//                //.showRestartButton(false) //default: true
////                .errorDrawable(R.drawable.logo_lestari) //default: bug image
//                .trackActivities(true) //default: false
//                //.minTimeBetweenCrashesMs(2000) //default: 3000
////                .errorDrawable(R.drawable.error) //default: bug image
//                //.restartActivity(LandingActivity.class) //default: null (your app's launch activity)
//                //.errorActivity(CustomErrorHandlerActivity.class) //default: null (default error activity)
//                //.eventListener(new YourCustomEventListener()) //default: null
//                .apply();
        instance = this;
    }

    public static synchronized ChatoBaseApplication getInstance() {
        return instance;
    }

    /**
     * Get RequestQueue
     *
     * @return
     */
    public RequestQueue getChatoRequestQueue() {
        Log.d("BaseApplication", "getRequestQueue : ");
        if (requestQueue == null) {
            Log.d("BaseApplication",
                    "getRequestQueue : make new instance ");
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return requestQueue;
    }

    /**
     * add to request using tag
     *
     * @param request
     * @param tag
     */
    public <T> void addToChatoRequestQueue(Request<T> request, String tag) {
        request.setTag(tag);

        // set retry policy
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        request.setShouldCache(false);

        Log.d("BaseApplication",
                "addToRequestQueue : " + request.getUrl());
        getChatoRequestQueue().add(request);
    }

    public void cancelPendingChatoRequest() {
        if (requestQueue != null)
            requestQueue.cancelAll(new RequestQueue.RequestFilter() {
                @Override
                public boolean apply(Request<?> request) {
                    return true;
                }
            });
    }

    public void cancelPendingChatoRequest(Object tag) {
        if (requestQueue != null)
            requestQueue.cancelAll(tag);
    }
}
