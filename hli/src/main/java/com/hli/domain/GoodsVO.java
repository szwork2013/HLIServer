package com.hli.domain;

import lombok.Data;

@Data
public class GoodsVO {
	private int goods_id;
	private int provider; //공급업체 1:M12, 2:coup
	private String brand_name;
	private String goods_name;
	private String goods_code;
	private String thumbnail;
	private String market_price;
	private String sell_price;
	private String created;
	private String updated;
}
