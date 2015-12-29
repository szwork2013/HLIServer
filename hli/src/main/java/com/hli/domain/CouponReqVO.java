package com.hli.domain;

import lombok.Data;

@Data
public class CouponReqVO {
	private String mid; //판매업체 아이디
	private String password; //판매업체 패스워드
	private int provider; //공급업체 1:M12, 2:coup
	private String goods_count;  //M12(max:5) coup(max:10)
	private String sell_price;   //판매가격
	private String goods_code;
	private String send_phone; //발신자
	private String recv_phone; //수신자
	private String message;
	private String tr_id; //거래코드
}
