package redis.clients.jedis;

import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisException;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class JedisSentinelPoolTest {

  protected static final HostAndPort sentinel1 = HostAndPorts.getSentinelServers().get(1);
  protected static final HostAndPort sentinel2 = HostAndPorts.getSentinelServers().get(3);
  private static final String MASTER_NAME = "mymaster";
  protected final Set<String> sentinels = new HashSet<>();

  @Before
  public void setUp() throws Exception {
    sentinels.clear();

    sentinels.add(sentinel1.toString());
    sentinels.add(sentinel2.toString());
  }

  @Test
  public void repeatedSentinelPoolInitialization() {

    for (int i = 0; i < 20; ++i) {
      var config = JedisPoolConfig.builder();

      JedisSentinelPool pool = new JedisSentinelPool(MASTER_NAME, sentinels, config.build(), 1000,
          "foobared", 2);
      pool.getResource().close();
      pool.destroy();
    }
  }

  @Test(expected = JedisConnectionException.class)
  public void initializeWithNotAvailableSentinelsShouldThrowException() {
    Set<String> wrongSentinels = new HashSet<String>();
    wrongSentinels.add(new HostAndPort("localhost", 65432).toString());
    wrongSentinels.add(new HostAndPort("localhost", 65431).toString());

    JedisSentinelPool pool = new JedisSentinelPool(MASTER_NAME, wrongSentinels);
    pool.destroy();
  }

  @Test(expected = JedisException.class)
  public void initializeWithNotMonitoredMasterNameShouldThrowException() {
    final String wrongMasterName = "wrongMasterName";
    JedisSentinelPool pool = new JedisSentinelPool(wrongMasterName, sentinels);
    pool.destroy();
  }

  @Test
  public void checkCloseableConnections() throws Exception {
    var config = JedisPoolConfig.builder();

    JedisSentinelPool pool = new JedisSentinelPool(MASTER_NAME, sentinels, config.build(), 1000,
        "foobared", 2);
    Jedis jedis = pool.getResource();
    jedis.auth("foobared");
    jedis.set("foo", "bar");
    assertEquals("bar", jedis.get("foo"));
    jedis.close();
    pool.close();
    assertTrue(pool.isClosed());
  }

  @Test
  public void returnResourceShouldResetState() {
    var config = JedisPoolConfig.builder();
    config.maxPoolSize(1);
    config.waitingForObjectTimeout(Duration.ZERO);
    try (JedisSentinelPool pool = new JedisSentinelPool(MASTER_NAME, sentinels, config.build(),
        1000, "foobared", 2)) {

      Jedis jedis = null;
      try (Jedis jedis1 = pool.getResource()) {
        jedis = jedis1;
        jedis1.set("hello", "jedis");
        Transaction t = jedis1.multi();
        t.set("hello", "world");
      }

      try (Jedis jedis2 = pool.getResource()) {
        assertSame(jedis, jedis2);
        assertEquals("jedis", jedis2.get("hello"));
      }
    }
  }

  @Test
  public void checkResourceIsCloseable() {
    var config = JedisPoolConfig.builder();
    config.maxPoolSize(1);
    config.waitingForObjectTimeout(Duration.ZERO);
    JedisSentinelPool pool = new JedisSentinelPool(MASTER_NAME, sentinels, config.build(), 1000,
        "foobared", 2);

    Jedis jedis = pool.getResource();
    try {
      jedis.set("hello", "jedis");
    } finally {
      jedis.close();
    }

    Jedis jedis2 = pool.getResource();
    try {
      assertEquals(jedis, jedis2);
    } finally {
      jedis2.close();
    }
  }

  @Test
  public void customClientName() {
    var config = JedisPoolConfig.builder();
    config.maxPoolSize(1);
    config.waitingForObjectTimeout(Duration.ZERO);
    JedisSentinelPool pool = new JedisSentinelPool(MASTER_NAME, sentinels, config.build(), 1000,
        "foobared", 0, "my_shiny_client_name");

    Jedis jedis = pool.getResource();

    try {
      assertEquals("my_shiny_client_name", jedis.clientGetname());
    } finally {
      jedis.close();
      pool.destroy();
    }

    assertTrue(pool.isClosed());
  }
}
