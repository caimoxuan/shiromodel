package com.cmx.shiroweb.chat.websocket;

import com.cmx.shiroweb.chat.channel.ChannelManager;
import com.cmx.shiroweb.chat.channel.GlobalChannel;
import com.cmx.shiroweb.chat.component.MessageDispatcher;
import com.cmx.shiroweb.chat.component.facilities.ChatRoom;
import com.cmx.shiroweb.chat.component.facilities.NormalRoom;
import com.cmx.shiroweb.chat.component.facilities.RoomManager;
import com.cmx.shiroweb.chat.constant.DefaultConstant;
import com.cmx.shiroweb.chat.proto.ChatMessageOuterClass;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


@Slf4j
@Sharable
@Component
public class ChatWebSocketServerHandler extends SimpleChannelInboundHandler<Object> {

    @Autowired
    private MessageDispatcher messageDispatcher;

    @Autowired
    private RoomManager roomManager;

    private WebSocketServerHandshaker handshaker;

    @PostConstruct
    public void initChatHall(){
        ChatRoom chatHall = new NormalRoom(DefaultConstant.DEFAULT_HALL_ID, DefaultConstant.DFFAULT_HALL_NAME);
        roomManager.registerRoom(chatHall);
    }

    /**
     * channel 通道 action 活跃的 当客户端主动链接服务端的链接后，这个通道就是活跃的了。也就是客户端与服务端建立了通信通道并且可以传输数据
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 添加
        roomManager.getHall().getRoomChannelGroup().add(ctx.channel());
        log.info("客户端与服务端连接开启：{}", ctx.channel().remoteAddress().toString());
    }

    /**
     * channel 通道 Inactive 不活跃的 当客户端主动断开服务端的链接后，这个通道就是不活跃的。也就是说客户端与服务端关闭了通信通道并且不可以传输数据
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 移除
        roomManager.getHall().getRoomChannelGroup().remove(ctx.channel());
        //从各个房间删除用户
        System.out.println("客户端与服务端连接关闭：" + ctx.channel().remoteAddress().toString());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    /**
     * 接收客户端的消息
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
        //第一次建立http连接
        if (msg instanceof FullHttpRequest) {
            handleHttpRequest(ctx, ((FullHttpRequest) msg));
            // 否则是websocket的消息， 这时已经被protobuf解析
        } else {
            log.info("handler webSocket message!");
            handlerWebSocketFrame(ctx, msg);
        }
    }

    private void handlerWebSocketFrame(ChannelHandlerContext ctx, Object message) throws Exception {
        // 判断是否关闭链路的指令
        if (message instanceof CloseWebSocketFrame) {
            log.info("get close webSocket message;");
            handshaker.close(ctx.channel(), ((CloseWebSocketFrame) message).retain());
            return;
        }
        // 判断是否ping消息
        if (message instanceof PingWebSocketFrame) {
            log.info("get ping webSocket message");
            ctx.channel().write(new PongWebSocketFrame(((PingWebSocketFrame)message).content().retain()));
            return;
        }
//
//        //大数据流消息，如果图片比较大，需要合并， 如果传的是文件， 需要判断messageType来保存文件
//        if(frame instanceof ContinuationWebSocketFrame){
//            System.out.println("get continuation webSocket message");
//            ContinuationWebSocketFrame continuationWebSocketFrame = ((ContinuationWebSocketFrame)frame);
//            ByteBuf byteBuf = continuationWebSocketFrame.retain().content();
//            if(continuationWebSocketFrame.isFinalFragment()) {
//                bytes.writeBytes(byteBuf);
//                System.out.println("finish : " + bytes.readableBytes());
//                final int length = bytes.readableBytes();
//                final byte[] array = new byte[length];
//                bytes.getBytes(bytes.readerIndex(), array, 0, length);
//                ChatMessageOuterClass.ChatMessage chatMessage = ChatMessageOuterClass.ChatMessage.parseFrom(array);
//                System.out.println(chatMessage.getMessageId() + " , " + chatMessage.getMessageContext());
//                File f = new File(UPLOAD_FILE_PATH + chatMessage.getFileMessage().getFileName());
//                FileOutputStream fos = new FileOutputStream(f);
//                fos.write(chatMessage.getFileMessage().getFileContent().toByteArray());
//                fos.close();
//                //ctx.channel().writeAndFlush(new BinaryWebSocketFrame(bytes.retain()));
//                bytes.clear();
//            }else{
//
//                bytes.writeBytes(byteBuf);
//                System.out.println(bytes.readableBytes());
//            }
//
//
//            //ctx.channel().writeAndFlush(frame.retain());
//            return;
//        }
//
//
        //二进制消息
        if (message instanceof ChatMessageOuterClass.ChatMessage) {
            log.info("get binary webSocket message");
            ChatMessageOuterClass.ChatMessage chatMessage = ((ChatMessageOuterClass.ChatMessage)message);
            //开始分发消息前存储当前channel
            ChannelManager.setChannel(ctx);
            try {
                messageDispatcher.dispatchMessage(chatMessage);
            }catch(Exception e){
                log.error("dispatch message get error : {}", e);
            }finally {
                //完成一次通话清除当前保存的用户
                ChannelManager.remove();
            }
        }
    }



    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
        // 如果HTTP解码失败，返回HTTP异常
        if(!req.decoderResult().isSuccess()){

            sendHttpResponse(ctx,req, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }

        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory("ws:/"+ctx.channel()+ "/websocket",null,false, 65535 * 1024);
        handshaker = wsFactory.newHandshaker(req);

        if(handshaker == null){
            //不支持
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        }else{
            handshaker.handshake(ctx.channel(), req);
        }
    }



    private static void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, DefaultFullHttpResponse res) {
        // 返回应答给客户端
        if (res.status().code() != 200)
        {
            ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
        }

        // 如果是非Keep-Alive，关闭连接
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        if (!isKeepAlive(req) || res.status().code() != 200)
        {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }


    private static boolean isKeepAlive(FullHttpRequest req)
    {
        return false;
    }

    /**
     *  异常处理，netty默认是关闭channel
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        //输出日志
        cause.printStackTrace();
        //关闭当前用户
        ctx.close();
    }

}
