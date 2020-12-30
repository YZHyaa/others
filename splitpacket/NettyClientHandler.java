package com.tuling.netty.base3;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;


public class NettyClientHandler extends SimpleChannelInboundHandler<String> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("成功连接到服务端..." + ctx.channel().remoteAddress());
//        ctx.writeAndFlush("Hello Server！");

        for (int i = 0; i < 100; i++) {
            String msg = "Just do it!";
            MyMessageProtocol protocol = new com.tuling.netty.base3.MyMessageProtocol();
            protocol.setContent(msg.getBytes(CharsetUtil.UTF_8));
            protocol.setLen(msg.getBytes(CharsetUtil.UTF_8).length);
            ctx.writeAndFlush(protocol);
//            ctx.writeAndFlush(msg);
        }

    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("Server："+ msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}