package com.oracle.Enum;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;
import java.util.HashMap;

@JsonSerialize
public class Result<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 编码 v 成功， valerror 是字段验证失败 login-index 超时或者未登录跳转登录页面 error 错误
	 */
	private String code;

	/**
	 * 消息
	 */
	private String msg;

	/**
	 * 具体每个输入错误的消息
	 */
	private HashMap<String, String> errors = new HashMap<String, String>();

	/**
	 * 返回的数据
	 */
	private T tdata;

	private String exMsg;

	private String pkCode;

	public String getPkCode() {
		return pkCode;
	}

	public void setPkCode(String pkCode) {
		this.pkCode = pkCode;
	}

	public String getExMsg() {
		return exMsg;
	}

	public void setExMsg(String exMsg) {
		this.exMsg = exMsg;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg == null ? "" : msg;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public HashMap<String, String> getErrors() {
		return errors;
	}

	public void setErrors(HashMap<String, String> errors) {
		this.errors = errors;
	}

	public T getTdata() {
		return tdata;
	}

	public void setTdata(T tdata) {
		this.tdata = tdata;
	}

	public void setCM(String code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public void setCT(String code, T data) {
		this.code = code;
		this.tdata = data;
	}

	public void setCMT(String code, String msg, T data) {
		this.code = code;
		this.msg = msg;
		this.tdata = data;
	}

	public Result() {
		this.code = "v";
		this.exMsg = "";
		this.pkCode = "";
	}

	public Result(String code, T data) {
		super();
		this.code = code;
		this.tdata = data;
	}

	public Result(String code, String msg, T tdata) {
		super();
		this.code = code;
		this.msg = msg;
		this.tdata = tdata;
	}

	/**
	 * 返回成功消息
	 *
	 * @return
	 */
	public Result<T> success() {
		return new Result<T>().success("操作成功");
	}

	public Result<T> success(String msg) {
		return new Result<T>().success(msg, null);
	}

	public Result<T> success(T data) {
		return new Result<T>().success("操作成功", data);
	}

	public Result<T> success(String msg, T data) {
		return new Result<T>("v", msg, data);
	}

	/**
	 * 返回错误消息
	 *
	 * @return
	 */
	public Result<T> error() {
		return new Result<T>().error("操作失败");
	}

	public Result<T> error(String msg) {
		return new Result<T>().error(msg, null);
	}

	public Result<T> error(T data) {
		return new Result<T>().error("操作成功", data);
	}

	public Result<T> error(String msg, T data) {
		return new Result<T>("error", msg, data);
	}
}
