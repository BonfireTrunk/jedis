package redis.clients.jedis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import today.bonfire.oss.sop.SimpleObjectPoolConfig;
import redis.clients.jedis.util.PlainPoolConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.util.SafeEncoder;
import redis.clients.jedis.util.server.RespResponse;
import redis.clients.jedis.util.server.TcpMockServer;

/**
 * A connection left in {@code MONITOR} mode must never reach the next pool borrower: the queued
 * lines, which carry other clients' commands, would be read as the replies to its commands.
 */
public class JedisMonitorMockTest {

  private static final String OTHER_CLIENTS_COMMAND = "2.0 \"SET\" \"session\" \"secret\"";
  private static final JedisClientConfig RESP2 = DefaultJedisClientConfig.builder()
      .protocol(RedisProtocol.RESP2).build();

  private TcpMockServer server;
  private HostAndPort hostAndPort;

  @BeforeEach
  public void setUp() throws IOException {
    server = new TcpMockServer();
    server.setCommandHandler((args, clientId) -> {
      switch (SafeEncoder.encode(args.getCommand().getRaw())) {
        case "MONITOR":
          return RespResponse.simpleString("OK") + RespResponse.simpleString("1.0 \"PING\"")
              + RespResponse.simpleString(OTHER_CLIENTS_COMMAND);
        case "GET":
          return RespResponse.bulkString("hello");
        default:
          return null;
      }
    });
    server.start();
    hostAndPort = new HostAndPort("localhost", server.getPort());
  }

  @AfterEach
  public void tearDown() throws IOException {
    server.stop();
  }

  @Test
  public void poolDiscardsConnectionWhenListenerThrows() {
    SimpleObjectPoolConfig poolConfig = PlainPoolConfig.builder().maxPoolSize(1).build();

    try (JedisPool pool = new JedisPool(poolConfig, hostAndPort, RESP2)) {
      assertThrows(IllegalStateException.class, () -> {
        try (Jedis jedis = pool.getResource()) {
          jedis.monitor(new ThrowingMonitor());
        }
      });

      assertEquals(1, pool.stats().destroyed());
      try (Jedis jedis = pool.getResource()) {
        assertEquals("hello", jedis.get("greeting"));
      }
    }
  }

  @Test
  public void nonPooledClientRejectsCommandsAfterListenerThrows() {
    try (Jedis jedis = new Jedis(hostAndPort, RESP2)) {
      assertThrows(IllegalStateException.class, () -> jedis.monitor(new ThrowingMonitor()));

      assertTrue(jedis.isBroken());
      assertThrows(JedisConnectionException.class, () -> jedis.get("greeting"));
    }
  }

  private static class ThrowingMonitor extends JedisMonitor {
    @Override
    public void onCommand(String command) {
      throw new IllegalStateException("stop monitoring");
    }
  }
}
