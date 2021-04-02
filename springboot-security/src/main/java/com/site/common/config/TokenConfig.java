package com.site.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.time.Duration;

/**
 * kwt配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class TokenConfig {

    /**
     * 发行者
     */
    private String issuer;

    /**
     * 密钥
     */
    private String secret;

    /**
     * token 过期时间
     */
    private Duration expire;

}
