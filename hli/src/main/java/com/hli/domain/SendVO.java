package com.hli.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SendVO {
	private int send_id;
	private String created;
	private int seller_id;
	private int goods_id;
	private String goods_count;
	private String sell_price;
	private String recv_phone;
	private String send_phone;
	private String tr_id;
	private String userid;
	private String title;
	private String msg;
	private String status_code;
	private String result_code;
	private String exchange_code;
	private String couponnumber; //쿠프만 가능
	private String pinnumber; //쿠프만 가능
	
	private String company_name;
	private String brand_name;
	@JsonProperty
	private boolean isReal;
}
