package com.site.security;

import org.springframework.security.core.AuthenticationException;

/**
 * 验证码异常
 */
public class SecurityException extends AuthenticationException {
    /**
     * Constructs an {@code AuthenticationException} with the specified message and no
     * @param msg the detail message
     */
    public SecurityException(String msg) {
        super(msg);
    }
}
