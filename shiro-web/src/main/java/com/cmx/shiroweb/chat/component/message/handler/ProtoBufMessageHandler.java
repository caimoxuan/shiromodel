package com.cmx.shiroweb.chat.component.message.handler;


import com.cmx.shiroweb.chat.proto.ChatMessageOuterClass;

/**
 * @author cmx
 * @date 2019/3/1
 */
public interface ProtoBufMessageHandler {

    void handleMessage(ChatMessageOuterClass.ChatMessage chatMessage);

}
