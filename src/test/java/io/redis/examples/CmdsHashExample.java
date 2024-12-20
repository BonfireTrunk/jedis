// EXAMPLE: cmds_hash
// REMOVE_START
package io.redis.examples;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.UnifiedJedis;

import java.util.HashMap;
import java.util.Map;

// HIDE_END

// HIDE_START
public class CmdsHashExample {
    @Test
    public void run() {
        UnifiedJedis jedis = new UnifiedJedis("redis://localhost:6379");

        //REMOVE_START
        // Clear any keys here before using them in tests.
        jedis.del("myhash");
        //REMOVE_END
// HIDE_END


        // STEP_START hdel

        // STEP_END

        // Tests for 'hdel' step.
        // REMOVE_START

        // REMOVE_END


        // STEP_START hexists

        // STEP_END

        // Tests for 'hexists' step.
        // REMOVE_START

        // REMOVE_END


        // STEP_START hexpire

        // STEP_END

        // Tests for 'hexpire' step.
        // REMOVE_START

        // REMOVE_END


        // STEP_START hexpireat

        // STEP_END

        // Tests for 'hexpireat' step.
        // REMOVE_START

        // REMOVE_END


        // STEP_START hexpiretime

        // STEP_END

        // Tests for 'hexpiretime' step.
        // REMOVE_START

        // REMOVE_END


        // STEP_START hget
        Map<String, String> hGetExampleParams = new HashMap<>();
        hGetExampleParams.put("field1", "foo");

        long hGetResult1 = jedis.hset("myhash", hGetExampleParams);
        System.out.println(hGetResult1);    // >>> 1

        String hGetResult2 = jedis.hget("myhash", "field1");
        System.out.println(hGetResult2);    // >>> foo

        String hGetResult3 = jedis.hget("myhash", "field2");
        System.out.println(hGetResult3);    // >>> null
        // STEP_END

        // Tests for 'hget' step.
        // REMOVE_START
        Assertions.assertEquals(1, hGetResult1);
        Assertions.assertEquals("foo", hGetResult2);
        Assertions.assertNull(hGetResult3);
        jedis.del("myhash");
        // REMOVE_END


        // STEP_START hgetall

        // STEP_END

        // Tests for 'hgetall' step.
        // REMOVE_START

        // REMOVE_END


        // STEP_START hincrby

        // STEP_END

        // Tests for 'hincrby' step.
        // REMOVE_START

        // REMOVE_END


        // STEP_START hincrbyfloat

        // STEP_END

        // Tests for 'hincrbyfloat' step.
        // REMOVE_START

        // REMOVE_END


        // STEP_START hkeys

        // STEP_END

        // Tests for 'hkeys' step.
        // REMOVE_START

        // REMOVE_END


        // STEP_START hlen

        // STEP_END

        // Tests for 'hlen' step.
        // REMOVE_START

        // REMOVE_END


        // STEP_START hmget

        // STEP_END

        // Tests for 'hmget' step.
        // REMOVE_START

        // REMOVE_END


        // STEP_START hmset

        // STEP_END

        // Tests for 'hmset' step.
        // REMOVE_START

        // REMOVE_END


        // STEP_START hpersist

        // STEP_END

        // Tests for 'hpersist' step.
        // REMOVE_START

        // REMOVE_END


        // STEP_START hpexpire

        // STEP_END

        // Tests for 'hpexpire' step.
        // REMOVE_START

        // REMOVE_END


        // STEP_START hpexpireat

        // STEP_END

        // Tests for 'hpexpireat' step.
        // REMOVE_START

        // REMOVE_END


        // STEP_START hpexpiretime

        // STEP_END

        // Tests for 'hpexpiretime' step.
        // REMOVE_START

        // REMOVE_END


        // STEP_START hpttl

        // STEP_END

        // Tests for 'hpttl' step.
        // REMOVE_START

        // REMOVE_END


        // STEP_START hrandfield

        // STEP_END

        // Tests for 'hrandfield' step.
        // REMOVE_START

        // REMOVE_END


        // STEP_START hscan

        // STEP_END

        // Tests for 'hscan' step.
        // REMOVE_START

        // REMOVE_END


        // STEP_START hset
        Map<String, String> hSetExampleParams = new HashMap<>();
        hSetExampleParams.put("field1", "Hello");
        long hSetResult1 = jedis.hset("myhash", hSetExampleParams);
        System.out.println(hSetResult1);    // >>> 1

        String hSetResult2 = jedis.hget("myhash", "field1");
        System.out.println(hSetResult2);    // >>> Hello

        hSetExampleParams.clear();
        hSetExampleParams.put("field2", "Hi");
        hSetExampleParams.put("field3", "World");
        long hSetResult3 = jedis.hset("myhash",hSetExampleParams);
        System.out.println(hSetResult3);    // >>> 2

        String hSetResult4 = jedis.hget("myhash", "field2");
        System.out.println(hSetResult4);    // >>> Hi

        String hSetResult5 = jedis.hget("myhash", "field3");
        System.out.println(hSetResult5);    // >>> World

        Map<String, String> hSetResult6 = jedis.hgetAll("myhash");
        
        for (String key: hSetResult6.keySet()) {
            System.out.println("Key: " + key + ", Value: " + hSetResult6.get(key));
        }
        // >>> Key: field3, Value: World
        // >>> Key: field2, Value: Hi
        // >>> Key: field1, Value: Hello
        // STEP_END

        // Tests for 'hset' step.
        // REMOVE_START
        Assertions.assertEquals(1, hSetResult1);
        Assertions.assertEquals("Hello", hSetResult2);
        Assertions.assertEquals(2, hSetResult3);
        Assertions.assertEquals("Hi", hSetResult4);
        Assertions.assertEquals("World", hSetResult5);
        Assertions.assertEquals(3, hSetResult6.size());
        Assertions.assertEquals("Hello", hSetResult6.get("field1"));
        Assertions.assertEquals("Hi", hSetResult6.get("field2"));
        Assertions.assertEquals("World", hSetResult6.get("field3"));
        jedis.del("myhash");
        // REMOVE_END


        // STEP_START hsetnx

        // STEP_END

        // Tests for 'hsetnx' step.
        // REMOVE_START

        // REMOVE_END


        // STEP_START hstrlen

        // STEP_END

        // Tests for 'hstrlen' step.
        // REMOVE_START

        // REMOVE_END


        // STEP_START httl

        // STEP_END

        // Tests for 'httl' step.
        // REMOVE_START

        // REMOVE_END


        // STEP_START hvals

        // STEP_END

        // Tests for 'hvals' step.
        // REMOVE_START

        // REMOVE_END


// HIDE_START
        
    }
}
// HIDE_END

