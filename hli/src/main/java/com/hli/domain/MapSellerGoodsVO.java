package com.hli.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MapSellerGoodsVO {
	private int id;
	private int seller_id;
	private int goods_id;
	private String commission;
	private String goods_name;
	private String sell_price;
	private int use_yn; //0:사용, 1:사용안함
}
