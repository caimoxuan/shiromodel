package com.cmx.shiroweb.chat.component.message.observer;

import com.cmx.shiroweb.chat.component.message.Message;
import com.cmx.shiroweb.chat.component.message.event.MessageEvent;
import com.cmx.shiroweb.chat.enums.MessageType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ChatMessageListener implements MessageListener {
    @Override
    public void onMessage(MessageEvent messageEvent) {
        Message message = (Message) messageEvent.getSource();
        if(MessageType.USER.name().equals(message.getMessageType())){
          log.info("user chat message : {}", message);
        }

    }
}
