package com.cmx.shiroweb.chat.component.message.handler;

import com.cmx.shiroweb.chat.enums.MessageType;
import com.cmx.shiroweb.chat.proto.ChatMessageOuterClass;
import lombok.Setter;

/**
 * @author cmx
 * @date 2019/3/1
 */
public class UserMessageHandler implements ProtoBufMessageHandler {

    @Setter
    private ProtoBufMessageHandler nextHandler;

    @Override
    public void handleMessage(ChatMessageOuterClass.ChatMessage chatMessage) {
        if(MessageType.USER.getCode() == chatMessage.getMessageType()){
            System.out.println("handler user message");
        }else{
            nextHandler.handleMessage(chatMessage);
        }
    }
}