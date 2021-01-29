package demo.netty.day1;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class TestServer {

	public static void main(String[] args) throws InterruptedException {
		//事件循环组
		//请求接收转发
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		//请求处理
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			//服务端启动
			ServerBootstrap serverBootstarp = new ServerBootstrap();
			serverBootstarp.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.childHandler(new TestServerInitiallzer());
			
			ChannelFuture channelFuture = serverBootstarp.bind(8899).sync();
			channelFuture.channel().closeFuture().sync();
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
}
