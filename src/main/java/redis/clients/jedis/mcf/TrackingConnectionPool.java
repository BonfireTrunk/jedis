package redis.clients.jedis.mcf;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.*;
import redis.clients.jedis.csc.CacheConnection;
import redis.clients.jedis.exceptions.JedisConnectionException;
import today.bonfire.oss.sop.SimpleObjectPoolConfig;

public class TrackingConnectionPool extends ConnectionPool {

  private static class FailFastConnectionFactory extends ConnectionFactory {
    private volatile boolean failFast = false;
    private final Set<Connection> factoryTrackedObjects = ConcurrentHashMap.newKeySet();

    public FailFastConnectionFactory(ConnectionFactory.Builder factoryBuilder,
        JedisClientConfig clientConfig) {
      super(factoryBuilder
          .connectionBuilder(createCustomConnectionBuilder(factoryBuilder, clientConfig)));
    }

    private static Connection.Builder createCustomConnectionBuilder(
        ConnectionFactory.Builder factoryBuilder, JedisClientConfig clientConfig) {
      Connection.Builder connBuilder = factoryBuilder.getCache() == null ? Connection.builder()
          : CacheConnection.builder(factoryBuilder.getCache());

      return connBuilder.socketFactory(factoryBuilder.getJedisSocketFactory())
          .clientConfig(clientConfig);
    }

    public void forceDisconnect() {
      for (Connection connection : factoryTrackedObjects) {
        try {
          connection.forceDisconnect();
        } catch (Exception e) {
          log.warn("Error while force disconnecting connection: " + connection.toIdentityString(),
            e);
        }
      }
    }

  }

  public static class Builder {
    private HostAndPort hostAndPort;
    private JedisClientConfig clientConfig;
    private SimpleObjectPoolConfig poolConfig;

    public Builder hostAndPort(HostAndPort hostAndPort) {
      this.hostAndPort = hostAndPort;
      return this;
    }

    public Builder clientConfig(JedisClientConfig clientConfig) {
      this.clientConfig = clientConfig;
      return this;
    }

    public Builder poolConfig(SimpleObjectPoolConfig poolConfig) {
      this.poolConfig = poolConfig;
      return this;
    }

    public TrackingConnectionPool build() {
      applyDefaults();
      return new TrackingConnectionPool(this);
    }

    private void applyDefaults() {
      if (clientConfig == null) {
        clientConfig = DefaultJedisClientConfig.builder().build();
      }
      if (poolConfig == null) {
        poolConfig = JedisPoolConfig.builder().build();
      }
    }
  }

  private static final Logger log = LoggerFactory.getLogger(TrackingConnectionPool.class);

  private final HostAndPort hostAndPort;
  private final JedisClientConfig clientConfig;
  private final SimpleObjectPoolConfig poolConfig;
  private final AtomicInteger numWaiters = new AtomicInteger();
  private final Set<Connection> poolTrackedObjects = ConcurrentHashMap.newKeySet();

  public static Builder builder() {
    return new Builder();
  }

  private TrackingConnectionPool(Builder builder) {
    super(createfailFastFactory(builder),
        builder.poolConfig != null ? builder.poolConfig : JedisPoolConfig.builder().build());

    this.hostAndPort = builder.hostAndPort;
    this.clientConfig = builder.clientConfig;
    this.poolConfig = builder.poolConfig;
    this.attachAuthenticationListener(builder.clientConfig.getAuthXManager());
  }

  private static FailFastConnectionFactory createfailFastFactory(Builder poolBuilder) {
    ConnectionFactory.Builder factoryBuilder = ConnectionFactory.builder()
        .clientConfig(poolBuilder.clientConfig).socketFactory(
          new DefaultJedisSocketFactory(poolBuilder.hostAndPort, poolBuilder.clientConfig));
    return new FailFastConnectionFactory(factoryBuilder, poolBuilder.clientConfig);
  }

  public static TrackingConnectionPool from(TrackingConnectionPool existing) {
    return builder().hostAndPort(existing.hostAndPort).clientConfig(existing.clientConfig)
        .poolConfig(existing.poolConfig).build();
  }

  @Override
  public Connection getResource() {
    try {
      numWaiters.incrementAndGet();
      Connection conn = super.getResource();
      poolTrackedObjects.add(conn);
      return conn;
    } catch (Exception e) {
      if (this.isClosed()) {
        throw new JedisConnectionException("Pool is closed!", e);
      }
      throw e;
    } finally {
      numWaiters.decrementAndGet();
    }
  }

  @Override
  public void returnResource(final Connection resource) {
    super.returnResource(resource);
    poolTrackedObjects.remove(resource);
  }

  @Override
  public void returnBrokenResource(final Connection resource) {
    super.returnBrokenResource(resource);
    poolTrackedObjects.remove(resource);
  }

  public void forceDisconnect() {
    this.close();
    ((FailFastConnectionFactory) this.getFactory()).failFast = true;
    int numOfConnected = poolTrackedObjects.size();
    // we need to wait for all waiters to leave before we are done with disconnecting the
    // connections, since a user app thread might be either;
    // - in the middle of a factory call(create|init) and not yet show up in poolTrackedObjects
    // - blocked on an exhausted pool, waiting for resources to return back pool
    while (numWaiters.get() > 0 || numOfConnected > 0) {
      this.clear();
      ((FailFastConnectionFactory) this.getFactory()).forceDisconnect();
      numOfConnected = 0;
      for (Connection connection : poolTrackedObjects) {
        try {
          if (connection.isConnected()) {
            numOfConnected++;
          }
          connection.forceDisconnect();
        } catch (Exception e) {
          log.warn("Error while force disconnecting connection: " + connection.toIdentityString(),
            e);
        }
      }
      try {
        // this is just to yield the thread for a fair share of CPU
        Thread.sleep(1);
      } catch (InterruptedException e) {
      }
    }
    ((FailFastConnectionFactory) this.getFactory()).failFast = false;
  }

  @Override
  public void close() {
    this.destroy();
    this.detachAuthenticationListener();
  }
}
