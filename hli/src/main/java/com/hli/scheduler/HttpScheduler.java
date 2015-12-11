package com.hli.scheduler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hli.domain.GoodsVO;
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
import io.netty.util.CharsetUtil;

public class HttpScheduler {
	private Logger log; 
	
	public HttpScheduler() {
		log = LoggerFactory.getLogger(HttpScheduler.class);
	}
	
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
	
	public void getProductOfM12() {
		OnReceiveListener listener = new OnReceiveListener() {
			
			@Override
			public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
				cause.printStackTrace();
		        ctx.close();
			}
			
			@Override
			public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
				/*if (msg instanceof HttpResponse) {
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
				}*/
				if (msg instanceof HttpContent) {
					HttpContent content = (HttpContent) msg;

					String strContent = content.content().toString();
					log.debug("content:" + strContent);
					
					InputStream in = null;
					try {
						in = new ByteArrayInputStream(strContent.getBytes("euc-kr"));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}

					try {
						SAXBuilder builder = new SAXBuilder();
						Document document = (Document) builder.build(in);
						Element rootNode = document.getRootElement();
						List list = rootNode.getChildren("ITEM");

						for (int i = 0; i < list.size(); i++) {
							Element node = (Element) list.get(i);
							GoodsVO goods = new GoodsVO();
							
							goods.setBrand_name(node.getChildText("BRAND_NAME"));
							goods.setGoods_name(node.getChildText("GOODS_NAME"));
							goods.setGoods_code(node.getChildText("GOODS_CODE"));
							goods.setThumbnail(node.getChildText("THUMBNAIL"));
							goods.setMarket_price(node.getChildText("MARKET_PRICE"));
							goods.setSell_price(node.getChildText("SELL_PRICE"));

							System.out.println("First Name : " + node.getChildText("BRAND_NAME")); //브랜드명
							System.out.println("First Name : " + node.getChildText("GOODS_NAME")); //상품명
							System.out.println("Last Name : " + node.getChildText("GOODS_CODE"));  //상품코드
							System.out.println("Nick Name : " + node.getChildText("THUMBNAIL"));   //thumbnail
							System.out.println("Salary : " + node.getChildText("MARKET_PRICE"));   //시장가격
							System.out.println("Salary : " + node.getChildText("SELL_PRICE"));     //판매가격

						}

					} catch (IOException io) {
						System.out.println(io.getMessage());
					} catch (JDOMException jdomex) {
						System.out.println(jdomex.getMessage());
					}
				}
			}
		};
		
		HttpClientHandler handler = new HttpClientHandler(listener);
		HttpClient client = new HttpClient(false, handler);
		
		//보낼 데이터 설정
		String url = "http://web6.m12.co.kr:12101/app/dev/goods_list_total.php?marketcode=HLINTNL01";
		URI uri;
		try {
			uri = new URI(url);
			
			FullHttpRequest request = new DefaultFullHttpRequest(
	                HttpVersion.HTTP_1_1, HttpMethod.GET, uri.getRawPath() /*+ "?" + uri.getRawQuery()*/ );
	        
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
