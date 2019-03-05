package com.cmx.shiroweb.chat.component.message.handler;

import com.cmx.shiroweb.chat.proto.ChatMessageOuterClass;
import org.springframework.stereotype.Component;

/**
 * @author cmx
 * @date 2019/3/1
 */
@Component
public class DefaultMessageHandler extends ProtoBufMessageHandler {

    @Override
    public void handleMessage(ChatMessageOuterClass.ChatMessage chatMessage) {
        System.out.println("message can not be handler ! message : {" + chatMessage + "}");
    }

}
