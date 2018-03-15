package com.cmx.shiroservice.session.cache;

import com.cmx.shiroservice.session.manager.IRedisManager;
import com.cmx.shiroservice.session.serializer.ObjectSerializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.CollectionUtils;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.*;


@Slf4j
public class RedisCache<K, V> implements Cache<K, V> {

    private static final String DEFAULT_CACHE_KEY_PREFIX = "shiro:cache:";

    private RedisSerializer keySerializer = new StringRedisSerializer();
    private RedisSerializer valueSerializer = new ObjectSerializer();
    private IRedisManager redisManager;
    private String keyPrefix = DEFAULT_CACHE_KEY_PREFIX;

    /**
     * Construction
     * @param redisManager
     */
    public RedisCache(IRedisManager redisManager, RedisSerializer keySerializer, RedisSerializer valueSerializer){
        if (redisManager == null) {
            throw new IllegalArgumentException("Cache argument cannot be null.");
        }
        this.redisManager = redisManager;
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;
    }

    /**
     * Constructs a cache instance with the specified
     * Redis manager and using a custom key prefix.
     * @param cache The cache manager instance
     * @param prefix The Redis key prefix
     */
    public RedisCache(IRedisManager cache, RedisSerializer keySerializer, RedisSerializer valueSerializer,
                      String prefix){
        this( cache, keySerializer, valueSerializer );
        this.keyPrefix = prefix;
    }

    @Override
    public V get(K key) throws CacheException {
        log.debug("get key [" + key + "]");

        if (key == null) {
            return null;
        }

        try {
            Object redisCacheKey = getRedisCacheKey(key);
            byte[] rawValue = redisManager.get(keySerializer.serialize(redisCacheKey));
            V value = (V) valueSerializer.deserialize(rawValue);
            return value;
        } catch (SerializationException e) {
            throw new CacheException(e);
        }
    }

    @Override
    public V put(K key, V value) throws CacheException {
        log.debug("put key [" + key + "]");
        try {
            Object redisCacheKey = getRedisCacheKey(key);
            redisManager.set(keySerializer.serialize(redisCacheKey), valueSerializer.serialize(value));
            return value;
        } catch (SerializationException e) {
            throw new CacheException(e);
        }
    }

    @Override
    public V remove(K key) throws CacheException {
        log.debug("remove key [" + key + "]");
        if (key == null) {
            return null;
        }
        try {
            Object redisCacheKey = getRedisCacheKey(key);
            byte[] rawValue = redisManager.get(keySerializer.serialize(redisCacheKey));
            V previous = (V) valueSerializer.deserialize(rawValue);
            redisManager.del(keySerializer.serialize(redisCacheKey));
            return previous;
        } catch (SerializationException e) {
            throw new CacheException(e);
        }
    }

    private Object getRedisCacheKey(K key) {
        if (key == null) {
            return null;
        }
        Object redisKey = key;
        if (keySerializer instanceof StringRedisSerializer) {
            redisKey = getStringRedisKey(key);
        }
        if (redisKey instanceof String) {
            return this.keyPrefix + redisKey;
        }
        return redisKey;
    }

    private Object getStringRedisKey(K key) {
        Object redisKey;
        if (key instanceof PrincipalCollection) {
            redisKey = getRedisKeyFromPrincipalCollection((PrincipalCollection) key);
        } else {
            redisKey = key.toString();
        }
        return redisKey;
    }

    private Object getRedisKeyFromPrincipalCollection(PrincipalCollection key) {
        Object redisKey;
        List<String> realmNames = getRealmNames(key);
        Collections.sort(realmNames);
        redisKey = joinRealmNames(realmNames);
        return redisKey;
    }

    private List<String> getRealmNames(PrincipalCollection key) {
        List<String> realmArr = new ArrayList<>();
        Set<String> realmNames = key.getRealmNames();
        for (String realmName: realmNames) {
            realmArr.add(realmName);
        }
        return realmArr;
    }

    private Object joinRealmNames(List<String> realmArr) {
        Object redisKey;
        StringBuilder redisKeyBuilder = new StringBuilder();
        for (int i = 0; i < realmArr.size(); i++) {
            String s = realmArr.get(i);
            redisKeyBuilder.append(s);
        }
        redisKey = redisKeyBuilder.toString();
        return redisKey;
    }

    @Override
    public void clear() throws CacheException {
        log.debug("clear cache");
        Set<byte[]> keys = null;
        try {
            keys = redisManager.keys(keySerializer.serialize(this.keyPrefix + "*"));
        } catch (SerializationException e) {
            log.error("get keys error", e);
        }
        if (keys == null || keys.size() == 0) {
            return;
        }
        for (byte[] key: keys) {
            redisManager.del(key);
        }
    }

    @Override
    public int size() {
        Long longSize = new Long(redisManager.dbSize());
        return longSize.intValue();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<K> keys() {
        Set<byte[]> keys = null;
        try {
            keys = redisManager.keys(keySerializer.serialize(this.keyPrefix + "*"));
        } catch (SerializationException e) {
            log.error("get keys error", e);
            return Collections.emptySet();
        }

        if (CollectionUtils.isEmpty(keys)) {
            return Collections.emptySet();
        }

        Set<K> convertedKeys = new HashSet<>();
        for(byte[] key:keys){
            try {
                convertedKeys.add((K)keySerializer.deserialize(key));
            } catch (SerializationException e) {
                log.error("deserialize keys error", e);
            }
        }
        return convertedKeys;
    }

    @Override
    public Collection<V> values() {
        Set<byte[]> keys = null;
        try {
            keys = redisManager.keys(keySerializer.serialize(this.keyPrefix + "*"));
        } catch (SerializationException e) {
            log.error("get values error", e);
            return Collections.emptySet();
        }

        if(CollectionUtils.isEmpty(keys)) {
            return Collections.emptySet();
        }

        List<V> values = new ArrayList<V>(keys.size());
        for (byte[] key : keys) {
            V value = null;
            try {
                value = (V)valueSerializer.deserialize(redisManager.get(key));
            } catch (SerializationException e) {
                log.error("deserialize values= error", e);
            }
            if (value != null) {
                values.add(value);
            }
        }
        return Collections.unmodifiableList(values);
    }

    public String getKeyPrefix() {
        return keyPrefix;
    }

    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }
}
