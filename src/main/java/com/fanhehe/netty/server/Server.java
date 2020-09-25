package com.fanhehe.netty.server;

import io.netty.channel.*;
import com.fanhehe.netty.server.handler.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class Server {

    public static void main(String[] args) {
        startServer();
    }

    public static void startServer() {
        //1.定义server启动类
        ServerBootstrap serverBootstrap = new ServerBootstrap();

        //2.定义工作组:boss分发请求给各个worker:boss负责监听端口请求，worker负责处理请求（读写）
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();

        //3.定义工作组
        serverBootstrap.group(boss, worker);

        //4.设置通道channel
        serverBootstrap.channel(NioServerSocketChannel.class);

        serverBootstrap.handler(new LoggingHandler());

        //5.添加handler，管道中的处理器，通过ChannelInitializer来构造
        serverBootstrap.childHandler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel channel) {
                ChannelPipeline pipeline = channel.pipeline();

                // Http协议
                pipeline.addLast(new HttpServerCodec());
                pipeline.addLast(new HttpObjectAggregator(512 * 1024));
                pipeline.addLast("serverHandler", new HttpHandler());

                // String 协议
//                pipeline.addLast(new StringDecoder());
//                pipeline.addLast(new StringHandler());
//                pipeline.addLast(new StringEncoder());
                // Protobuf
            }
        });

        // 6.设置参数
        // 设置参数，TCP参数
        serverBootstrap.option(ChannelOption.SO_BACKLOG, 2048); // 连接缓冲池的大小
        serverBootstrap.childOption(ChannelOption.TCP_NODELAY, true); // 关闭延迟发送
        serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true); // 维持链接的活跃，清除死链接

        //7.绑定ip和port
        try {
            ChannelFuture channelFuture
                    = serverBootstrap
                    .bind(9099)
                    .sync(); // Future模式的channel对象

            // 7.5.监听关闭
            channelFuture
                    .channel()
                    .closeFuture()
                    .sync(); // 等待服务关闭, 关闭后应该释放资源

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //8.优雅的关闭资源
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}