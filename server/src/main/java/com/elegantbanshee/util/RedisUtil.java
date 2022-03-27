package com.elegantbanshee.util;

import org.json.JSONArray;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.concurrent.locks.ReentrantLock;

public class RedisUtil {
    private static final String TOP_IMAGES_KEY = "TOP_IMAGES";
    private static JedisPool pool;
    private static final ReentrantLock LOCK = new ReentrantLock();


    public static JSONArray getTopImages() {
        LOCK.lock();
        String topString;
        try (Jedis jedis = pool.getResource()) {
            topString = jedis.get(TOP_IMAGES_KEY);
        }
        LOCK.unlock();
        if (topString == null)
            topString = "[]";
        return new JSONArray(topString);
    }

    public static void pushImage(String id) {
        JSONArray images = getTopImages();
        if (images.length() > 10)
            images = new JSONArray();
        images.put(id);

        LOCK.lock();
        try (Jedis jedis = pool.getResource()) {
            jedis.set(TOP_IMAGES_KEY, images.toString());
        }
        LOCK.unlock();
    }

    public static void init() {
        pool = new JedisPool(System.getenv("REDIS_URL"));
    }
}
