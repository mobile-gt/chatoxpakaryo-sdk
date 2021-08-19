package com.gamatechno.chato.sdk.module.service.Firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.gamatechno.chato.sdk.data.constant.Api;
import com.gamatechno.chato.sdk.module.request.GGFWRest;
import com.gamatechno.chato.sdk.module.request.RequestInterface;
import com.gamatechno.chato.sdk.utils.ChatoUtils;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FirebaseInstanceIDService extends FirebaseMessagingService {
    int repeat = 0;

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        repeat = 0;
        /*String token = FirebaseMessaging.getInstance().getToken().getResult();
        GGFWUtil.setStringToSP(getContext(), Preferences.FIREBASE_TOKEN, token);
        requestTokenRefresh(token, repeat);*/
    }

    public void requestTokenRefresh(String token, int ulang){
        if(ulang < 3){
            GGFWRest.POST(Api.update_device_token(), new RequestInterface.OnPostRequest() {
                @Override
                public void onPreExecuted() {
                    repeat++;
                }

                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        if(!response.getBoolean("success")){
                            requestTokenRefresh(token, repeat);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(String error) {
                    requestTokenRefresh(token, repeat);
                }

                @Override
                public Map<String, String> requestParam() {
                    Map<String, String> params = new HashMap<>();

                    params.put("device_token", token);

                    return params;
                }

                @Override
                public Map<String, String> requestHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + ChatoUtils.getUserLogin(getBaseContext()).getAccess_token());
                    return headers;
                }
            });
        }
    }
}
