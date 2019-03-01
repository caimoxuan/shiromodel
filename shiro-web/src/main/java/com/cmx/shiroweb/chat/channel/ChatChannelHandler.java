package com.cmx.shiroweb.chat.channel;

import com.cmx.shiroweb.chat.proto.ChatMessageOuterClass;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrameAggregator;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * @author cmx
 */
@Component
public class ChatChannelHandler extends ChannelInitializer<SocketChannel> {

    /**
     *  http 能处理的最大数据量
     */
    private static final int MAX_HTTP_AGGREGATOR = 65536;

    /**
     * webSocket 能处理的最大数据量
     */
    private static final int MAX_WEBSOCKET_AGGREGATOR = 104857600;

    @Autowired
    private SimpleChannelInboundHandler chatWebSocketServerHandler;

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        // 设置30秒没有读到数据，则触发一个READER_IDLE事件。
        // pipeline.addLast(new IdleStateHandler(30, 0, 0));
        // HttpServerCodec：将请求和应答消息解码为HTTP消息
        socketChannel.pipeline().addLast("http-codec",new HttpServerCodec());
        // HttpObjectAggregator：将HTTP消息的多个部分合成一条完整的HTTP消息 设置消息的最大字节长度
        socketChannel.pipeline().addLast("aggregator",new HttpObjectAggregator(MAX_HTTP_AGGREGATOR));
        // ChunkedWriteHandler：向客户端发送HTML5文件
        socketChannel.pipeline().addLast("http-chunked",new ChunkedWriteHandler());
        //将websocket的消息聚合 最大支持100M消息 测试200M文件 chrome卡死
        socketChannel.pipeline().addLast("websocket-aggregator", new WebSocketFrameAggregator(MAX_WEBSOCKET_AGGREGATOR));
        //BinaryWebSocketFrame， 就不用手动解码了， 因为protobuf的消息一定是binary消息
        socketChannel.pipeline().addLast(new MessageToMessageDecoder<BinaryWebSocketFrame>() {
            @Override
            protected void decode(ChannelHandlerContext ctx, BinaryWebSocketFrame frame, List<Object> objs) throws Exception {
                ByteBuf buf = frame.content();
                objs.add(buf);
                buf.retain();
            }
        });
        //将websocketFream中的消息反序列成protobuf类型
        socketChannel.pipeline().addLast(new ProtobufDecoder(ChatMessageOuterClass.ChatMessage.getDefaultInstance()));
        socketChannel.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
        // 在管道中添加我们自己的接收数据实现方法
        socketChannel.pipeline().addLast("handler",chatWebSocketServerHandler);
    }

}
