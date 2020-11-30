package com.gamatechno.chato.sdk.app.chatrooms;

import com.gamatechno.chato.sdk.app.chatrooms.uimodel.ChatRoomsUiModel;
import com.gamatechno.chato.sdk.module.core.BaseView;

import java.util.List;

public interface ChatRoomsView {
    interface ObrolanPresenter{
        void requestObrolan(boolean isRefresh, String keyword);
        void pinChatRoom(ChatRoomsUiModel chatRoomUiModel, int total);
        void deleteRoom(ChatRoomsUiModel chatRoomUiModel);
    }

    interface View extends BaseView{
        void onRequestObrolan(List<ChatRoomsUiModel> list, boolean isRefresh);
        void onFailedRequestObrolan();
        void successPinnedChatRoom(String message);
        void onDeleteRoom(boolean isSuccess, String message);
    }
 }
