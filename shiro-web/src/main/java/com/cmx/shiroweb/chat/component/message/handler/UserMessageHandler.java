package com.cmx.shiroweb.chat.component.message.handler;

import com.cmx.shiroweb.chat.enums.MessageType;
import com.cmx.shiroweb.chat.proto.ChatMessageOuterClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author cmx
 * @date 2019/3/1
 */
@Slf4j
@Component
public class UserMessageHandler extends ProtoBufMessageHandler {

    @Override
    public void handleMessage(ChatMessageOuterClass.ChatMessage chatMessage) {
        if(MessageType.TEXT.getCode() == chatMessage.getMessageType() ||
           MessageType.EMOJI.getCode() == chatMessage.getMessageType() ||
           MessageType.IMG.getCode() == chatMessage.getMessageType()){
            log.info("handler user message, messageId : {}", chatMessage.getMessageId());
            messageRouter.route(chatMessage);
            saveMessage(chatMessage);
        }else{
            nextHandler.handleMessage(chatMessage);
        }
    }
}
