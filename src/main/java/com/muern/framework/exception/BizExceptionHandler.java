package com.muern.framework.exception;

import com.muern.framework.common.CodeImpl;
import com.muern.framework.common.Result;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 业务异常处理器
 * 
 * @author gegeza
 * @date 20222-04-07
 */
public class BizExceptionHandler {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(BizExceptionHandler.class);

    @ExceptionHandler({BizException.class})
    @ResponseBody
    public Result<Void> bizExceptionHandler(BizException e) {
        LOGGER.error(e.getMessage(), e);
        return Result.ins(e.getCode(), e.getMessage());
    }

    @ExceptionHandler({Exception.class})
    @ResponseBody
    public Result<Void> exceptionErrorHandler(Exception e) {
        LOGGER.error(e.getMessage(), e);
        return Result.ins(CodeImpl.FAIL.getCode(), e.getMessage());
    }    
}