package com.cmx.shiroweb.chat.component.message.handler;

import com.cmx.shiroweb.chat.proto.ChatMessageOuterClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author cmx
 * @date 2019/3/1
 */
@Slf4j
@Component
public class DefaultMessageHandler extends ProtoBufMessageHandler {

    @Override
    public void handleMessage(ChatMessageOuterClass.ChatMessage chatMessage) {
        log.info("message can not be handler ! message : {}", chatMessage);
    }

}
