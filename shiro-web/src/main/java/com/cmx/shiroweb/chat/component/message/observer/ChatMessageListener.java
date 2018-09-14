package com.cmx.shiroweb.chat.component.message.observer;

import com.alibaba.fastjson.JSON;
import com.cmx.shiroweb.chat.component.facilities.ChatRoom;
import com.cmx.shiroweb.chat.component.facilities.RoomManager;
import com.cmx.shiroweb.chat.component.message.Message;
import com.cmx.shiroweb.chat.component.message.event.MessageEvent;
import com.cmx.shiroweb.chat.enums.MessageType;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ChatMessageListener implements MessageListener {

    @Autowired
    private RoomManager roomManager;

    @Override
    public void onMessage(MessageEvent messageEvent) {
        Message message = (Message) messageEvent.getSource();
        if(MessageType.USER.name().equals(message.getMessageType())){
            if(message.getReceiveUser() != null){
                Long userId = message.getReceiveUser();
                //单播
                ChannelId channelId = roomManager.getHall().getUserList().get(userId);
                Channel channel = roomManager.getHall().roomChannelGroup.find(channelId);
                if(channel != null){
                    TextWebSocketFrame tws = new TextWebSocketFrame(JSON.toJSONString(message));
                    channel.writeAndFlush(tws);
                    return;
                }
                log.error("can not find channel maybe user logout: {}", message);

            }else{
                //广播
                ChatRoom room = roomManager.getRoom(message.getRoomId());
                if(room != null) {
                    TextWebSocketFrame tws = new TextWebSocketFrame(JSON.toJSONString(message));
                    room.getRoomChannelGroup().writeAndFlush(tws);
                    return;
                }
                log.error("can not find room, message : {}", message);
            }
        }

    }
}
