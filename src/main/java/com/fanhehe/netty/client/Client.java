package com.fanhehe.netty.client;


import io.netty.channel.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import com.fanhehe.netty.client.handler.ClientHandler;

public class Client {
    public static void main(String[] args) {
        //1.定义服务类
        Bootstrap clientBootstrap = new Bootstrap();

        //2.定义执行线程组
        EventLoopGroup worker = new NioEventLoopGroup();

        //3.设置线程池
        clientBootstrap.group(worker);

        //4.设置通道
        clientBootstrap.channel(NioSocketChannel.class);

        //5.添加Handler
        clientBootstrap.handler(new ChannelInitializer() {
            @Override
            protected void initChannel(Channel channel) {
                System.out.println("client channel init!");
                ChannelPipeline pipeline = channel.pipeline();
                pipeline.addLast("StringDecoder", new StringDecoder());
                pipeline.addLast("StringEncoder", new StringEncoder());
                pipeline.addLast("clientHandler", new ClientHandler());

            }
        });

        //6.建立连接
        ChannelFuture channelFuture = clientBootstrap.connect("0.0.0.0",9099);
        try {
            //7.测试输入
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            while(true){
                System.out.println("请输入：");
                String msg = bufferedReader.readLine();
                channelFuture.channel().writeAndFlush(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            //8.关闭连接
            worker.shutdownGracefully();
        }
    }
}

