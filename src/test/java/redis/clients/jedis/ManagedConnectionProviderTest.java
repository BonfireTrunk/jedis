package redis.clients.jedis;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.providers.ManagedConnectionProvider;
import redis.clients.jedis.util.IOUtils;

public class ManagedConnectionProviderTest {

  private Connection connection;

  @BeforeEach
  public void setUp() {
    EndpointConfig endpoint = HostAndPorts.getRedisEndpoint("standalone0");
    connection = new Connection(endpoint.getHostAndPort(), endpoint.getClientConfigBuilder().build());
  }

  @AfterEach
  public void tearDown() {
    IOUtils.closeQuietly(connection);
  }

  @Test
  public void test() {
    ManagedConnectionProvider managed = new ManagedConnectionProvider();
    try (UnifiedJedis jedis = new UnifiedJedis(managed)) {
      try {
        jedis.get("any");
        Assertions.fail("Should get NPE.");
      } catch (NullPointerException npe) { }
      managed.setConnection(connection);
      Assertions.assertNull(jedis.get("any"));
    }
  }
}
