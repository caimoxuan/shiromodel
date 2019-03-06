package com.cmx.shiroweb.chat.component.message.handler;


import com.cmx.shiroweb.chat.component.message.router.MessageRouter;
import com.cmx.shiroweb.chat.proto.ChatMessageOuterClass;
import com.cmx.shiroweb.chat.repository.MongoRepository;
import com.googlecode.protobuf.format.JsonFormat;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

/**
 * @author cmx
 * @date 2019/3/1
 */
@Slf4j
@Component
public abstract class ProtoBufMessageHandler {


    @Setter
    ProtoBufMessageHandler nextHandler;

    @Autowired
    MessageRouter messageRouter;

    @Autowired
    MongoRepository mongoRepository;

    /**
     * webSocket消息处理
     * @param chatMessage 消息内容
     */
    abstract void handleMessage(ChatMessageOuterClass.ChatMessage chatMessage);


    void saveMessage(ChatMessageOuterClass.ChatMessage chatMessage) {
        String jsonMessage = JsonFormat.printToString(chatMessage);
        try {
            mongoRepository.insert(jsonMessage, "chat_log");
        }catch(Exception e){
            log.info("save message to mongo get error : {}", e);
        }
    }

}
