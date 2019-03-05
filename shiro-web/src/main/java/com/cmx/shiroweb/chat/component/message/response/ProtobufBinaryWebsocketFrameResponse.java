package com.cmx.shiroweb.chat.component.message.response;

import com.cmx.shiroweb.chat.proto.ChatMessageOuterClass;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

/**
 * @author cmx
 * @date 2019/3/4
 */
public class ProtobufBinaryWebsocketFrameResponse {


    public static BinaryWebSocketFrame responseMessage (ChatMessageOuterClass.ChatMessage chatMessage) {
        byte[] bytes = chatMessage.toByteArray();
        ByteBuf buf = Unpooled.buffer();
        buf.writeBytes(bytes);
        return new BinaryWebSocketFrame(buf);
    }

}
