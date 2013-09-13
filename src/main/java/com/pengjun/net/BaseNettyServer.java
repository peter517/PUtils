package com.pengjun.net;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

public class BaseNettyServer {

	private final int port;
	private ServerBootstrap bootstrap;

	protected BaseNettyServer(int port) {
		this.port = port;
	}

	protected void start(ChannelPipelineFactory channelPipelineFactory) {

		bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool()));
		bootstrap.setPipelineFactory(channelPipelineFactory);
		bootstrap.bind(new InetSocketAddress(port));

	}

	protected void disConnect() {
		if (bootstrap != null) {
			bootstrap.releaseExternalResources();
		}
	}

}
