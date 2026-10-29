# Changelog

All notable changes to this fork of [Jedis](https://github.com/redis/jedis) are documented here. Changes that come from upstream are listed in the [Jedis release notes](https://github.com/redis/jedis/releases). The format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

## [8.0.2] - 2026-10-29

### Changed

- Based on Jedis 8.0.2 (the previous fork release, 7.1.0, was based on Jedis 7.1.0).
- Connection pools use simple-object-pool 3.0.0 (was 2.4.0) in place of commons-pool2. Pool defaults and exceptions changed since 7.1.0; see [Connection Pool Changes](docs/migration-guides/v7-to-v8.md#connection-pool-changes) in the 7-to-8 migration guide.
- Built on bonfire-oss-parent 2.1.1 (was 1.1.12). Java 21 or newer is still required.

### Added

- Releases are also published to GitHub Packages. Maven Central remains the place to get this fork from, since GitHub Packages needs a token even for public packages.
