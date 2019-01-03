package com.github.eriksen.seckilling.peresistence.model;

import java.time.Duration;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

import com.github.eriksen.seckilling.utils.CacheConst;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import lombok.Data;

/**
 * ActivityCache
 */
@Data
@RedisHash(value = CacheConst.ACTIVITY_CACHE_KEY_PREFIX)
public class ActivityCache {

    @Id
    private String id;

    private Date startTime;

    private Date endTime;

    @TimeToLive
    public long getTimeToLive() {
        return Duration.between(this.endTime.toInstant().atZone(ZoneId.systemDefault()).toLocalTime(), LocalTime.now())
                .toMillis();
    }
}