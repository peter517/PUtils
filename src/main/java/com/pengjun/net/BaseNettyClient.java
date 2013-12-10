package com.pengjun.net;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

public class BaseNettyClient {

	private String serverIp;
	private int port;

	protected BaseNettyClient(String serverIp, int port) {
		this.serverIp = serverIp;
		this.port = port;
	}

	protected Channel channel = null;
	protected ClientBootstrap bootstrap = null;

	public Channel connect(ChannelPipelineFactory pipelineFactory) {

		bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(
				Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool()));
		bootstrap.setPipelineFactory(pipelineFactory);

		ChannelFuture connectFuture = bootstrap.connect(new InetSocketAddress(
				serverIp, port));
		channel = connectFuture.awaitUninterruptibly().getChannel();
		return channel;
	}

	public void disConnect() {
		if (channel != null) {
			channel.close().awaitUninterruptibly();
		}
		if (bootstrap != null) {
			bootstrap.releaseExternalResources();
		}
	}

}
