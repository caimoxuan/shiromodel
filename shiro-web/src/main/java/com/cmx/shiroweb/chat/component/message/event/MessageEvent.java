package com.cmx.shiroweb.chat.component.message.event;


import java.util.EventObject;

public class MessageEvent extends EventObject {
    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public MessageEvent(Object source) {
        super(source);
    }



}
