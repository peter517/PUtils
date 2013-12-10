package com.pengjun.net;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelException;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

public class BaseNettyServer {

	private final int port;
	public ServerBootstrap bootstrap;
	public Channel channel;

	protected BaseNettyServer(int port) {
		this.port = port;
	}

	protected void start(ChannelPipelineFactory channelPipelineFactory) {

		bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(
				Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool()));

		bootstrap.setPipelineFactory(channelPipelineFactory);
		try {
			channel = bootstrap.bind(new InetSocketAddress(port));
		} catch (ChannelException ce) {
			try {
				if (channel != null)
					channel.close().awaitUninterruptibly();
			} catch (Exception e) {

			}
		}

	}

	protected void stop() {
		try {
			if (channel != null) {
				channel.close().awaitUninterruptibly();
			}
			if (bootstrap != null) {
				bootstrap.releaseExternalResources();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			channel = null;
			bootstrap = null;
		}
	}

}
