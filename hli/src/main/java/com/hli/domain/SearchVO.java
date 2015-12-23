package com.hli.domain;

import lombok.Data;

@Data
public class SearchVO {
	private String search_key;
	private String search_value;
	private int start_index;
	private int page_size;
	private int total_count;
	private String order_key;
	
	private int seller_id;
	
}
