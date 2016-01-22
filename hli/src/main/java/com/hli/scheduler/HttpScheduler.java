package com.hli.scheduler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hli.domain.GoodsVO;
import com.hli.httpclient.HttpClient;
import com.hli.httpclient.HttpClientHandler;
import com.hli.httpclient.OnReceiveListener;
import com.hli.service.AdminService;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

@Component
public class HttpScheduler {
	
	@Autowired
	private AdminService adminService;
	
	private Logger log; 
	
	private HashMap<String, GoodsVO> goodsList = new HashMap<String, GoodsVO>();
	
	public HttpScheduler() {
		log = LoggerFactory.getLogger(HttpScheduler.class);
	}
	
	//M12 실상품가져오기
	@Scheduled(cron="0 0 02 * * ?")
	public void scheduleM12() {
		getProductOfM12(true);
	}
	
	//M12 실상품가져오기
	@Scheduled(cron="0 30 02 * * ?")
	public void scheduleM12Test() {
		getProductOfM12(false);
	}
	
	//쿠프 실상품
	@Scheduled(cron="0 0 03 * * ?")
	public void scheduleCoup() {
		getProductOfCoupList(true);
	}
	
	//쿠프 테스트상품
	@Scheduled(cron="0 30 03 * * ?")
	public void scheduleCoupTest() {
		getProductOfCoupList(false);
	}
	
	//쿠프 전체 상품 코드 가져오기 
	public void getProductOfCoupList(boolean isReal) {	
		OnReceiveListener listener = new OnReceiveListener() {
			
			@Override
			public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
				cause.printStackTrace();
		        ctx.close();
			}
			
			@Override
			public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
				if (msg instanceof HttpContent) {
					HttpContent content = (HttpContent) msg;
					String strContent = content.content().toString(CharsetUtil.UTF_8);
					//System.out.println("content:" + strContent);

					try {
						InputStream in = new ByteArrayInputStream(strContent.getBytes("utf-8"));
						SAXBuilder builder = new SAXBuilder();
						Document document = (Document) builder.build(in);
						Element rootNode = document.getRootElement();

						Element listNode =rootNode.getChild("LIST", rootNode.getNamespace());
						
						if(listNode != null) {
							//전체 쿠폰 정보 읽기
							goodsList.clear();
							List<Element> t11List = listNode.getChildren("T11_VIEW", rootNode.getNamespace());
							for(int i=0; i<t11List.size(); ++i) {
								String couponCode = t11List.get(i).getChildText("COUPONCODE", rootNode.getNamespace());
								goodsList.put(couponCode, null);
							}
							
							for(String coupon : goodsList.keySet()) {
								System.out.println("coupon code:" + coupon);
							}
							
							for(String couponCode : goodsList.keySet()) {
								if(isReal) {
									getProductOfCoup(couponCode, true);
								} else {
									getProductOfCoup(couponCode, false);
								}
							}
						} 
					} catch (IOException io) {
						System.out.println(io.getMessage());
					} catch (JDOMException jdomex) {
						System.out.println(jdomex.getMessage());
					}  catch (Exception e) {
						System.out.println(e.getMessage());
					}
				}
			}
		};
		
		HttpClientHandler handler = new HttpClientHandler(listener);
		HttpClient client = new HttpClient(false, handler);
		
		//보낼 데이터 설정
		String url;
		if (isReal) {
			url = "http://v3api.inumber.co.kr/serviceapi.asmx/ServiceCouponList_02?CODE=0577&PASS=hli123&DOCCODE=0577000";
			return;
		} else {
			url = "http://issuev3apitest.m2i.kr:9999/serviceapi.asmx/ServiceCouponList_02?CODE=0424&PASS=hlint123&DOCCODE=0424000";
		}
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
	
	//쿠프 개별 상품 가져오기
	public void getProductOfCoup(String couponCode, boolean isReal) {	
		OnReceiveListener listener = new OnReceiveListener() {
			@Override
			public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
				cause.printStackTrace();
		        ctx.close();
			}
			
			@Override
			public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
				if (msg instanceof HttpContent) {
					HttpContent content = (HttpContent) msg;
					String strContent = content.content().toString(CharsetUtil.UTF_8);
					System.out.println("content:" + strContent);
					
					try {
						InputStream in = new ByteArrayInputStream(strContent.getBytes("utf-8"));
						SAXBuilder builder = new SAXBuilder();
						Document document = (Document) builder.build(in);
						Element rootNode = document.getRootElement();

						Element resultCode =rootNode.getChild("RESULTCODE", rootNode.getNamespace());
						
						if(resultCode != null) {
							//쿠폰 정보 저장
							GoodsVO goods = new GoodsVO();
							
							goods.setProvider(2);
							goods.setGoods_code(couponCode);
							goods.setGoods_name(rootNode.getChildText("COUPONNAME" ,rootNode.getNamespace()));
							goods.setBrand_name(rootNode.getChildText("COMP_NAME",rootNode.getNamespace()));
							goods.setSell_price(rootNode.getChildText("SEL_PRICE",rootNode.getNamespace()));
							goods.setMarket_price(rootNode.getChildText("USEPRICE",rootNode.getNamespace()));
							goods.setAdj_price(rootNode.getChildText("ADJ_PRICE",rootNode.getNamespace()));
							goods.setThumbnail(rootNode.getChildText("BASIC_IMG",rootNode.getNamespace()));
							goods.setGoods_info(rootNode.getChildText("USE_AREA",rootNode.getNamespace()));
							goods.setUse_note(rootNode.getChildText("USE_NOTE",rootNode.getNamespace()));
							goods.setUse_term(rootNode.getChildText("USE_TERM",rootNode.getNamespace()));
							
							if(isReal) {
								goods.setReal(true);
							} else {
								goods.setReal(false);
							}
							
							System.out.println("goods:" + goods);
							
							adminService.saveGoods(goods);
						} 
					} catch (IOException io) {
						System.out.println(io.getMessage());
					} catch (JDOMException jdomex) {
						System.out.println(jdomex.getMessage());
					}  catch (Exception e) {
						System.out.println(e.getMessage());
					}
				}
			}
		};
		
		HttpClientHandler handler = new HttpClientHandler(listener);
		HttpClient client = new HttpClient(false, handler);
		
		//보낼 데이터 설정
		String url;
		if(isReal) {
			url = "http://v3api.inumber.co.kr/serviceapi.asmx/ServiceCouponInfo?CODE=0577&PASS=hli123&COUPONCODE=" + couponCode;
			return;
		} else {
			url = "http://issuev3apitest.m2i.kr:9999/serviceapi.asmx/ServiceCouponInfo?CODE=0424&PASS=hlint123&COUPONCODE=" + couponCode;
		}

		try {
			URI uri = new URI(url);
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
	
	//M12 상품 정보 가져오기-----------------------------------------------------------------------------------------
	public void getProductOfM12(boolean isReal) {
		OnReceiveListener listener = new OnReceiveListener() {
			@Override
			public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
				cause.printStackTrace();
				System.err.println("exception");
		        ctx.close();
			}
			
			@Override
			public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
				System.err.println("channelRead0");
				
				if (msg instanceof HttpContent) {
					HttpContent content = (HttpContent) msg;

					ByteBuf buffer = content.content();
					byte [] array = new byte[buffer.capacity()];
					for (int i = 0; i < buffer.capacity(); i ++) {
						array[i] = buffer.getByte(i);
					}
					
					String strContent = new String(array, "euc-kr");
					//System.out.println("content:" + strContent);
					
					try {
						InputStream in = new ByteArrayInputStream(strContent.getBytes("euc-kr"));
						SAXBuilder builder = new SAXBuilder();
						Document document = (Document) builder.build(in);
						Element rootNode = document.getRootElement();
						List list = rootNode.getChildren("ITEM");
						System.out.println("list size()" + list.size());

						for (int i = 0; i < list.size(); i++) {
							Element node = (Element) list.get(i);
							GoodsVO goods = new GoodsVO();
							
							goods.setProvider(1);
							goods.setBrand_name(node.getChildText("BRAND_NAME"));
							goods.setGoods_name(node.getChildText("GOODS_NAME"));
							goods.setGoods_code(node.getChildText("GOODS_CODE"));
							goods.setThumbnail(node.getChildText("THUMBNAIL"));
							goods.setMarket_price(node.getChildText("MARKET_PRICE"));
							goods.setSell_price(node.getChildText("SELL_PRICE"));
							goods.setGoods_info(node.getChildText("GOODS_INFO"));
							goods.setReal(isReal);

							System.out.println("goods:" + goods);

							adminService.saveGoods(goods);
							Thread.sleep(100);
						}
					} catch (IOException io) {
						System.out.println(io.getMessage());
					} catch (JDOMException jdomex) {
						System.out.println(jdomex.getMessage());
					}  catch (Exception e) {
						System.out.println(e.getMessage());
					}
				}
			}
		};
		
		HttpClientHandler handler = new HttpClientHandler(listener);
		HttpClient client = new HttpClient(false, handler);
		
		//보낼 데이터 설정
		String url;
		if (isReal) {
			url = "http://web6.m12.co.kr:12101/app/goods_list_total.php?marketcode=HLINTNL01";
		} else {
			url = "http://web6.m12.co.kr:12101/app/dev/goods_list_total.php?marketcode=HLINTNL01";
		}
		
		//String url = "http://web6.m12.co.kr:12101/app/dev/goods_list_total.php";
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
