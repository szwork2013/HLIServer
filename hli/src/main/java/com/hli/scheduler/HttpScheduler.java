package com.hli.scheduler;

import java.net.URI;
import java.net.URISyntaxException;

import com.hli.httpclient.HttpClient;
import com.hli.httpclient.HttpClientHandler;
import com.hli.httpclient.OnReceiveListener;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringEncoder;
import io.netty.util.CharsetUtil;

public class HttpScheduler {
	
	public void getProductOfCoup() {
		OnReceiveListener listener = new OnReceiveListener() {
			
			@Override
			public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
				cause.printStackTrace();
		        ctx.close();
			}
			
			@Override
			public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
				if (msg instanceof HttpResponse) {
					HttpResponse response = (HttpResponse) msg;

					System.err.println("STATUS: " + response.getStatus());
					System.err.println("VERSION: " + response.getProtocolVersion());
					System.err.println();

					if (!response.headers().isEmpty()) {
						for (CharSequence name : response.headers().names()) {
							for (CharSequence value : response.headers().getAll(name)) {
								System.err.println("HEADER: " + name + " = " + value);
							}
						}
						System.err.println();
					}
				}
				if (msg instanceof HttpContent) {
					HttpContent content = (HttpContent) msg;

					System.err.print(content.content().toString(CharsetUtil.UTF_8));
					System.err.flush();

				}
			}
		};
		
		HttpClientHandler handler = new HttpClientHandler(listener);
		HttpClient client = new HttpClient(false, handler);
		
		//보낼 데이터 설정
		String url = "http://issuev3apitest.m2i.kr:9999/serviceapi.asmx/ServiceCouponList_02?CODE=0424&PASS=hlint123&DOCCODE=0424000";
		URI uri;
		try {
			uri = new URI(url);
			
			FullHttpRequest request = new DefaultFullHttpRequest(
	                HttpVersion.HTTP_1_1, HttpMethod.GET, uri.getRawPath() + "?" + uri.getRawQuery() );
	        
			request.headers().set("host", uri.getHost());
	        request.headers().set("connection", "close");
	        request.headers().set("accept-encoding", "gzip");
			
			client.sendRequest(request, url);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
