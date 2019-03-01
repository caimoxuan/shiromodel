package com.cmx.shiroweb.chat.component.message.handler;

import com.cmx.shiroweb.chat.enums.MessageType;
import com.cmx.shiroweb.chat.proto.ChatMessageOuterClass;
import lombok.Setter;

/**
 * @author cmx
 * @date 2019/3/1
 */
public class SystemMessageHandler implements ProtoBufMessageHandler {

    @Setter
    private ProtoBufMessageHandler nextHandler;

    @Override
    public void handleMessage(ChatMessageOuterClass.ChatMessage chatMessage) {
        if(MessageType.SYSTEM.getCode() == chatMessage.getMessageType()) {
            System.out.println("handler system message");
        } else {
            nextHandler.handleMessage(chatMessage);
        }
    }
}
