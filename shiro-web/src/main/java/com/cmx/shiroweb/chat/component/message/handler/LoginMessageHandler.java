package com.cmx.shiroweb.chat.component.message.handler;

import com.cmx.shiroweb.chat.enums.MessageType;
import com.cmx.shiroweb.chat.proto.ChatMessageOuterClass;
import org.springframework.stereotype.Component;

/**
 * @author cmx
 * @date 2019/3/1
 */
@Component
public class LoginMessageHandler extends ProtoBufMessageHandler {

    @Override
    public void handleMessage(ChatMessageOuterClass.ChatMessage chatMessage) {
        if(MessageType.LOGIN.getCode() == chatMessage.getMessageType()){
            System.out.println("handler login message");
        }else{
            nextHandler.handleMessage(chatMessage);
        }
    }
}
