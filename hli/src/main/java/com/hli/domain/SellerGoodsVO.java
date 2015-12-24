package com.hli.domain;

import lombok.Data;

@Data
public class SellerGoodsVO {
	private String brand_name;
	private String goods_name;
	private String goods_code;
	private String thumbnail;
	private String market_price; //소비자/정상가격 쿠프는 useprice
	private String sell_price;   //판매가격
	private String adj_price;    //수수료 제외한 가격
	private String goods_info; //쿠프는 use_area
	private String use_note;   //쿠프만 존재
	private String use_term;   //쿠프만 존재
}
