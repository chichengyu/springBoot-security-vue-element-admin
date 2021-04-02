package com.site.common.exception;

import com.site.common.enums.BaseCodeEnum;

/**
 * 异常
 */
public class BusinessException extends RuntimeException {

    private int code;

    private String message;

    public BusinessException(String message){
        super(message);
        this.message = message;
    }

    public BusinessException(String message,int code){
        super(message);
        this.code = code;
        this.message = message;
    }

    public BusinessException(BaseCodeEnum baseCodeEnum){
        this(baseCodeEnum.getMessage(),baseCodeEnum.getCode());
    }

    public int getCode(){
        return code;
    }

    public String getMessage(){
        return message;
    }
}
