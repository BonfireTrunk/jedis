// package redis.clients.jedis.benchmark;
//
// import java.io.IOException;
// import java.net.UnknownHostException;
// import java.util.ArrayList;
// import java.util.Calendar;
// import java.util.Collection;
// import java.util.List;
//
// import redis.clients.jedis.HostAndPort;
// import redis.clients.jedis.HostAndPorts;
// import redis.clients.jedis.Jedis;
// import redis.clients.jedis.JedisShardInfo;
// import redis.clients.jedis.ShardedJedis;
//
// public class ShardedBenchmark {
//
// private static HostAndPort hnp1 = HostAndPorts.getRedisServers().get(0);
// private static HostAndPort hnp2 = HostAndPorts.getRedisServers().get(1);
// private static final int TOTAL_OPERATIONS = 100000;
//
// public static void main(String[] args) throws UnknownHostException, IOException {
// List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>();
// JedisShardInfo shard = new JedisShardInfo(hnp1);
// shard.setPassword("foobared");
// shards.add(shard);
// shard = new JedisShardInfo(hnp2);
// shard.setPassword("foobared");
// shards.add(shard);
// ShardedJedis jedis = new ShardedJedis(shards);
// Collection<Jedis> allShards = jedis.getAllShards();
// for (Jedis j : allShards) {
// j.flushAll();
// }
//
// long begin = Calendar.getInstance().getTimeInMillis();
//
// for (int n = 0; n <= TOTAL_OPERATIONS; n++) {
// String key = "foo" + n;
// jedis.set(key, "bar" + n);
// jedis.get(key);
// }
//
// long elapsed = Calendar.getInstance().getTimeInMillis() - begin;
//
// jedis.disconnect();
//
// System.out.println(((1000 * 2 * TOTAL_OPERATIONS) / elapsed) + " ops");
// }
// }
