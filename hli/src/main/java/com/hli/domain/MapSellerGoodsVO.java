package com.hli.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MapSellerGoodsVO {
	private int seller_id;
	private int goods_id;
	private String commission;
}
