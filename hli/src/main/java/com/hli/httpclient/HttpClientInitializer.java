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

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

public class HttpClientInitializer extends ChannelInitializer<SocketChannel> {

    private final boolean ssl;
    
    private HttpClientHandler handler;
    
    private SSLContext sslContext;

    public HttpClientInitializer(boolean ssl, HttpClientHandler handler) {
        this.ssl = ssl;
        this.handler = handler;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        // Create a default pipeline implementation.
        ChannelPipeline p = ch.pipeline();

        p.addLast("log", new LoggingHandler(LogLevel.INFO));
        
        // Enable HTTPS if necessary.
//        if (ssl) {
//        	SSLEngine engine = sslContext.createSSLEngine();
//        	engine.setUseClientMode(true);
//        	engine.setEnabledCipherSuites(SSLCipherSuits.getEnableCipherSuits());
//            
//            p.addLast("ssl", new SslHandler(engine));
//        }

        p.addLast("codec", new HttpClientCodec());

        // Remove the following line if you don't want automatic content decompression.
        p.addLast("inflater", new HttpContentDecompressor());

        // Uncomment the following line if you don't want to handle HttpChunks.
        p.addLast("aggregator", new HttpObjectAggregator(10240000));

        p.addLast("handler", handler);
    }
}
