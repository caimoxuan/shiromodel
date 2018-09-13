package com.cmx.shiroweb.chat.component.message.observer;

import com.cmx.shiroweb.chat.component.message.event.MessageEvent;

import java.util.EventListener;

public interface MessageListener extends EventListener {

    void onMessage(MessageEvent messageEvent);

}
