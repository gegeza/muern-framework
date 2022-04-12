package com.muern.framework.exception;

import com.muern.framework.common.Code;
import com.muern.framework.common.CodeImpl;
import com.muern.framework.common.Result;

/**
 * 自定义业务异常类
 * @author gegeza
 * @date 2022-04-07
 */
public class BizException extends RuntimeException {
    static final long serialVersionUID = -5034897190745766938L;

    protected String code;

    public BizException(String code, String message) {
        super(message);
        this.code = code;
    }

    public BizException(String message) {
        this(CodeImpl.FAIL.getCode(), message);
    }

    public BizException(Result<?> result) {
        this(result.getCode(), result.getDesc());
    }

    public BizException(Code code){
        this(code.getCode(), code.getDesc());
    }

    public BizException(String code, String message, Object... args) {
        super(String.format(message, args));
        this.code = code;
    }

    public BizException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}

