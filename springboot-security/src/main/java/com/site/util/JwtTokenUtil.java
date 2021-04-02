package com.site.util;

import com.site.common.config.TokenConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.CompressionCodecs;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.Map;

/**
 * jwt token 工具类
 */
public class JwtTokenUtil {

    /**
     * 发行者
     */
    private static String issuer;

    /**
     * 过期时间
     */
    private static long expired;

    /**
     * 密钥
     */
    private static String secret;

    public static void setTokenSettings(TokenConfig tokenConfig){
        issuer = tokenConfig.getIssuer();
        expired = tokenConfig.getExpire().toMillis();
        secret = tokenConfig.getSecret();
    }

    /**
     * 生成 jwt token
     * @param subject 主体（subject 代表这个JWT的主体，即它的所有人 一般是用户id，但这里为了方便用username）
     * @param claims  载荷-参数 claim("name","小二")
     * @return
     */
    public static String generateToken(String subject, Map<String, Object> claims){
        return generateToken(issuer,subject,claims,expired,secret);
    }

    /**
     * 生成 jwt token
     * @param issuer  发行者
     * @param subject 主体（subject 代表这个JWT的主体，即它的所有人 一般是用户id，但这里为了方便用username）
     * @param claims  载荷-参数 claim("name","小二")
     * @param expired 过期时间(秒)
     * @param secret  密钥
     * @return
     */
    public static String generateToken(String issuer, String subject, Map<String, Object> claims, long expired, String secret) {
        JwtBuilder builder = Jwts.builder();
        builder.setHeaderParam("typ","JWT");
        // 一定要先设置这claim赋值，一旦写在标准的声明赋值之后，就是覆盖了那些标准的声明的 subject
        if (null != claims){// 参数 claim("name","小二")
            builder.setClaims(claims);
        }
        if (null != issuer && !issuer.equals("")){// 发行者
            builder.setIssuer(issuer);
        }
        if (null != subject && !subject.equals("")){ // 主体（subject 代表这个JWT的主体，即它的所有人 一般是用户id）
            builder.setSubject(subject);
        }
        if (expired >= 0){
            builder.setIssuedAt(new Date()) // 发行时间
                    .setExpiration(new Date(System.currentTimeMillis() + expired))
                    .signWith(SignatureAlgorithm.HS256,secret) // 签名类型 与 密钥
                    .compressWith(CompressionCodecs.DEFLATE);// 对载荷进行压缩
        }
        return builder.compact();// 压缩一下
    }

    /**
     * 解析 token
     * @param token
     * @return
     */
    public static Claims parseToken(String token){
        try{
            final Claims claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
            return claims;
        }catch (Exception e){}
        return null;
    }

    /**
     * 验证是否过期
     * @param token
     * @return
     */
    public static boolean isTokenExpired(String token){
        try{
            Claims claims = parseToken(token);
            return claims != null && !claims.getExpiration().before(new Date());
        }catch (Exception e){
            return true;
        }
    }

    /**
     * 验证token是否验证通过
     * @param token
     * @return
     */
    public static boolean validate(String token){
        try{
            return isTokenExpired(token);
        }catch (Exception e){
            return false;
        }
    }

    /**
     * 获取用户username
     * @param token
     * @return
     */
    public static String getSubject(String token){
        try{
            Claims claims = parseToken(token);
            return claims != null ? claims.getSubject() : null;
        }catch (Exception e){
            return null;
        }
    }

    /**
     * 获取token的剩余过期时间
     * @param token
     * @return
     */
    public static long getRemainingTime(String token){
        long time = 0;
        try{
            Claims claims = parseToken(token);
            time = claims.getExpiration().getTime() - System.currentTimeMillis();
        }catch (Exception e){}
        return time;
    }
}
