package com.cmx.shiroweb.chat.component.message.observer;

import com.cmx.shiroweb.chat.channel.ChannelManager;
import com.cmx.shiroweb.chat.component.facilities.ChatRoom;
import com.cmx.shiroweb.chat.component.facilities.RoomManager;
import com.cmx.shiroweb.chat.component.message.Message;
import com.cmx.shiroweb.chat.component.message.event.MessageEvent;
import com.cmx.shiroweb.chat.enums.MessageType;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LoginMessageListener implements MessageListener {


    @Autowired
    private RoomManager roomManager;

    /**
     * 登入消息的处理
     * 1. 消息为登入消息
     * 2. 找到消息的所在位置
     * 3. 处理相应位置的消息推送
     * @param messageEvent
     */
    @Override
    public void onMessage(MessageEvent messageEvent) {
        Message message = (Message)messageEvent.getSource();
        if(MessageType.LOGIN.name().equals(message.getMessageType())){
            log.info("user login message : {}", message);
            ChatRoom room = roomManager.getRoom(message.getRoomId());
            if(room == null){
                log.error("can not handle message : {}, room not exit.", message);
                return;
            }
            ChannelHandlerContext channel = ChannelManager.getChannel();
            if(channel == null || message.getSendUser() == null){
                log.error("can not find user info, channel : {}, userId: {}", channel, message.getSendUser());
                return;
            }
            //房间注册用户
            room.getUserList().put(message.getSendUser(), channel.channel().id());
        }
    }

}
