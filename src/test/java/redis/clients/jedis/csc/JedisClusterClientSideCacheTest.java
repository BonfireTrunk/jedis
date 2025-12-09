package redis.clients.jedis.csc;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.RegisterExtension;
import redis.clients.jedis.Connection;
import redis.clients.jedis.ConnectionPoolConfig;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.HostAndPorts;
import redis.clients.jedis.JedisClientConfig;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.util.RedisVersionCondition;
import io.redis.test.annotations.SinceRedisVersion;
import today.bonfire.oss.sop.SimpleObjectPoolConfig;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

@SinceRedisVersion(value = "7.4.0", message = "Jedis client-side caching is only supported with Redis 7.4 or later.")
@Tag("integration")
public class JedisClusterClientSideCacheTest extends UnifiedJedisClientSideCacheTestBase {

  private static final Set<HostAndPort> hnp = new HashSet<>(HostAndPorts.getStableClusterServers());

  private static final Supplier<JedisClientConfig> clientConfig
      = () -> DefaultJedisClientConfig.builder().resp3().password("cluster").build();

  private static final Supplier<SimpleObjectPoolConfig> singleConnectionPoolConfig
      = () -> {
    var poolConfig = ConnectionPoolConfig.builder();
    poolConfig.maxPoolSize(1);
    return poolConfig.build();
  };

  @RegisterExtension
  public static RedisVersionCondition versionCondition = new RedisVersionCondition(hnp.iterator().next(), clientConfig.get());

  @Override
  protected JedisCluster createRegularJedis() {
    return new JedisCluster(hnp, clientConfig.get());
  }

  @Override
  protected JedisCluster createCachedJedis(CacheConfig cacheConfig) {
    return new JedisCluster(hnp, clientConfig.get(), cacheConfig);
  }

}
