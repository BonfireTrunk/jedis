package redis.clients.jedis.util;

import redis.clients.jedis.annots.Internal;
import today.bonfire.oss.sop.SimpleObjectPoolConfig;
import today.bonfire.oss.sop.SimpleObjectPoolConfig.EvictionPolicy;

/**
 * The counterpart of a plain commons-pool2 {@code GenericObjectPoolConfig}, for the places where
 * upstream Jedis pools without the {@link redis.clients.jedis.ConnectionPoolConfig} defaults: no
 * scheduled eviction and no idle validation. Borrowers still wait at most 10 seconds on an
 * exhausted pool, not forever.
 *
 * @since 8.0.2
 */
@Internal
public final class PlainPoolConfig {

  private PlainPoolConfig() {
  }

  public static SimpleObjectPoolConfig.Builder builder() {
    return SimpleObjectPoolConfig.builder().testWhileIdle(false)
        .evictionPolicy(EvictionPolicy.NONE);
  }

  public static SimpleObjectPoolConfig create() {
    return builder().build();
  }
}
