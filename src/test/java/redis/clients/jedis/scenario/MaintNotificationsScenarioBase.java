package redis.clients.jedis.scenario;

import com.redis.test.fi.Database;
import com.redis.test.fi.StandaloneEffect;
import com.redis.test.fi.TriggerCatalog;

import today.bonfire.oss.sop.SimpleObjectPoolConfig;
import redis.clients.jedis.util.PlainPoolConfig;

import org.junit.jupiter.api.AfterEach;

import redis.clients.jedis.Connection;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.MaintenanceNotificationsConfig;
import redis.clients.jedis.RedisClient;
import redis.clients.jedis.SslOptions;

/**
 * Scenario tests that run one standalone {@link RedisClient} against the deployed database. A
 * subclass deploys the database and opens the client through {@link #openClient}; the client config
 * starts from the database's credentials (plus pinned TLS trust for a TLS database) and the pool
 * from its defaults, and {@link #customiseClientConfig} / {@link #customiseClientPoolConfig} add a
 * subclass's settings. The client is owned here and closed on teardown before the database is
 * released.
 */
abstract class MaintNotificationsScenarioBase extends FaultInjectorScenarioBase {

  /**
   * The full topology-change-standalone catalog, resolved once: effect -> triggers -> requirements.
   */
  static final TriggerCatalog STANDALONE_CATALOG = TriggerCatalog.resolve(faultInjector,
    StandaloneEffect.class);

  RedisClient client;

  /** Opens the client under test and takes ownership of it. */
  RedisClient openClient(Database database, MaintenanceNotificationsConfig maintenance) {
    DefaultJedisClientConfig.Builder config = database.clientConfig();
    SslOptions ssl = pinnedSslOptions(database);
    if (ssl != null) {
      config.sslOptions(ssl);
    }
    customiseClientConfig(config);
    SimpleObjectPoolConfig.Builder poolConfig = PlainPoolConfig.builder();
    customiseClientPoolConfig(poolConfig);
    client = RedisClient.builder().hostAndPort(database.host(), database.port())
        .clientConfig(config.build()).poolConfig(poolConfig.build())
        .maintenanceNotifications(maintenance).build();
    return client;
  }

  /**
   * Adds client settings on top of the credentials and TLS trust. The default adds nothing: the
   * client is what an application gets without tuning.
   */
  void customiseClientConfig(DefaultJedisClientConfig.Builder config) {
  }

  /** Adds pool settings on top of the defaults. */
  void customiseClientPoolConfig(SimpleObjectPoolConfig.Builder poolConfig) {
  }

  @AfterEach
  final void tearDownClient() {
    client = closeIfOpen(client);
  }

  /** Closes the client if one is open; returns null so the field is cleared in one step. */
  static RedisClient closeIfOpen(RedisClient client) {
    if (client != null) {
      client.close();
    }
    return null;
  }
}
