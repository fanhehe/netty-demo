package com.fanhehe.netty.server.handler;

import java.util.HashMap;
import java.util.Map;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;
import io.netty.handler.codec.http.*;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class HttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) {
        // 获取请求的uri
        String uri = req.uri();
        String msg = "<html><head><title>test</title></head><body>你请求uri为：" + uri + "</body></html>";
        // 创建http响应
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8));

        // 设置头信息
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=UTF-8");
        // 将html write到客户端
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
