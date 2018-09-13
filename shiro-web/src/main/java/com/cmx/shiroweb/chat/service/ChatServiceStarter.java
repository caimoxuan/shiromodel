package com.cmx.shiroweb.chat.service;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


@Slf4j
@Component
public class ChatServiceStarter {


    @Autowired
    private ChannelInitializer chatChannelHandler;

    @PostConstruct
    public void startServer(){
        Thread t = new Thread(new ChatRunner());
        t.start();
    }

    class ChatRunner implements Runnable {

        @Override
        public void run() {
            log.info("netty server start!");
            // 在服务器端每个监听的socket都有一个boss线程来处理。在客户端，只有一个boss线程来处理所有的socket。
            EventLoopGroup bossGroup = new NioEventLoopGroup();
            // Worker线程：Worker线程执行所有的异步I/O，即处理操作
            EventLoopGroup workGroup = new NioEventLoopGroup();
            try {
                // ServerBootstrap 启动NIO服务的辅助启动类,负责初始话netty服务器，并且开始监听端口的socket请求
                ServerBootstrap b = new ServerBootstrap();
                b.group(bossGroup, workGroup);
                // 设置非阻塞,用它来建立新accept的连接,用于构造serversocketchannel的工厂类
                b.channel(NioServerSocketChannel.class);
                // ChildChannelHandler 对出入的数据进行的业务操作,其继承ChannelInitializer
                b.childHandler(chatChannelHandler);
                System.out.println("服务端开启等待客户端连接 ... ...");
                Channel ch = b.bind(7397).sync().channel();
                ch.closeFuture().sync();
            } catch (Exception e) {
                e.printStackTrace();
            }finally{
                bossGroup.shutdownGracefully();
                workGroup.shutdownGracefully();
            }
        }
    }

}
