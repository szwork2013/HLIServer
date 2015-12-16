package com.hli.result;

public class Result {
	private int result = 0;
	private String msg;
	
	public Result() {
		
	}
	
	public Result(int result, String msg) {
		this.result = result;
		this.msg = msg;
	}
	
	public int getResult() {
		return result;
	}
	public void setResult(int result) {
		this.result = result;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	
}
