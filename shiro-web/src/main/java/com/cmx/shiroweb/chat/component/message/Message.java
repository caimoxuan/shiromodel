package com.cmx.shiroweb.chat.component.message;

import com.cmx.shiroweb.chat.constant.DefaultConstant;
import lombok.Data;

@Data
public class Message {

    protected String msgId;

    protected String msgContext;

    protected Long timeStamp;

    protected Long sendUser;

    protected Long receiveUser;

    protected String roomId = DefaultConstant.DEFAULT_HALL_ID;

    protected String messageType;

}
