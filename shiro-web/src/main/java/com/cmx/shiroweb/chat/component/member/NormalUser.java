package com.cmx.shiroweb.chat.component.member;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import lombok.Data;

@Data
public class NormalUser extends User {

    private ChannelId channelId;

    public void bindChannel(ChannelHandlerContext ctx){
        this.channelId = ctx.channel().id();
    }

}
