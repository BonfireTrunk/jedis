package redis.clients.jedis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import redis.clients.jedis.util.PlainPoolConfig;
import today.bonfire.oss.sop.SimpleObjectPoolConfig;
import today.bonfire.oss.sop.SimpleObjectPoolConfig.EvictionPolicy;

public class ConnectionPoolConfigTest {

  static Stream<SimpleObjectPoolConfig> jedisDefaults() {
    return Stream.of(new ConnectionPoolConfig(), ConnectionPoolConfig.builder().build(),
      new JedisPoolConfig(), JedisPoolConfig.builder().build());
  }

  @ParameterizedTest
  @MethodSource("jedisDefaults")
  public void jedisDefaultsMatchUpstream(SimpleObjectPoolConfig config) {
    assertEquals(8, config.maxPoolSize());
    assertEquals(0, config.minPoolSize());
    assertTrue(config.lifo());
    assertFalse(config.fairness());
    assertFalse(config.testOnCreate());
    assertFalse(config.testOnBorrow());
    assertFalse(config.testOnReturn());
    assertTrue(config.testWhileIdle());
    assertEquals(Duration.ofSeconds(60), config.objEvictionTimeout());
    assertEquals(Duration.ofSeconds(30), config.durationBetweenEvictionsRuns());
    assertEquals(config.maxPoolSize(), config.numValidationsPerEvictionRun());
    assertEquals(Duration.ofSeconds(10), config.waitingForObjectTimeout());
  }

  @Test
  public void builderSettingsOverrideDefaults() {
    SimpleObjectPoolConfig config = ConnectionPoolConfig.builder().maxPoolSize(16)
        .testOnBorrow(true).waitingForObjectTimeout(Duration.ofMillis(-1)).build();

    assertTrue(config instanceof ConnectionPoolConfig);
    assertEquals(16, config.maxPoolSize());
    assertEquals(16, config.numValidationsPerEvictionRun());
    assertTrue(config.testOnBorrow());
    assertTrue(config.waitingForObjectTimeout().isNegative());
    assertEquals(Duration.ofSeconds(60), config.objEvictionTimeout());
  }

  @Test
  public void plainConfigHasNoIdleMaintenance() {
    SimpleObjectPoolConfig config = PlainPoolConfig.create();

    assertEquals(8, config.maxPoolSize());
    assertFalse(config.testWhileIdle());
    assertFalse(config.testOnBorrow());
    assertEquals(EvictionPolicy.NONE, config.evictionPolicy());
    assertEquals(Duration.ofSeconds(10), config.waitingForObjectTimeout());
  }
}
