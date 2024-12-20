package redis.clients.jedis.misc;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.EndpointConfig;
import redis.clients.jedis.HostAndPorts;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.exceptions.JedisClusterOperationException;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ClusterInitErrorTest {

  private static final String INIT_NO_ERROR_PROPERTY = "jedis.cluster.initNoError";

  @AfterEach
  public void cleanUp() {
    System.getProperties().remove(INIT_NO_ERROR_PROPERTY);
  }

  @Test
  public void initError() {
    assertThrows(JedisClusterOperationException.class, () -> {
      Assertions.assertNull(System.getProperty(INIT_NO_ERROR_PROPERTY));
      EndpointConfig endpoint = HostAndPorts.getRedisEndpoint("standalone0");
      try (JedisCluster cluster = new JedisCluster(
          Collections.singleton(endpoint.getHostAndPort()),
          endpoint.getClientConfigBuilder().build())) {
        throw new IllegalStateException("should not reach here");
      }
    });
  }

  @Test
  public void initNoError() {
    System.setProperty(INIT_NO_ERROR_PROPERTY, "");
    EndpointConfig endpoint = HostAndPorts.getRedisEndpoint("standalone0");
    try (JedisCluster cluster = new JedisCluster(
        Collections.singleton(endpoint.getHostAndPort()),
        endpoint.getClientConfigBuilder().build())) {
      Assertions.assertThrows(JedisClusterOperationException.class, () -> cluster.get("foo"));
    }
  }
}
