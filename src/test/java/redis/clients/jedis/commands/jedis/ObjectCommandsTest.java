package redis.clients.jedis.commands.jedis;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import redis.clients.jedis.EndpointConfig;
import redis.clients.jedis.HostAndPorts;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.RedisProtocol;
import redis.clients.jedis.exceptions.JedisDataException;
import redis.clients.jedis.util.SafeEncoder;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(Parameterized.class)
public class ObjectCommandsTest extends JedisCommandsTestBase {

  private final String key = "mylist";
  private final byte[] binaryKey = SafeEncoder.encode(key);
  private final EndpointConfig lfuEndpoint = HostAndPorts.getRedisEndpoint("standalone7-with-lfu-policy");
  private Jedis lfuJedis;

  public ObjectCommandsTest(RedisProtocol protocol) {
    super(protocol);
  }

  @BeforeEach
  @Override
  public void setUp() throws Exception {
    super.setUp();

    lfuJedis = new Jedis(lfuEndpoint.getHostAndPort(),
        lfuEndpoint.getClientConfigBuilder().build());
    lfuJedis.connect();
    lfuJedis.flushAll();
  }

  @AfterEach
  @Override
  public void tearDown() throws Exception {
    lfuJedis.disconnect();
    super.tearDown();
  }

  @Test
  public void objectRefcount() {
    jedis.lpush(key, "hello world");
    Long refcount = jedis.objectRefcount(key);
    assertEquals(Long.valueOf(1), refcount);

    // Binary
    refcount = jedis.objectRefcount(binaryKey);
    assertEquals(Long.valueOf(1), refcount);

  }

  @Test
  public void objectEncodingString() {
    jedis.set(key, "hello world");
    assertThat(jedis.objectEncoding(key), containsString("str"));

    // Binary
    assertThat(SafeEncoder.encode(jedis.objectEncoding(binaryKey)), containsString("str"));
  }

  @Test
  public void objectEncodingList() {
    jedis.lpush(key, "hello world");
    assertThat(jedis.objectEncoding(key), containsString("list"));

    // Binary
    assertThat(SafeEncoder.encode(jedis.objectEncoding(binaryKey)), containsString("list"));
  }

  @Test
  public void objectIdletime() throws InterruptedException {
    jedis.lpush(key, "hello world");

    Long time = jedis.objectIdletime(key);
    assertEquals(Long.valueOf(0), time);

    // Binary
    time = jedis.objectIdletime(binaryKey);
    assertEquals(Long.valueOf(0), time);
  }

  @Test
  public void objectHelp() {
    // String
    List<String> helpTexts = jedis.objectHelp();
    Assertions.assertNotNull(helpTexts);

    // Binary
    List<byte[]> helpBinaryTexts = jedis.objectHelpBinary();
    Assertions.assertNotNull(helpBinaryTexts);
  }

  @Test
  public void objectFreq() {
    lfuJedis.set(key, "test1");
    lfuJedis.get(key);
    // String
    assertThat(lfuJedis.objectFreq(key), greaterThanOrEqualTo(1L));
    // Binary
    assertThat(lfuJedis.objectFreq(binaryKey), greaterThanOrEqualTo(1L));

    Assertions.assertNull(lfuJedis.objectFreq("no_such_key"));

    jedis.set(key, "test2");
    Assertions.assertThrows(JedisDataException.class, () -> jedis.objectFreq(key), "Freq is only allowed with LFU policy");
  }
}
