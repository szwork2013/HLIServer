package com.hli.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

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
	//jackson 은 getIsReal, setIsReal을 필요로하지만 lombok은 그렇게 만들지 않는다.
	@JsonProperty
	private boolean isReal;
	
}
