package com.cmx.shiroweb.chat.component;


import com.alibaba.fastjson.JSONObject;
import com.cmx.shiroweb.chat.component.message.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class MessageDispatcher{

    @Autowired
    private MongoTemplate mongoTemplate;


    public void dispatchMessage(String jsonMessage){
        Message message = JSONObject.parseObject(jsonMessage, Message.class);
        saveMessage(message);

    }

    private void saveMessage(Message message){
        try {
            mongoTemplate.insert(message, "chatServer");
        }catch(Exception e){
            log.error("save message to mongo get error: {}", e);
        }
    }

}
