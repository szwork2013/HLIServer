package com.hli.httpclient;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpObject;

public interface OnReceiveListener {
	public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception;
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception;
}
