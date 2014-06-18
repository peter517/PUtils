package com.pengjun.net.remotectrl;

import java.util.HashSet;
import java.util.Set;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;

import com.pengjun.net.BaseNettyServer;

public class RemoteCtrlServer extends BaseNettyServer {
	private static RemoteCtrlServer remoteCtrlServer = null;
	protected final static Set<RemoteCtrlCmd> remoteCtrlCmdSet = new HashSet<RemoteCtrlCmd>();

	public void addRemoteCtrlCmd(RemoteCtrlCmd remoteCtrlCmd) {
		remoteCtrlCmdSet.add(remoteCtrlCmd);
	}

	public synchronized static RemoteCtrlServer getInstance() {
		if (remoteCtrlServer == null)
			remoteCtrlServer = new RemoteCtrlServer();

		return remoteCtrlServer;
	}

	public RemoteCtrlServer() {
		super(3271);
	}

	public void start() {

		super.start(new ChannelPipelineFactory() {

			@Override
			public ChannelPipeline getPipeline() throws Exception {
				ChannelPipeline pipeline = Channels.pipeline();
				pipeline.addLast("encode", new StringEncoder());
				pipeline.addLast("decode", new StringDecoder());
				pipeline.addLast("handler", new RemoteCtrlHandler());

				return pipeline;
			}
		});

		bootstrap.setOption("child.tcpNoDelay", true);
		bootstrap.setOption("child.keepAlive", true);
	}

	public static void main(String[] args) {
		RemoteCtrlServer.getInstance().start();
		RemoteCtrlCmd remoteCtrlCmd = new RemoteCtrlCmd() {

			@Override
			public void parseAsCommand(String cmd) throws Exception {
				System.out.print("cmd = " + cmd);
			}
		};
		RemoteCtrlServer.getInstance().addRemoteCtrlCmd(remoteCtrlCmd);
	}
}
