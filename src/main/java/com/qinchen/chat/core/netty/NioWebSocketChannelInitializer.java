package com.qinchen.chat.core.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import java.util.concurrent.TimeUnit;


public class NioWebSocketChannelInitializer extends ChannelInitializer<SocketChannel> {

    /**
     * 初始化
     * @param ch
     */
    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        //pipeline.addLast(new LoggingHandler("INFO"));
        pipeline.addLast("http-codec",new HttpServerCodec());
        pipeline.addLast("chunk",new ChunkedWriteHandler());
        pipeline.addLast(new HttpObjectAggregator(8192));
        pipeline.addLast(new WebSocketServerProtocolHandler("/webSocket"));
        pipeline.addLast(new IdleStateHandler(60,120,180,TimeUnit.SECONDS));
        pipeline.addLast(new MyNioWebSocketHandler()); //自定义的业务handler
        ch.pipeline().addLast(new ExceptionCaughtHandler());

    }
}
