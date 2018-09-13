package com.cmx.shiroweb.chat.component;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cmx.shiroweb.chat.channel.ChannelManager;
import com.cmx.shiroweb.chat.component.facilities.ChatRoom;
import com.cmx.shiroweb.chat.component.message.Message;
import com.cmx.shiroweb.chat.component.message.event.MessageEvent;
import com.cmx.shiroweb.chat.component.message.observer.MessageListener;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.EventListener;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
public class MessageDispatcher implements ApplicationContextAware{

    @Autowired
    private MongoTemplate mongoTemplate;

    private List<MessageListener> messageListeners = new LinkedList<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        log.info("start init message listener!");
        Map<String, MessageListener> beanMap = applicationContext.getBeansOfType(MessageListener.class);
        for(MessageListener listener : beanMap.values()){
            messageListeners.add(listener);
        }
    }

    public void dispatchMessage(String jsonMessage, ChatRoom room){
        Message message = JSONObject.parseObject(jsonMessage, Message.class);
        saveMessage(message);
        for(MessageListener listener : messageListeners){
            listener.onMessage(new MessageEvent(message));
        }
    }

    private void saveMessage(Message message){
        try {
            mongoTemplate.insert(message, "chatServer");
        }catch(Exception e){
            log.error("save message to mongo get error: {}", e);
        }
    }

    public void doLogin(Long userId, ChatRoom room){
        log.info("user register userId");
        //1 用户加入
        ChannelHandlerContext channel = ChannelManager.getChannel();
        room.getUserList().put(userId, channel.channel().id());
        Message message = new Message();
        message.setMsgContext("you are register in !");
        TextWebSocketFrame tws = new TextWebSocketFrame(JSON.toJSONString(message));
        // 广播用户登陆信息
        room.getRoomChannelGroup().write(tws);
    }

}
