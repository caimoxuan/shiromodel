package com.cmx.shiroweb.chat.component.facilities;


import com.cmx.shiroweb.chat.constant.DefaultConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class RoomManager {


    private Map<String, ChatRoom> roomMap = new HashMap<>();


    public void registerRoom(ChatRoom room){
        roomMap.put(room.getRoomId(), room);
    }


    public void logoutRoom(String roomId){
        roomMap.remove(roomId);
    }

    public ChatRoom getRoom(String roomId){
        return roomMap.get(roomId);
    }

    public ChatRoom getHall(){
        return roomMap.get(DefaultConstant.DEFAULT_HALL_ID);
    }

}
