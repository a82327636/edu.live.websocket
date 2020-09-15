package com.qinchen.chat.core.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.stereotype.Component;

@Component
public class WebSocketServer{

	private static class SingletonWSServer {
		static final WebSocketServer instance = new WebSocketServer();
	}

	public static WebSocketServer getInstance(){
		return SingletonWSServer.instance;
	}

	private NioEventLoopGroup bossGroup;
	private NioEventLoopGroup workGroup;
	private ServerBootstrap bootstrap;

	public WebSocketServer(){
		bossGroup = new NioEventLoopGroup(2);
		workGroup = new NioEventLoopGroup();
		bootstrap = new ServerBootstrap();
		bootstrap.group(bossGroup,workGroup)
				.channel(NioServerSocketChannel.class)
				.option(ChannelOption.SO_BACKLOG,128)
				.childOption(ChannelOption.SO_KEEPALIVE,true)
				.childHandler(new NioWebSocketChannelInitializer());
	}


	public void start() {
		try {
			bootstrap.bind(8888).sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}






}
