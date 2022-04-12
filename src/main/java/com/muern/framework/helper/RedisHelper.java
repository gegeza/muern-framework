package com.muern.framework.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * @author gegeza
 * @date 2020-08-19 10:38 AM
 */
@Component
public class RedisHelper {

    public static final Logger LOGGER = LoggerFactory.getLogger(RedisHelper.class);
    /** 释放分布式锁的LUA脚本 */
    public static final String UNLOCK_LUA;

    /**
     * 释放锁脚本，原子操作
     */
    static {
        StringBuilder sb = new StringBuilder();
        sb.append("if redis.call(\"get\",KEYS[1]) == ARGV[1] ");
        sb.append("then ");
        sb.append("    return redis.call(\"del\",KEYS[1]) ");
        sb.append("else ");
        sb.append("    return 0 ");
        sb.append("end ");
        UNLOCK_LUA = sb.toString();
    }

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 获取分布式锁，原子操作
     * @param lockKey 分布式锁的key
     * @param requestId 分布式锁key对应的value 可以使用UUID.randomUUID().toString();
     * @param expire 锁过期时间
     * @param timeUnit 时间单位
     * @return 获取锁的结果
     */
    public boolean tryLock(String lockKey, String requestId, long expire, TimeUnit timeUnit) {
        try{
            RedisCallback<Boolean> callback = (connection) -> connection.set(lockKey.getBytes(StandardCharsets.UTF_8), requestId.getBytes(StandardCharsets.UTF_8),
                    Expiration.seconds(timeUnit.toSeconds(expire)), RedisStringCommands.SetOption.SET_IF_ABSENT);
            return redisTemplate.execute(callback);
        } catch (Exception e) {
            LOGGER.error("redis lock error.", e);
        }
        return false;
    }

    /**
     * 释放分布式锁，原子操作
     * @param lockKey 分布式锁key
     * @param requestId 分布式锁key对应的value
     * @return 释放锁结果
     */
    public boolean releaseLock(String lockKey, String requestId) {
        RedisCallback<Boolean> callback = (connection) ->
                connection.eval(UNLOCK_LUA.getBytes(), ReturnType.BOOLEAN ,1, lockKey.getBytes(StandardCharsets.UTF_8), requestId.getBytes(StandardCharsets.UTF_8));
        return redisTemplate.execute(callback);
    }

    /** 获取缓存中key对应的value */
    public String getStr(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    /** 获取缓存中key对应的value */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /** value++ */
    public Long incr(String key) {
        return incr(key, 1);
    }

    /** value += {delta} */
    public Long incr(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /** value-- */
    public Long decr(String key) {
        return decr(key, 1);
    }

    /** value -= {delta} */
    public Long decr(String key, long delta) {
        return redisTemplate.opsForValue().decrement(key, delta);
    }

    /** 添加key-value */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /** 添加key-value 单位：秒 */
    public void set(String key, Object value, long seconds) {
        redisTemplate.opsForValue().set(key, value, seconds, TimeUnit.SECONDS);
    }

    /** Key不存在则设置并返回true，否则不设置并返回false */
    public boolean setNX(String key, Object value) {
        return redisTemplate.opsForValue().setIfAbsent(key, value);
    }

    /**  Key不存在则设置并返回true，否则不设置并返回false */
    public boolean setNX(String key, Object value, long seconds) {
        return redisTemplate.opsForValue().setIfAbsent(key, value, seconds, TimeUnit.SECONDS);
    }

    /** 更新key剩余时间，单位：秒 */
    public boolean expire(String key, long seconds) {
        return redisTemplate.expire(key, seconds, TimeUnit.SECONDS);
    }

    /** 查询key剩余时间，单位：秒 */
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /** 删除key对应的value */
    public void del(String key) {
        redisTemplate.delete(key);
    }

    /**  批量删除对应的value */
    public void del(Collection<String> keys) {
        redisTemplate.delete(keys);
    }

    /** 根据正则批量删除key */
    public void delPattern(final String pattern) {
        redisTemplate.delete(redisTemplate.keys(pattern));
    }

    /** 判断缓存中是否存在key */
    public boolean exists(final String key) {
        return redisTemplate.hasKey(key);
    }
}
