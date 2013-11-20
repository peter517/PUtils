package com.pengjun.net.remotectrl;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

public class RemoteCtrlHandler extends SimpleChannelUpstreamHandler {

	public RemoteCtrlHandler() {
		super();
	}

	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
		if (e.getMessage() != null) {
			try {
				parseAsCommand(e.getMessage().toString());
			} catch (Exception e1) {
				e1.printStackTrace();

			}
		}

	}

	private void parseAsCommand(String msg) throws Exception {
		for (RemoteCtrlCmd remoteCtrlCmd : RemoteCtrlServer.remoteCtrlCmdSet) {
			remoteCtrlCmd.parseAsCommand(msg);
		}
	}

	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
		e.getCause().printStackTrace();
		Channel ch = e.getChannel();
		ch.close();
	}
}