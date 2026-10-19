package redis.clients.jedis.scenario;

import redis.clients.jedis.ConnectionPoolConfig;
import today.bonfire.oss.sop.SimpleObjectPoolConfig;

import java.time.Duration;

public class RecommendedSettings {

  public static ConnectionPoolConfig poolConfig;

  static {
    poolConfig = new ConnectionPoolConfig();
    // NOTE: as upstream, this local shadows the static field, so these settings are not applied
    // to it
    SimpleObjectPoolConfig poolConfig = ConnectionPoolConfig.builder()
        .maxPoolSize(8).minPoolSize(0).waitingForObjectTimeout(Duration.ofSeconds(1))
        .testWhileIdle(true).durationBetweenEvictionsRuns(Duration.ofSeconds(1)).build();
  }

  public static int MAX_RETRIES = 5;

  public static Duration MAX_TOTAL_RETRIES_DURATION = Duration.ofSeconds(10);

  public static int DEFAULT_TIMEOUT_MS = 5000;



}
