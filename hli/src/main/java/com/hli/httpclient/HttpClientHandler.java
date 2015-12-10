/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.hli.httpclient;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.WriteTimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientHandler extends SimpleChannelInboundHandler<HttpObject> {
	private Logger log;
	
	public HttpClientHandler() {
    	log = LoggerFactory.getLogger(HttpClient.class);
	}
	
    @Override
    public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
    	if(msg instanceof DefaultFullHttpResponse) {
    		DefaultFullHttpResponse response = (DefaultFullHttpResponse) msg;
    		log.debug("[HttpClientHandler.channelRead0()] Response Message Received. \n"+response);
    		
    		// channel로 콜 찾아서 리스너로 Dispatch
    		SendRequestCallImpl call = adaptor.getRequestChannelTable().remove(ctx.channel());
    		if(call != null) {
    			HTTPResponseMessage responseMessage = new HTTPResponseMessage(response);
    			call.setResponseMessage(responseMessage);
    			
    			adaptor.dispatchCall(call);
    		} else {
    			
    			log.error("[RESTAdaptorImpl.receivedResponseMessage()] Call Not found....");
    		}
    	} else {
    		log.error("[HttpClientHandler.channelRead0()] Not Supported Message Received. \n"+msg);
    	}
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    	if(cause instanceof ReadTimeoutException) { 
			log.error("[HttpClientHandler.exceptionCaught()] ReadTimeout. channel={}", ctx.channel());
		} else if(cause instanceof WriteTimeoutException) {
			log.error("[HttpClientHandler.exceptionCaught()] WriteTimeout. channel={}", ctx.channel());
		} else {
			log.error("[HttpClientHandler.exceptionCaught()] Exception! cause={}", cause.getMessage(), cause);
		}
		
		ctx.close();
    }
}
