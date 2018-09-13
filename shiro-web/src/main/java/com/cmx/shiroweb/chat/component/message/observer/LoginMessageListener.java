package com.cmx.shiroweb.chat.component.message.observer;

import com.cmx.shiroweb.chat.component.message.Message;
import com.cmx.shiroweb.chat.component.message.event.MessageEvent;
import com.cmx.shiroweb.chat.enums.MessageType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LoginMessageListener implements MessageListener {


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
        }
    }

}
