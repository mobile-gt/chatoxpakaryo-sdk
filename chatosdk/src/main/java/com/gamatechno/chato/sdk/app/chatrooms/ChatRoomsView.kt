package com.gamatechno.chato.sdk.app.chatrooms

import com.gamatechno.chato.sdk.app.chatrooms.uimodel.ChatRoomsUiModel
import com.gamatechno.chato.sdk.module.core.BaseView

interface ChatRoomsView {
    interface ObrolanPresenter {
        fun requestObrolan(isRefresh: Boolean, keyword: String?, categories: String)
        fun pinChatRoom(chatRoomUiModel: ChatRoomsUiModel?, total: Int)
        fun deleteRoom(chatRoomUiModel: ChatRoomsUiModel?)
    }

    interface View : BaseView {
        fun onRequestObrolan(list: List<ChatRoomsUiModel?>?, isRefresh: Boolean)
        fun onFailedRequestObrolan()
        fun successPinnedChatRoom(message: String?)
        fun onDeleteRoom(isSuccess: Boolean, message: String?)
    }
}