package com.gamatechno.chato.sdk.app.chatrooms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gamatechno.chato.sdk.R;
import com.gamatechno.chato.sdk.app.chatroom.ChatRoomActivity;
import com.gamatechno.chato.sdk.app.chatrooms.adapter.RoomAdapter;
import com.gamatechno.chato.sdk.app.chatrooms.helper.ChatRoomsHelper;
import com.gamatechno.chato.sdk.app.chatrooms.uimodel.ChatRoomsUiModel;
import com.gamatechno.chato.sdk.app.chatrooms.viewmodel.ChatRoomsViewModel;
import com.gamatechno.chato.sdk.data.constant.StringConstant;
import com.gamatechno.chato.sdk.utils.animation.AnimationToggle;
import com.gamatechno.ggfw.utils.GGFWUtil;
import com.gamatechno.ggfw.utils.RecyclerScroll;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;

import static com.gamatechno.chato.sdk.module.request.GGFWRest.CODE_NETWORKERROR;
import static com.gamatechno.chato.sdk.module.request.GGFWRest.CODE_NOCONNECTIONERROR;
import static com.gamatechno.chato.sdk.module.request.GGFWRest.CODE_SERVERERROR;

public class ChatRoomsFragment extends Fragment implements ChatRoomsView.View {

    private static final String TAG = ChatRoomsFragment.class.getSimpleName();

    RecyclerView rv;

    RelativeLayout helper_noconversation;

    RelativeLayout helper_noconnection;

    RelativeLayout helper_servererror;

    Button btn_tryconnection;

    Button btn_tryservererror;

    AnimationToggle helper_loading_top;

    SwipeRefreshLayout swipe;

    TextView tv_startnewconversation;

    boolean isGroup = false;
    RoomAdapter roomAdapter;

    ChatRoomsPresenter obrolanPresenter;

    ChatRoomsViewModel viewModel;

    boolean isLoadMore = true;
    IntentFilter filter;

    String keyword = "";

    LinearLayoutManager linearLayoutManager;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            Log.d(TAG, "Receiving....");
            if(action.equals(StringConstant.broadcast_refresh_chat)){
                obrolanPresenter.requestObrolan(true, keyword);
            }
        }
    };

    private void registerReceiver(){
        filter = new IntentFilter();
        filter.addAction(StringConstant.broadcast_refresh_chat);
    }

    public static ChatRoomsFragment newInstance(boolean isGroup, ChatRoomsViewModel viewModel) {
        ChatRoomsFragment fragment = new ChatRoomsFragment();
        fragment.isGroup = isGroup;
        fragment.viewModel = viewModel;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_chat_rooms_fragment, container, false);
        initView(rootView);

        linearLayoutManager = new LinearLayoutManager(getContext());
        rv.setLayoutManager(linearLayoutManager);
        helper_loading_top.setInOutAnimation(R.anim.pull_in_bottom, R.anim.push_out_top);

        registerReceiver();
        initObrolan();

        rv.addOnScrollListener(new RecyclerScroll(linearLayoutManager){

            @Override
            public void show() {
//                Log.d("ChatRoomsFragment", "show: ");
                viewModel.updateScrollStatus(true);
            }

            @Override
            public void hide() {
                viewModel.updateScrollStatus(false);
            }

            @Override
            public void loadMore() {
                if(obrolanPresenter.isSuccess && !obrolanPresenter.isLoading){
                    obrolanPresenter.requestObrolan(false, "");
                }
            }
        });

        tv_startnewconversation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.updateStartChat(true);
            }
        });

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                keyword = "";
                obrolanPresenter.requestObrolan(true, "");
            }
        });

        observeViewModel();

        btn_tryservererror.setOnClickListener(onClickListener());
        btn_tryconnection.setOnClickListener(onClickListener());
        return rootView;
    }

    private void observeViewModel(){
        viewModel.initRequestDelete().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if(aBoolean){
                    if(roomAdapter.getData() != null){
                        ChatRoomsUiModel model = ChatRoomsHelper.getChatRoomClicked(roomAdapter.getData());
                        obrolanPresenter.deleteRoom(model);
                        uncheckTheChatRoom(false);
                    }
                }
            }
        });

        viewModel.initKeyword().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                if(s.equals("")){
                    keyword = s;
                    uncheckTheChatRoom(false);
                }
            }
        });

        viewModel.initRequestPin().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isRoom) {
                if(isRoom != null){
                    if(roomAdapter.getData() != null){
                        obrolanPresenter.pinChatRoom(ChatRoomsHelper.getChatRoomClicked(roomAdapter.getData()), ChatRoomsHelper.totalPinnedChatRoom(roomAdapter.getData()));
                        uncheckTheChatRoom(false);
                    }
                }
            }
        });


        viewModel.initChatRoomClickFromSearch().observe(getViewLifecycleOwner(), new Observer<ChatRoomsUiModel>() {
            @Override
            public void onChanged(@Nullable ChatRoomsUiModel model) {
                if(ChatRoomsHelper.getChatRoomClicked(roomAdapter.getData()) != null){
                    if(ChatRoomsHelper.getIndexChatRoom(roomAdapter.getData(), model) != -1){
                        if(ChatRoomsHelper.getChatRoomClicked(roomAdapter.getData()).getRoomChat().getRoom_id() == model.getRoomChat().getRoom_id()){
                            roomAdapter.getData().get(ChatRoomsHelper.getIndexChatRoom(roomAdapter.getData(), model)).setIs_checked(false);
                        } else {
                            checkTheChatRoom(ChatRoomsHelper.getIndexChatRoom(roomAdapter.getData(), model), true);
                        }
                        viewModel.updateChatRoomsLongPress(model);
                    }
                } else {
                    Intent intent = new Intent(getContext(), ChatRoomActivity.class);
                    intent.putExtra("chatroom", model);
                    startActivity(intent);
                }
                roomAdapter.notifyDataSetChanged();
            }
        });

        viewModel.initChatRoomLongPressFromSearch().observe(getViewLifecycleOwner(), new Observer<ChatRoomsUiModel>() {
            @Override
            public void onChanged(@Nullable ChatRoomsUiModel chatRoomUiModel) {
                if(ChatRoomsHelper.getChatRoomClicked(roomAdapter.getData()) != null){
                    if(ChatRoomsHelper.getIndexChatRoom(roomAdapter.getData(), chatRoomUiModel) != -1){
                        if(ChatRoomsHelper.getChatRoomClicked(roomAdapter.getData()).getRoomChat().getRoom_id() == chatRoomUiModel.getRoomChat().getRoom_id()){
                            roomAdapter.getData().get(ChatRoomsHelper.getIndexChatRoom(roomAdapter.getData(), chatRoomUiModel)).setIs_checked(false);
                        } else {
                            checkTheChatRoom(ChatRoomsHelper.getIndexChatRoom(roomAdapter.getData(), chatRoomUiModel), true);
                        }
                        viewModel.updateChatRoomsLongPress(chatRoomUiModel);
                    }
                } else {
                    if(ChatRoomsHelper.getIndexChatRoom(roomAdapter.getData(), chatRoomUiModel) != -1){
                        checkTheChatRoom(ChatRoomsHelper.getIndexChatRoom(roomAdapter.getData(), chatRoomUiModel), true);
                        viewModel.updateChatRoomsLongPress(chatRoomUiModel);
                    }
                }
                roomAdapter.notifyDataSetChanged();
            }
        });
        viewModel.initRefreshRoom().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                obrolanPresenter.requestObrolan(true, "");
            }
        });
    }

    public View.OnClickListener onClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getId() == R.id.btn_tryservererror || view.getId() == R.id.btn_tryconnection){
                    swipe.setRefreshing(false);
                    keyword = "";
                    obrolanPresenter.requestObrolan(true, "");
                }
            }
        };
    }

    private void initObrolan(){
        obrolanPresenter = new ChatRoomsPresenter(getContext(), this);
        roomAdapter = new RoomAdapter(getContext(), new RoomAdapter.OnObrolanAdapter() {
            @Override
            public void onClickObrolan(ChatRoomsUiModel model) {
                if(ChatRoomsHelper.getChatRoomClicked(roomAdapter.getData()) != null){
                    if(ChatRoomsHelper.getIndexChatRoom(roomAdapter.getData(), model) != -1){
                        if(ChatRoomsHelper.getChatRoomClicked(roomAdapter.getData()).getRoomChat().getRoom_id() == model.getRoomChat().getRoom_id()){
                            roomAdapter.getData().get(ChatRoomsHelper.getIndexChatRoom(roomAdapter.getData(), model)).setIs_checked(false);
                        } else {
                            checkTheChatRoom(ChatRoomsHelper.getIndexChatRoom(roomAdapter.getData(), model), true);
                        }
                        viewModel.updateChatRoomsLongPress(model);
                    }
                } else {
                    Intent intent = new Intent(getContext(), ChatRoomActivity.class);
                    intent.putExtra("chatroom", model);
                    startActivity(intent);
                }
                roomAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLongClick(ChatRoomsUiModel chatRoomUiModel) {
                if(ChatRoomsHelper.getChatRoomClicked(roomAdapter.getData()) != null){
                    if(ChatRoomsHelper.getIndexChatRoom(roomAdapter.getData(), chatRoomUiModel) != -1){
                        if(ChatRoomsHelper.getChatRoomClicked(roomAdapter.getData()).getRoomChat().getRoom_id() == chatRoomUiModel.getRoomChat().getRoom_id()){
                            roomAdapter.getData().get(ChatRoomsHelper.getIndexChatRoom(roomAdapter.getData(), chatRoomUiModel)).setIs_checked(false);
                        } else {
                            checkTheChatRoom(ChatRoomsHelper.getIndexChatRoom(roomAdapter.getData(), chatRoomUiModel), true);
                        }
                        viewModel.updateChatRoomsLongPress(chatRoomUiModel);
                    }
                } else {
                    if(ChatRoomsHelper.getIndexChatRoom(roomAdapter.getData(), chatRoomUiModel) != -1){
                        checkTheChatRoom(ChatRoomsHelper.getIndexChatRoom(roomAdapter.getData(), chatRoomUiModel), true);
                        viewModel.updateChatRoomsLongPress(chatRoomUiModel);
                    }
                }
                roomAdapter.notifyDataSetChanged();
            }
        });
        rv.setAdapter(roomAdapter);
    }

    private void checkTheChatRoom(int i, boolean isOK){
        for (int j = 0; j < roomAdapter.getData().size(); j++) {
            if(j == i){
                roomAdapter.getData().get(j).setIs_checked(isOK);
            } else {
                roomAdapter.getData().get(j).setIs_checked(!isOK);
            }
        }
    }

    private void uncheckTheChatRoom(boolean isOk){
        for (int j = 0; j < roomAdapter.getData().size(); j++) {
            roomAdapter.getData().get(j).setIs_checked(isOk);
        }
        roomAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoading() {
        helper_noconversation.setVisibility(View.GONE);
        helper_noconnection.setVisibility(View.GONE);
        helper_servererror.setVisibility(View.GONE);
        if(roomAdapter.getData().size() == 0){
            roomAdapter.initLoading(true);
        }
    }

    @Override
    public void onHideLoading() {
        helper_loading_top.hide();
        if(roomAdapter.getData().size() == 0){
            roomAdapter.initLoading(false);
        } else {
            if(swipe.isRefreshing()) swipe.setRefreshing(false);
        }
    }

    @Override
    public void onErrorConnection(String message) {
        if(roomAdapter.getData().size()==0){
            switch (message){
                case CODE_SERVERERROR:
                    helper_servererror.setVisibility(View.VISIBLE);
                    break;
                case CODE_NETWORKERROR:
                    helper_noconnection.setVisibility(View.VISIBLE);
                    break;
                case CODE_NOCONNECTIONERROR:
                    helper_noconnection.setVisibility(View.VISIBLE);
                    break;
                default:
                    GGFWUtil.ToastShort(getContext(), message);
                    break;
            }
        } else {
            GGFWUtil.ToastShort(getContext(), message);
        }
    }

    @Override
    public void onAuthFailed(String error) {
        GGFWUtil.ToastShort(getContext(), error);
    }

    @Override
    public void onRequestObrolan(List<ChatRoomsUiModel> list, boolean isRefresh) {
        isLoadMore = true;
        helper_noconversation.setVisibility(View.GONE);
        roomAdapter.addData(isRefresh, list);
        if(roomAdapter.getData().size() == 0)
            helper_noconversation.setVisibility(View.VISIBLE);
        else
            helper_noconversation.setVisibility(View.GONE);
    }

    @Override
    public void onFailedRequestObrolan() {
        if(roomAdapter.getData().size() == 0){
            helper_noconversation.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void successPinnedChatRoom(String message) {
        GGFWUtil.ToastShort(getContext(), message);
        obrolanPresenter.requestObrolan(true, keyword);
    }

    @Override
    public void onDeleteRoom(boolean isSuccess, String message) {
        GGFWUtil.ToastShort(getContext(), message);
        if(isSuccess){
            obrolanPresenter.requestObrolan(true, keyword);
        }
    }

    @Override
    public void onStop() {
        try {
            if(getContext()!=null)
                getContext().unregisterReceiver(receiver);
        } catch (Exception e){
            e.getMessage();
        }
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: "+FirebaseInstanceId.getInstance().getToken());
        getContext().registerReceiver(receiver, filter);
        obrolanPresenter.requestObrolan(true, keyword);
    }

    private void initView(View view){
        rv = view.findViewById(R.id.rv);
        helper_noconversation = view.findViewById(R.id.helper_noconversation);
        helper_noconnection = view.findViewById(R.id.helper_noconnection);
        helper_servererror = view.findViewById(R.id.helper_servererror);
        btn_tryconnection = view.findViewById(R.id.btn_tryconnection);
        btn_tryservererror = view.findViewById(R.id.btn_tryservererror);
        helper_loading_top = view.findViewById(R.id.helper_loading_top);
        swipe = view.findViewById(R.id.swipe);
        tv_startnewconversation = view.findViewById(R.id.tv_startnewconversation);
    }
}
