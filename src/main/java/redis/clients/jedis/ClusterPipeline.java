package redis.clients.jedis;

import redis.clients.jedis.providers.ClusterConnectionProvider;
import redis.clients.jedis.util.IOUtils;

import java.time.Duration;
import java.util.Set;

public class ClusterPipeline extends MultiNodePipelineBase {

  private final ClusterConnectionProvider provider;
  private       AutoCloseable             closeable = null;

  public ClusterPipeline(Set<HostAndPort> clusterNodes, JedisClientConfig clientConfig) {
    this(new ClusterConnectionProvider(clusterNodes, clientConfig),
         createClusterCommandObjects(clientConfig.getRedisProtocol()));
    this.closeable = this.provider;
  }

  public ClusterPipeline(Set<HostAndPort> clusterNodes, JedisClientConfig clientConfig,
                         JedisPoolConfig poolConfig) {
    this(new ClusterConnectionProvider(clusterNodes, clientConfig, poolConfig),
         createClusterCommandObjects(clientConfig.getRedisProtocol()));
    this.closeable = this.provider;
  }

  public ClusterPipeline(Set<HostAndPort> clusterNodes, JedisClientConfig clientConfig,
                         JedisPoolConfig poolConfig, Duration topologyRefreshPeriod) {
    this(new ClusterConnectionProvider(clusterNodes, clientConfig, poolConfig, topologyRefreshPeriod),
         createClusterCommandObjects(clientConfig.getRedisProtocol()));
    this.closeable = this.provider;
  }

  public ClusterPipeline(ClusterConnectionProvider provider) {
    this(provider, new ClusterCommandObjects());
  }

  public ClusterPipeline(ClusterConnectionProvider provider, ClusterCommandObjects commandObjects) {
    super(commandObjects);
    this.provider = provider;
  }

  private static ClusterCommandObjects createClusterCommandObjects(RedisProtocol protocol) {
    ClusterCommandObjects cco = new ClusterCommandObjects();
    if (protocol == RedisProtocol.RESP3) cco.setProtocol(protocol);
    return cco;
  }

  /**
   * This method must be called after constructor, if graph commands are going to be used.
   */
  public void prepareGraphCommands() {
    super.prepareGraphCommands(provider);
  }

  @Override
  protected HostAndPort getNodeKey(CommandArguments args) {
    return provider.getNode(((ClusterCommandArguments) args).getCommandHashSlot());
  }

  @Override
  protected Connection getConnection(HostAndPort nodeKey) {
    return provider.getConnection(nodeKey);
  }

  @Override
  public void close() {
    try {
      super.close();
    } finally {
      IOUtils.closeQuietly(closeable);
    }
  }

  public Response<Long> spublish(String channel, String message) {
    return appendCommand(commandObjects.spublish(channel, message));
  }

  public Response<Long> spublish(byte[] channel, byte[] message) {
    return appendCommand(commandObjects.spublish(channel, message));
  }
}
