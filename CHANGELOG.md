# Changelog

All notable changes to this fork of [Jedis](https://github.com/redis/jedis) are documented here. Changes that come from upstream are listed in the [Jedis release notes](https://github.com/redis/jedis/releases). The format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

## [8.1.0] - 2026-11-27

### Changed

- Based on Jedis 8.1.0 (was 8.0.2). Upstream's changes, including smart client handoffs (maintenance notifications), are listed in the [Jedis 8.1.0 release notes](docs/release-notes/8.1.0.md).
- Connections retired by a maintenance notification are evicted through simple-object-pool's `shouldEvict` hook, which takes the place of upstream's commons-pool2 eviction policy. simple-object-pool stays at 3.0.0.
- A retired or broken connection returned to the pool is destroyed without opening a replacement; the next borrow opens one. Upstream's commons-pool2 opens the replacement at once, so after a `MOVING` notification the fork's first command pays the connect.
- With `minPoolSize` above 0, the eviction pass a maintenance handoff or a token renewal triggers also refills the pool to `minPoolSize` straight away, on the thread that runs the pass. commons-pool2 leaves the refill to its next scheduled evictor run.
- Pools created without a pool config no longer run scheduled idle validation or evict idle connections after 5 minutes, matching upstream. This covers cluster node pools (for example `new ClusterConnectionProvider(nodes, clientConfig)`), `new ConnectionPool(hostAndPort, clientConfig)`, `new PooledConnectionProvider(hostAndPort)` and the constructors that take only a factory. Clients built with a builder (`RedisClient.builder()`, `RedisClusterClient.builder()` and so on) are unaffected, since the builder always passes a pool config.
- Built on bonfire-oss-parent 2.2.1 (was 2.1.1).

### Added

- The jar contains the license as `META-INF/LICENSE`, as the parent POM now does for every library.

## [8.0.2] - 2026-10-29

### Changed

- Based on Jedis 8.0.2 (the previous fork release, 7.1.0, was based on Jedis 7.1.0).
- Connection pools use simple-object-pool 3.0.0 (was 2.4.0) in place of commons-pool2. Pool defaults and exceptions changed since 7.1.0; see [Connection Pool Changes](docs/migration-guides/v7-to-v8.md#connection-pool-changes) in the 7-to-8 migration guide.
- Built on bonfire-oss-parent 2.1.1 (was 1.1.12). Java 21 or newer is still required.

### Added

- Releases are also published to GitHub Packages. Maven Central remains the place to get this fork from, since GitHub Packages needs a token even for public packages.
