package com.gamatechno.chato.sdk.app.chatroom.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.gamatechno.chato.sdk.R;
import com.gamatechno.chato.sdk.data.DAO.Chat.Chat;

import butterknife.BindView;

public class MessageViewHolder extends BaseChatViewHolder implements BaseChatViewHolder.OnChatViewHolder {

    LinearLayout lay_chat;

    LinearLayout lay_container_chat;

    RelativeLayout lay_not_read_yet;

    LinearLayout lay_status;

    Context context;
    ChatRoomAdapter.OnChatRoomClick onChatRoomClick;

    boolean isGroup;

    public MessageViewHolder(@NonNull View itemView, Context context, ChatRoomAdapter.OnChatRoomClick onChatRoomClick) {
        super(context, itemView);
        this.setIsRecyclable(false);
        this.context = context;
        this.onChatRoomClick = onChatRoomClick;
        initView(itemView);
    }

    public MessageViewHolder(@NonNull View itemView, Context context, ChatRoomAdapter.OnChatRoomClick onChatRoomClick, boolean isGroup) {
        super(context, itemView);
        this.setIsRecyclable(false);
        this.context = context;
        this.isGroup = isGroup;
        this.onChatRoomClick = onChatRoomClick;
        initView(itemView);
    }

    public void bindData(Chat chat, int isMyChat, int position, boolean isDateShow, boolean isRect, boolean isGroupNeedName) {
        if(chat.getIs_deleted() == 1){
            super.bindDeletedData(chat, isDateShow, isRect,  isGroup, position, isMyChat, onChatRoomClick);
        } else {
            super.bindData(chat, isDateShow, isRect,  isGroup, isGroupNeedName, position, isMyChat, onChatRoomClick);
        }

    }

    private void initView(View view){
        lay_chat = view.findViewById(R.id.avatarView);
        lay_container_chat = view.findViewById(R.id.lay_container_chat);
        lay_not_read_yet = view.findViewById(R.id.lay_not_read_yet);
        lay_status = view.findViewById(R.id.lay_status);
    }
}
