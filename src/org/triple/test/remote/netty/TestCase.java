package org.triple.test.remote.netty;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;
import org.junit.Test;

public class TestCase {

	public void testServer() {
		//初始化channel的辅助类，为具体子类提供公共数据结构  
		ServerBootstrap bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(
				// boss线程 负责处理请求等待 分发连接任务
				Executors.newCachedThreadPool(),
				// worker线程 负责处理连接
				Executors.newCachedThreadPool())
		);
		
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			public ChannelPipeline getPipeline() {
				// 生成一个管道
				ChannelPipeline pipeline = Channels.pipeline();
				// 添加decoder encoder 
				pipeline.addLast("decoder", new StringDecoder());
				pipeline.addLast("encoder", new StringEncoder());
				// 添加 实际的channel处理者
				pipeline.addLast("handler", new HelloWorldServerHandler());
				return pipeline;
			}
		});
		//创建服务器端channel的辅助类,接收connection请求  
		bootstrap.bind(new InetSocketAddress(8080));
	}

	public void testClient() {
		//创建客户端channel的辅助类,发起connection请求   
		ClientBootstrap bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(
				Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));
		//It means one same HelloWorldClientHandler instance is going to handle multiple Channels and consequently the data will be corrupted.  
		//基于上面这个描述，必须用到ChannelPipelineFactory每次创建一个pipeline  
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			public ChannelPipeline getPipeline() {
				ChannelPipeline pipeline = Channels.pipeline();
				pipeline.addLast("decoder", new StringDecoder());
				pipeline.addLast("encoder", new StringEncoder());
				pipeline.addLast("handler", new HelloWorldClientHandler());
				return pipeline;
			}
		});
		//创建无连接传输channel的辅助类(UDP),包括client和server  
		ChannelFuture future = bootstrap.connect(new InetSocketAddress("localhost", 8080));
		future.getChannel().getCloseFuture().awaitUninterruptibly();
		bootstrap.releaseExternalResources();
	}

	@Test
	public void testNetty() {
		testServer();
		testClient();
	}

}