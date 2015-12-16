package com.hli.result;

public class ResultDataTotal<T> extends ResultData<T> {
	private int total;
	
	public ResultDataTotal () {
		
	}
	
	public ResultDataTotal (int result, String msg, T data) {
		super(result, msg, data);
	}
	
	public ResultDataTotal (int result, String msg, T data, int total) {
		super(result, msg, data);
		this.total = total;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}
}
