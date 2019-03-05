package com.cmx.shiroweb.chat.component.message.router;

import com.cmx.shiroweb.chat.component.facilities.RoomManager;
import com.cmx.shiroweb.chat.component.message.response.ProtobufBinaryWebsocketFrameResponse;
import com.cmx.shiroweb.chat.proto.ChatMessageOuterClass;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author cmx
 * @date 2019/3/4
 */
@Slf4j
@Component
public class MessageRouter {

    @Autowired
    private RoomManager roomManager;

    public void route(ChatMessageOuterClass.ChatMessage message) {
        ChatMessageOuterClass.ChatMessage.RouterDispatch routerDispatch = message.getRouterDispatch();
        //用这个来判断是私聊还是在房间中等 消息路由类型
        int dispatchType = routerDispatch.getDispatchType();
        //消息的发送者id
        String fromUser = routerDispatch.getFromUser();
        //消息的接收者id 房间中为房间、组的id 否则为用户的id
        String toUser = routerDispatch.getToUser();
        BinaryWebSocketFrame binaryWebSocketFrame = ProtobufBinaryWebsocketFrameResponse.responseMessage(message);
        roomManager.getHall().getRoomChannelGroup().writeAndFlush(binaryWebSocketFrame);
        log.info("route by message : {}", message.getMessageId());
    }


}
