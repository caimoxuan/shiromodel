package com.cmx.shiroweb.chat.component.facilities;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import lombok.Data;

import java.util.Map;

@Data
public abstract class ChatRoom {

    protected String roomId;

    protected String roomName;

    protected Map<Long, ChannelId> userList;

    public ChannelGroup roomChannelGroup;

    /**
     * 加入
     */
    public void comingIn(Long userId, ChannelHandlerContext ctx) {
        roomChannelGroup.add(ctx.channel());
        userList.put(userId, ctx.channel().id());
    }

    /**
     * 离开
     */
    public void comingOut(Long userId, ChannelHandlerContext ctx){
        roomChannelGroup.remove(ctx);
        userList.remove(userId);
    }

}
