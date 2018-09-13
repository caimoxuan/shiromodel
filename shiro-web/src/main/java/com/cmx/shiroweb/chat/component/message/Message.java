package com.cmx.shiroweb.chat.component.message;

import lombok.Data;

@Data
public class Message {

    protected String msgId;

    protected String msgContext;

    protected Long timeStamp;

    protected Long sendUserId;

    protected Long receiveUserId;

    protected String roomId = "01";

    protected String messageType;

}
