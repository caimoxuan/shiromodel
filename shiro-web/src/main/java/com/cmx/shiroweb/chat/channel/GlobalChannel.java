package com.cmx.shiroweb.chat.channel;


import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class GlobalChannel {

    private static ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private static Map<String, ChannelId> USER_CHANNEL = new ConcurrentHashMap();


    public static Channel getChannel(String userId) {
        if(userId == null){
            return null;
        }
        ChannelId channelId = USER_CHANNEL.get(userId);
        if(channelId == null){
            return null;
        }
        return group.find(channelId);
    }


    public static void setChannel(String userId, Channel channel){
        group.add(channel);
        USER_CHANNEL.put(userId, channel.id());
    }

    public static void removeChannel(String userId) {
        group.remove(group.find(USER_CHANNEL.get(userId)));
        USER_CHANNEL.remove(userId);
    }
}
