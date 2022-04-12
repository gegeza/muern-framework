package com.muern.framework.common;

import java.io.Serializable;

/**
 * @author gegeza
 * @date 2020-04-20 3:52 PM
 */
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 100000000000000000L;

    private String code;
    private String desc;
    private T data;

    private Result(String code, String desc, T data) {
        this.code = code;
        this.desc = desc;
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return Json.toStr(this);
    }

    public static <T> Result<T> succ() {
        return ins(CodeImpl.OK);
    }

    public static <T> Result<T> fail() {
        return ins(CodeImpl.FAIL);
    }

    public static <T> Result<T> ins(Code code) {
        return ins(code, null);
    }


    public static <T> Result<T> ins(T t) {
        return ins(CodeImpl.OK, t);
    }

    public static <T> Result<T> ins(Code code, T t) {
        return ins(code.getCode(), code.getDesc(), t);
    }

    public static <T> Result<T> ins(String code, String desc) {
        return ins(code, desc, null);
    }

    public static <T> Result<T> ins(String code, String desc, T t) {
        return new Result<>(code, desc, t);
    }

    /** 用于判断当前Result 是否是成功的 */
    public boolean ok() {
        return CodeImpl.OK.getCode().equals(this.code);
    }

    public T data() {
        return !ok() || data == null ? null : this.data;
    }
}
