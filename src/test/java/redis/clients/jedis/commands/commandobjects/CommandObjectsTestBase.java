package redis.clients.jedis.commands.commandobjects;

import org.junit.jupiter.api.BeforeEach;
import redis.clients.jedis.CommandObject;
import redis.clients.jedis.CommandObjects;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.EndpointConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.RedisProtocol;
import redis.clients.jedis.args.FlushMode;
import redis.clients.jedis.commands.CommandsTestsParameters;
import redis.clients.jedis.executors.CommandExecutor;
import redis.clients.jedis.executors.DefaultCommandExecutor;
import redis.clients.jedis.providers.ConnectionProvider;
import redis.clients.jedis.providers.PooledConnectionProvider;

import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Base class for CommandObjects tests. The tests are parameterized to run with
 * several versions of RESP. The idea is to test commands at this low level, using
 * a simple executor. Higher level concepts like {@link redis.clients.jedis.UnifiedJedis},
 * or {@link redis.clients.jedis.PipeliningBase} can be tested separately with mocks.
 * <p>
 * This class provides the basic setup, except the {@link HostAndPort} for connecting
 * to a running Redis server. That one is provided by abstract subclasses, depending
 * on if a Redis Stack server is needed, or a standalone suffices.
 */
public abstract class CommandObjectsTestBase {

  /**
   * Input data for parameterized tests. In principle all subclasses of this
   * class should be parameterized tests, to run with several versions of RESP.
   *
   * @see CommandsTestsParameters#respVersions()
   */
  public static Collection<Object[]> data() {
    return CommandsTestsParameters.respVersions();
  }

  /**
   * RESP protocol used in the tests. Injected from subclasses.
   */
  protected RedisProtocol protocol;

  /**
   * Host and port of the Redis server to connect to. Injected from subclasses.
   */
  protected EndpointConfig endpoint;

  /**
   * The {@link CommandObjects} to use for the tests. This is the subject-under-test.
   */
  protected CommandObjects commandObjects;

  /**
   * A {@link CommandExecutor} that can execute commands against the running Redis server.
   * Not exposed to subclasses, which should use a convenience method instead.
   */
  private CommandExecutor commandExecutor;

  public void initCommandObjectsTestBase(RedisProtocol protocol, EndpointConfig endpoint) {
    this.protocol = protocol;
    this.endpoint = endpoint;
    commandObjects = new CommandObjects();
    commandObjects.setProtocol(protocol);
  }

  @BeforeEach
  public void setUp() {
    // Configure a default command executor.
    DefaultJedisClientConfig clientConfig = endpoint.getClientConfigBuilder().protocol(protocol)
        .build();

    ConnectionProvider connectionProvider = new PooledConnectionProvider(endpoint.getHostAndPort(),
        clientConfig);

    commandExecutor = new DefaultCommandExecutor(connectionProvider);

    // Cleanup before each test.
    assertThat(
        commandExecutor.executeCommand(commandObjects.flushAll()),
        equalTo("OK"));

    assertThat(
        commandExecutor.executeCommand(commandObjects.functionFlush(FlushMode.SYNC)),
        equalTo("OK"));
  }

  /**
   * Convenience method for subclasses, for running any {@link CommandObject}.
   */
  protected <T> T exec(CommandObject<T> commandObject) {
    return commandExecutor.executeCommand(commandObject);
  }

}
