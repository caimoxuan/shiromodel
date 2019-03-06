package com.cmx.shiroweb.chat.component.message.handler;

import com.alibaba.fastjson.JSON;
import com.cmx.shiroweb.chat.channel.ChannelManager;
import com.cmx.shiroweb.chat.channel.GlobalChannel;
import com.cmx.shiroweb.chat.component.member.LoginToken;
import com.cmx.shiroweb.chat.component.member.NormalUser;
import com.cmx.shiroweb.chat.component.message.response.SystemResponse;
import com.cmx.shiroweb.chat.enums.MessageType;
import com.cmx.shiroweb.chat.proto.ChatMessageOuterClass;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cmx
 * @date 2019/3/1
 */
@Slf4j
@Component
public class LoginMessageHandler extends ProtoBufMessageHandler {


    @Override
    public void handleMessage(ChatMessageOuterClass.ChatMessage chatMessage) {
        if(MessageType.LOGIN.getCode() == chatMessage.getMessageType()){
            log.info("handler login message : {}", chatMessage.getMessageContext());
            //获取消息体
            String loginMessage = chatMessage.getMessageContext();
            verifyUser(loginMessage);
            //查询当前用户的群组 推送上线状态
        }else{
            nextHandler.handleMessage(chatMessage);
        }
    }


    private void verifyUser(String loginMessage) {
        LoginToken loginToken;
        try {
            loginToken = JSON.parseObject(loginMessage, LoginToken.class);
        }catch(Exception e){
            log.info("can not parse login message, close channel, message : {}", loginMessage);
            ChannelManager.getChannel().writeAndFlush(SystemResponse.buildSystemMessage(SystemResponse.loginMessageParseFailResponse));
            return;
        }

        String token = loginToken.getToken();
        String userId = loginToken.getUserId();
        Map<String, Object> fieldMap = new HashMap(1);
        fieldMap.put("userId", userId);
        NormalUser user = (NormalUser)mongoRepository.findOne(fieldMap, "chat_user", NormalUser.class);
        if(user.getToken() != null || user.getToken().equals(token)){
            if(user.isActive()){
                //用户登陆中 顶(先获取用户原先的channel 推送顶的信息， 再将新的channel放入)
                Channel oldChannel = GlobalChannel.getChannel(userId);
                if(oldChannel != null) {
                    oldChannel.writeAndFlush(SystemResponse.buildSystemMessage(SystemResponse.loginStatusConflictResponse));
                    oldChannel.close();
                }
            }
            Map<String, Object> updateParam = new HashMap(1);
            updateParam.put("isActive", true);
            mongoRepository.update(fieldMap, updateParam, "chat_user");
            Channel channel = ChannelManager.getChannel().channel();
            GlobalChannel.setChannel(userId, channel);
            channel.writeAndFlush(SystemResponse.buildSystemMessage(SystemResponse.loginSuccessResponse));
        }else{
            log.info("login user not match token, close channel, message : {}", loginMessage);
            ChannelManager.getChannel().writeAndFlush(SystemResponse.buildSystemMessage(SystemResponse.loginMessageCheckFailResponse));
        }
    }
}
