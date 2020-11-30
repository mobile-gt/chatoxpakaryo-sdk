package com.gamatechno.chato.sdk.app.chatrooms.helper;

import com.gamatechno.chato.sdk.app.chatrooms.uimodel.ChatRoomsUiModel;
import com.gamatechno.chato.sdk.data.DAO.Chat.Chat;

import java.util.ArrayList;
import java.util.List;

public class ChatRoomsHelper {
    public  static List<Chat> sampleChat(){
        List<Chat> sampleChat = new ArrayList<>();

        Chat chat = new Chat(1, 1, 2, "ini Aku", "message", "", "DELIVERED", "2019-04-09", "00:00:01");
        sampleChat.add(chat);
        chat = new Chat(2,2, 1, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", "message", "", "DELIVERED", "2019-04-09", "00:00:01");
        sampleChat.add(chat);
        chat = new Chat(3,2, 1, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.", "image", "https://www.gamatechno.com/images/2016_07_11_General_Meeting_1.JPG", "DELIVERED", "2019-04-09", "00:00:01");
        sampleChat.add(chat);

        return sampleChat;
    }

    public static int totalPinnedChatRoom(List<ChatRoomsUiModel> list){
        int total = 0;
        for (ChatRoomsUiModel m : list){
            if(m.getRoomChat().getIs_pined() == 1){
                total = total+1;
            }
        }
        return total;
    }

    public static boolean isChatRoomClicked(List<ChatRoomsUiModel> list){
        for (ChatRoomsUiModel m : list){
            if(m.getIs_checked()){
                return true;
            }
        }
        return false;
    }

    public static ChatRoomsUiModel getChatRoomClicked(List<ChatRoomsUiModel> lists){
        for (ChatRoomsUiModel m : lists){
            if(m.getIs_checked()){
                return m;
            }
        }
        return null;
    }

    public static int getIndexChatRoom(List<ChatRoomsUiModel> chatRoomUiModelList, ChatRoomsUiModel chatRoomUiModel){
        for (int i = 0; i < chatRoomUiModelList.size(); i++) {
            if(chatRoomUiModel.getRoomChat().getRoom_id() == chatRoomUiModelList.get(i).getRoomChat().getRoom_id()){
                return i;
            }
        }
        return -1;
    }


}
