package com.gamatechno.chato.sdk.app.chatrooms.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gamatechno.chato.sdk.R;
import com.gamatechno.chato.sdk.app.chatrooms.uimodel.ChatRoomsUiModel;

import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    Context context;
    List<ChatRoomsUiModel> chatList;
    OnObrolanAdapter onObrolanAdapter;

    public RoomAdapter(Context context, List<ChatRoomsUiModel> chatList, OnObrolanAdapter onObrolanAdapter) {
        this.context = context;
        this.chatList = chatList;
        this.onObrolanAdapter = onObrolanAdapter;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_chat_rooms, viewGroup, false);
        return new ChatroomViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ChatRoomsUiModel model = chatList.get(i);
        ((ChatroomViewHolder)viewHolder).bindDatas(model);

        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                onObrolanAdapter.onLongClick(model);
                return true;
            }
        });

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onObrolanAdapter.onClickObrolan(model);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public interface OnObrolanAdapter{
        void onClickObrolan(ChatRoomsUiModel model);
        void onLongClick(ChatRoomsUiModel model);
    }
}
