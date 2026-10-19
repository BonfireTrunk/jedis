package redis.clients.jedis;

import java.time.Duration;

import today.bonfire.oss.sop.SimpleObjectPoolConfig;

/**
 * Configuration for {@link Connection} pools. {@code new ConnectionPoolConfig()} and
 * {@link #builder()} both start from the Jedis defaults: every 30 seconds all idle connections are
 * validated, and those idle for more than 60 seconds are evicted. Connections are not validated on
 * borrow.
 * <p>
 * An exhausted pool makes borrowers wait at most 10 seconds. Set {@code waitingForObjectTimeout}
 * to a negative duration to wait forever (commons-pool2's {@code maxWait=-1}), or to
 * {@link Duration#ZERO} to fail immediately (commons-pool2's {@code blockWhenExhausted=false}).
 */
public class ConnectionPoolConfig extends SimpleObjectPoolConfig {

  public ConnectionPoolConfig() {
    // defaults to make your life with connection pool easier :)
    super(builder().prepareAndCheck());
  }

  public ConnectionPoolConfig(SimpleObjectPoolConfig.Builder config) {
    super(config);
  }

  /**
   * @return a builder preset with the Jedis defaults
   */
  public static Builder builder() {
    return new Builder();
  }

  public static class Builder extends SimpleObjectPoolConfig.Builder {

    public Builder() {
      applyDefaults();
    }

    /**
     * Applies the Jedis defaults again.
     * @return this builder
     * @deprecated {@link ConnectionPoolConfig#builder()} already starts from the Jedis defaults.
     */
    @Deprecated
    public Builder defaultConfig() {
      applyDefaults();
      return this;
    }

    private void applyDefaults() {
      // numValidationsPerEvictionRun stays unset, so every idle connection is examined on each run
      testWhileIdle(true);
      objEvictionTimeout(Duration.ofMillis(60000));
      durationBetweenEvictionsRuns(Duration.ofMillis(30000));
    }

    @Override
    protected Builder prepareAndCheck() {
      super.prepareAndCheck();
      return this;
    }

    @Override
    public ConnectionPoolConfig build() {
      return new ConnectionPoolConfig(prepareAndCheck());
    }
  }
}
