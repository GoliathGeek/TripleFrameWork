package org.triple.test.remote.netty;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

class HelloWorldServerHandler extends SimpleChannelHandler {
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		e.getChannel().write("Hello, World");
	}

	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
		System.out.println("Unexpected exception from downstream." + e.getCause());
		e.getChannel().close();
	}
}