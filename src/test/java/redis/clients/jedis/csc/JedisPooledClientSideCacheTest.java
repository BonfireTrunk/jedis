package redis.clients.jedis.csc;

import org.junit.jupiter.api.BeforeAll;
import redis.clients.jedis.HostAndPorts;

public class JedisPooledClientSideCacheTest extends JedisPooledClientSideCacheTestBase {

  @BeforeAll
  public static void prepare() {
    endpoint = HostAndPorts.getRedisEndpoint("standalone1");
  }

}
