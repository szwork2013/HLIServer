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

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple HTTP client that prints out the content of the HTTP response to
 * {@link System#out} to test {@link HttpServer}.
 */
public class HttpClient {
	private Logger log;
	private Logger callLog;
	
	private EventLoopGroup group;
	private Bootstrap bootStrap; 
	
	/** XXX ssl테스트 해야함....... */
	private boolean ssl;
	private boolean keystoreUsingFile = false;
	private String keystoreFilePath = "";
	private String keystorePassword = "";
	
    public HttpClient(boolean ssl, HttpClientHandler handler, HttpAdaptorImpl adaptor) {
    	initialize(ssl, handler, adaptor);
    }
    
    public HttpClient(boolean ssl, HttpClientHandler handler, HttpAdaptorImpl adaptor, 
    		boolean keystoreUsingFile, String keystoreFilePath, String keystorePassword) {
    	this.keystoreUsingFile = keystoreUsingFile;
    	this.keystoreFilePath = keystoreFilePath;
    	this.keystorePassword = keystorePassword;
    	
    	initialize(ssl, handler, adaptor);
    }
    
    public void initialize(boolean ssl, HttpClientHandler handler, HttpAdaptorImpl adaptor) {
    	log = LoggerFactory.getLogger(adaptor.getAdaptorName());
    	callLog = LoggerFactory.getLogger(adaptor.getAdaptorName()+"_CALLLOG");
    	
    	this.usage = UsageWrapper.getInstance();
    	
    	this.ssl = ssl;
    	
    	group = new NioEventLoopGroup(adaptor.getClientLoopGroupSize());
    	
		bootStrap = new Bootstrap();
		
		bootStrap.group(group)
	     .channel(NioSocketChannel.class);
	    
		if(keystoreUsingFile) {
			bootStrap.handler(new HttpClientInitializer(ssl, handler, keystoreFilePath, keystorePassword));
		} else {
			bootStrap.handler(new HttpClientInitializer(ssl, handler));
		}
		
		bootStrap.option(ChannelOption.SO_KEEPALIVE, true);
    }
    
    
    public void shutdownClient() {
    	group.shutdownGracefully();
    }
    
    public Channel sendRequest(SendRequestCallImpl call, int connectTimeout) throws Exception {
    	URI uri = call.getHttpRequest().getURI();
    	DefaultFullHttpRequest request = call.getHttpRequest().getDefaultFullHttpRequest();
    	
        String scheme = uri.getScheme() == null? "http" : uri.getScheme();
        String host = uri.getHost() == null? "localhost" : uri.getHost();
        int port = uri.getPort();
        if (port == -1) {
            if ("http".equalsIgnoreCase(scheme)) {
                port = 80;
            } else if ("https".equalsIgnoreCase(scheme)) {
                port = 443;
            }
        }

        // scheme 이 http나 https가 아닐때
        if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
            System.err.println("Only HTTP(S) is supported.");
            
            throw new IllegalArgumentException("Bad URI Scheme. "+scheme);
        }
        
        // ssl가 false인데 https 요청 왔을때
        if(!ssl && scheme.equalsIgnoreCase("https")) {
			System.err.println("HTTPS not supported.");
			
			throw new Exception("HTTPS not supported.");
        }
        
        // set Connect Timeout
        bootStrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout);
        // XXX Socket Timeout 따로걸어야하나...?
        bootStrap.option(ChannelOption.SO_TIMEOUT, connectTimeout);
        
        // 헤더에 HOST 추가
        request.headers().set(HttpHeaders.Names.HOST, host);
        
        // Connect
        ChannelFuture cf = bootStrap.connect(host, port).sync();
        Channel ch = cf.channel();
        
        log.debug("[HTTPClient.sendRequest()] Send Request. URI={}\n{}", uri, request);
    	callLog.debug("[SEND HTTP REQUEST] callId={}\n{}", call.getCallID(), call.getHttpRequest());
    	
    	usage.requestIncrement(UsageWrapper.SEND, call.getServiceID().getName(), request.getMethod().name());
    	
    	// Send HTTP Request Message
        ch.writeAndFlush(request);
        
        call.setIsSendRequest(true);
        
        return ch;
    }
}
