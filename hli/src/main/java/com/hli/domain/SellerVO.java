package com.hli.domain;

import lombok.Data;

@Data
public class SellerVO {
	private int seller_id;
	private String company_name;  //판매업체 이름
	private String mid;
	private String password;
	//private String code;  //코드
	private String person_name;  //담당자 이름
	private String contact;  //담당자 연락처
	private String email;  //담당자 이메일
	private String allowed_ip;  //허용 아이피
	private int sale_status;  //판매상태 1:대기중, 2:진행중, 3:중지
	
}