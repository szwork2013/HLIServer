package com.hli.result;

public class ResultData<T> extends Result {
	private T data;
	
	public ResultData() {

	}
	
	public ResultData(int result, String msg) {
		super(result, msg);
	}

	public ResultData(T data) {
		this.data = data;
	}
	
	public ResultData(int result, String msg, T data) {
		super(result, msg);
		this.data = data;
	}

	
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}

}
