package com.cmx.shiroweb.chat.component.facilities;

import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.Data;

import java.util.HashMap;

@Data
public class NormalRoom extends ChatRoom {

    /**
     * 当前房间容量
     */
    private Integer maxCapability;

    public NormalRoom(String roomId, String roomName){
        this.roomId = roomId;
        this.roomName = roomName;
        this.roomChannelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
        this.userList = new HashMap<>();
    }
}
