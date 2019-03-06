package com.cmx.shiroweb.chat.component.message.response;

import com.alibaba.fastjson.JSON;
import com.cmx.shiroweb.chat.enums.MessageType;
import com.cmx.shiroweb.chat.proto.ChatMessageOuterClass;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import lombok.Data;

import java.time.Instant;

/**
 * @author cmx
 * @date 2019/3/6
 */
@Data
public class SystemResponse extends ProtobufBinaryWebsocketFrameResponse{

    /**
     * 回复码
     */
    private Integer code;
    /**
     * 系统信息
     */
    private String message;

    private SystemResponse(Integer code, String message){
        this.code = code;
        this.message = message;
    }


    /**
     *  login
     */
    public static final SystemResponse loginSuccessResponse  = new SystemResponse(10000, "success");
    public static final SystemResponse loginMessageParseFailResponse = new SystemResponse(10001, "登陆信息异常，请刷新！");
    public static final SystemResponse loginStatusConflictResponse = new SystemResponse(100002, "账号在其他地址登陆，请确认账号安全！");
    public static final SystemResponse loginMessageCheckFailResponse = new SystemResponse(10001, "登陆验证失败，非法的连接！");



    public static BinaryWebSocketFrame buildSystemMessage(SystemResponse systemResponse){
        ChatMessageOuterClass.ChatMessage message = ChatMessageOuterClass.ChatMessage.newBuilder().
                setMessageType(MessageType.SYSTEM.getCode()).
                setMessageTimestamp(Instant.now().toEpochMilli()).
                setMessageContext(JSON.toJSONString(systemResponse)).build();
        return responseMessage(message);
    }

}
