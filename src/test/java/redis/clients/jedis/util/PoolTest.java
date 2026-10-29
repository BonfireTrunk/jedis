package redis.clients.jedis.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisException;
import today.bonfire.oss.sop.PooledObjectFactory;
import today.bonfire.oss.sop.SimpleObjectPoolConfig;

/**
 * The exceptions {@link Pool#getResource()} throws, as documented in the v7-to-v8 migration guide.
 */
public class PoolTest {

  @Test
  public void connectionFailureIsThrownUnchanged() {
    JedisConnectionException failure = new JedisConnectionException("connect failed");
    try (Pool<Object> pool = new Pool<>(failingFactory(failure), PlainPoolConfig.create())) {
      assertSame(failure, assertThrows(JedisConnectionException.class, pool::getResource));
    }
  }

  @Test
  public void connectionFailureIsThrownUnchangedWhenTheWaitEndsBeforeARetry() {
    JedisConnectionException failure = new JedisConnectionException("connect failed");
    // The retry delay outlasts the wait, so the borrow deadline passes between creation attempts
    SimpleObjectPoolConfig config = PlainPoolConfig.builder().maxRetries(3)
        .retryCreationDelay(Duration.ofSeconds(10)).waitingForObjectTimeout(Duration.ofMillis(50))
        .build();
    try (Pool<Object> pool = new Pool<>(failingFactory(failure), config)) {
      assertSame(failure, assertThrows(JedisConnectionException.class, pool::getResource));
    }
  }

  @Test
  public void exhaustedPoolThrowsJedisException() {
    SimpleObjectPoolConfig config = PlainPoolConfig.builder().maxPoolSize(1)
        .waitingForObjectTimeout(Duration.ZERO).build();
    try (Pool<Object> pool = new Pool<>(objectFactory(), config)) {
      pool.getResource();
      JedisException e = assertThrows(JedisException.class, pool::getResource);
      assertFalse(e instanceof JedisConnectionException);
    }
  }

  @Test
  public void closedPoolThrowsJedisException() {
    Pool<Object> pool = new Pool<>(objectFactory(), PlainPoolConfig.create());
    pool.close();
    JedisException e = assertThrows(JedisException.class, pool::getResource);
    assertFalse(e instanceof JedisConnectionException);
  }

  private static PooledObjectFactory<Object> objectFactory() {
    return new PooledObjectFactory<Object>() {
      @Override
      public Object createObject() {
        return new Object();
      }

      @Override
      public void destroyObject(Object obj) {
      }
    };
  }

  private static PooledObjectFactory<Object> failingFactory(RuntimeException failure) {
    return new PooledObjectFactory<Object>() {
      @Override
      public Object createObject() {
        throw failure;
      }

      @Override
      public void destroyObject(Object obj) {
      }
    };
  }
}
