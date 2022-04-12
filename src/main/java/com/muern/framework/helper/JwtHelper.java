package com.muern.framework.helper;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author gegeza
 * @date 2019-12-06 11:31 AM
 */
@Component
@ConditionalOnClass(name = {"org.springframework.data.redis.core.RedisTemplate"})
public class JwtHelper {
    public static final Logger LOGGER = LoggerFactory.getLogger(JwtHelper.class);
    private static final String TOKEN_NAME = "Authorization";
    private static final String TOKEN_REDIS_KEY = "MUERN:JWT_TOKEN:";
    private static final Algorithm algorithm = Algorithm.HMAC256("CAFEBABE");
    private static final long TOKEN_REFRESH_INTERVAL = 30 * 60L;

    @Resource private RedisTemplate<String, Object> redisTemplate;

    /**
     * token是否过期
     * @return true：过期
     */
    public boolean isTokenExpired(String token) {
        if (StringUtils.isEmpty(token)) {
            return true;
        }
        try {
            //先判断redis中是否包含该token
            Boolean hasKey = redisTemplate.hasKey(TOKEN_REDIS_KEY.concat(token));
            if (hasKey != null && hasKey) {
                //再判断jwt中token是否过期
                DecodedJWT jwt = JWT.decode(token);
                return jwt.getExpiresAt().before(new Date());
            }
        } catch (JWTDecodeException e) {
            LOGGER.error("jwt decode error {}", e.getMessage());
        }
        return true;
    }

    /**
     * 生成签名,expireTime后过期
     * @param name 用户名
     * @param type 用户类型
     * @param expireTime token过期时间
     * @return 加密的token
     */
    public String sign(String customInfo, Long expireTime) {
        Date date = new Date(System.currentTimeMillis() + expireTime);
        // 附带name信息和type信息
        String token =  JWT.create()
                .withClaim("customInfo", customInfo)
                .withClaim("expireTime", expireTime)
                .withExpiresAt(date)
                .withIssuedAt(new Date())
                .sign(algorithm);
        //存入redis中
        redisTemplate.opsForValue().set(TOKEN_REDIS_KEY.concat(token), token, expireTime, TimeUnit.MILLISECONDS);
        return token;
    }

    /** 根据token判断是否需要刷新 如果需要刷新 则返回新的token  */
    public String shouldTokenRefresh(String token) {
        try {
            //解析token
            DecodedJWT jwt = JWT.decode(token);
            //获取签发时间
            LocalDateTime issueTime = LocalDateTime.ofInstant(jwt.getIssuedAt().toInstant(), ZoneId.systemDefault());
            //如果需要刷新
            if (LocalDateTime.now().minusSeconds(TOKEN_REFRESH_INTERVAL).isAfter(issueTime)) {
                String customInfo = jwt.getClaim("customInfo").asString();
                Long expireTime = jwt.getClaim("expireTime").asLong();
                return sign(customInfo, expireTime);
            }
        } catch (JWTDecodeException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    /** 从HttpServletRequest的请求头中获取token */
    public String getToken(HttpServletRequest request) {
        return request.getHeader(TOKEN_NAME);
    }

    /** 往HttpServletResponse的响应头中放入token */
    public void putToken(HttpServletResponse response, String token) {
        response.setHeader(TOKEN_NAME, token);
    }

    /** 从redis中移除token 实现登出 */
    public void removeToken(String token) {
        try {
            Boolean hasKey = redisTemplate.hasKey(TOKEN_REDIS_KEY.concat(token));
            if (hasKey != null && hasKey) {
                //从redis中移除token
                redisTemplate.delete(TOKEN_REDIS_KEY.concat(token));
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /** 根据token获取对应的登录账户名称 */
    public String getTokenName(String token) {
        try {
            return JWT.decode(token).getClaim("name").asString();
        } catch (JWTDecodeException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    /** 根据token获取对应的登录账户类型 */
    public String getTokenType(String token) {
        try {
            return JWT.decode(token).getClaim("type").asString();
        } catch (JWTDecodeException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }
}
