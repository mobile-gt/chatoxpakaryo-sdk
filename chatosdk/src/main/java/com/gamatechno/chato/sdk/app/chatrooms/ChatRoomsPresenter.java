package com.gamatechno.chato.sdk.app.chatrooms;

import android.content.Context;

import com.gamatechno.chato.sdk.app.chatrooms.uimodel.ChatRoomsUiModel;
import com.gamatechno.chato.sdk.data.DAO.RoomChat.RoomChat;
import com.gamatechno.chato.sdk.data.constant.Api;
import com.gamatechno.chato.sdk.module.core.BasePresenter;
import com.gamatechno.chato.sdk.module.request.GGFWRest;
import com.gamatechno.chato.sdk.module.request.RequestInterface;
import com.gamatechno.chato.sdk.utils.ChatoUtils;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatRoomsPresenter extends BasePresenter implements ChatRoomsView.ObrolanPresenter{

    ChatRoomsView.View view;
    int page = 0;
    Gson gson;

    public ChatRoomsPresenter(Context context, ChatRoomsView.View view) {
        super(context);
        this.view = view;
        gson = new Gson();
    }

    @Override
    public void requestObrolan(boolean isRefresh, String keyword) {
        if(isRefresh)
            page = 0;
        page++;

        GGFWRest.GETAuth(Api.list_conversation(""+page, ""), getContext(), new RequestInterface.OnAuthGetRequest() {
            @Override
            public void onPreExecuted() {
                view.onLoading();
            }

            @Override
            public void onSuccess(JSONObject response) {
                view.onHideLoading();
                try {
                    if(response.getBoolean("success")){
                        List<ChatRoomsUiModel> models = new ArrayList<>();
                        JSONObject result = response.getJSONObject("result");
                        JSONArray list_user = result.getJSONArray("list_user");
                        for (int i = 0; i < list_user.length(); i++) {
                            models.add(new ChatRoomsUiModel(gson.fromJson(list_user.get(i).toString(), RoomChat.class)));
                        }
                        view.onRequestObrolan(models, isRefresh);
                    } else {
                        view.onFailedRequestObrolan();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String error) {
                view.onHideLoading();
                view.onErrorConnection(error);
            }

            @Override
            public void onUnauthorized(String error) {
                view.onAuthFailed(error);
            }

            @Override
            public Map<String, String> requestParam() {
                return null;
            }

            @Override
            public Map<String, String> requestHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + ChatoUtils.getUserLogin(getContext()).getAccess_token());
                if(customer!=null){
                    headers.put("customer_app_id", ""+customer.getCustomer_app_id());
                    headers.put("customer_secret", ""+customer.getCustomer_secret());
                }
                return headers;
            }
        });
    }

    @Override
    public void pinChatRoom(ChatRoomsUiModel chatRoomUiModel, int total) {
        GGFWRest.POST(Api.pin_chat_room(), new RequestInterface.OnPostRequest() {
            @Override
            public void onPreExecuted() {
                view.onLoading();
            }

            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (response.getBoolean("success")) {
                        view.successPinnedChatRoom(response.getString("message"));
                    } else {
                        view.successPinnedChatRoom(response.getString("message"));
                    }

                } catch (JSONException e) {
                    view.onHideLoading();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String error) {
                view.onHideLoading();
            }

            @Override
            public Map<String, String> requestParam() {
                Map<String, String> params = new HashMap<>();
                params.put("room_id", ""+chatRoomUiModel.getRoomChat().getRoom_id());
                params.put("index", ""+total);
                params.put("status", chatRoomUiModel.getRoomChat().getIs_pined() == 0 ? "PIN" : "UNPIN");
                return params;
            }

            @Override
            public Map<String, String> requestHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + ChatoUtils.getUserLogin(getContext()).getAccess_token());
                if(customer!=null){
                    headers.put("customer_app_id", ""+customer.getCustomer_app_id());
                    headers.put("customer_secret", ""+customer.getCustomer_secret());
                }
                return headers;
            }
        });
    }

    @Override
    public void deleteRoom(ChatRoomsUiModel chatRoomUiModel) {
        GGFWRest.POST(Api.delete_room(), new RequestInterface.OnPostRequest() {
            @Override
            public void onPreExecuted() {
                view.onLoading();
            }

            @Override
            public void onSuccess(JSONObject response) {
                view.onHideLoading();
                try {
                    if (response.getBoolean("success")) {
                        view.onDeleteRoom(true, response.getString("message"));
                    } else {
                        view.onDeleteRoom(false, response.getString("message"));
                    }

                } catch (JSONException e) {
                    view.onHideLoading();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String error) {
                view.onHideLoading();
            }

            @Override
            public Map<String, String> requestParam() {
                Map<String, String> params = new HashMap<>();
                params.put("room_id", ""+chatRoomUiModel.getRoomChat().getRoom_id());
                return params;
            }

            @Override
            public Map<String, String> requestHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + ChatoUtils.getUserLogin(getContext()).getAccess_token());
                if(customer!=null){
                    headers.put("customer_app_id", ""+customer.getCustomer_app_id());
                    headers.put("customer_secret", ""+customer.getCustomer_secret());
                }
                return headers;
            }
        });
    }

}
