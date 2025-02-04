package redis.clients.jedis.util;

import static redis.clients.jedis.util.JedisURIHelper.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.net.URI;
import java.net.URISyntaxException;
import org.junit.Test;
import redis.clients.jedis.RedisProtocol;

public class JedisURIHelperTest {

  @Test
  public void shouldGetUserAndPasswordFromURIWithCredentials() throws URISyntaxException {
    URI uri = new URI("redis://user:password@host:9000/0");
    assertEquals("user", JedisURIHelper.getUser(uri));
    assertEquals("password", JedisURIHelper.getPassword(uri));
  }

  @Test
  public void shouldGetNullUserFromURIWithCredentials() throws URISyntaxException {
    URI uri = new URI("redis://:password@host:9000/0");
    assertNull(JedisURIHelper.getUser(uri));
    assertEquals("password", JedisURIHelper.getPassword(uri));
  }

  @Test
  public void shouldReturnNullIfURIDoesNotHaveCredentials() throws URISyntaxException {
    URI uri = new URI("redis://host:9000/0");
    assertNull(JedisURIHelper.getUser(uri));
    assertNull(JedisURIHelper.getPassword(uri));
  }

  @Test
  public void shouldGetDbFromURIWithCredentials() throws URISyntaxException {
    URI uri = new URI("redis://user:password@host:9000/3");
    assertEquals(3, JedisURIHelper.getDBIndex(uri));
  }

  @Test
  public void shouldGetDbFromURIWithoutCredentials() throws URISyntaxException {
    URI uri = new URI("redis://host:9000/4");
    assertEquals(4, JedisURIHelper.getDBIndex(uri));
  }

  @Test
  public void shouldGetDefaultDbFromURIIfNoDbWasSpecified() throws URISyntaxException {
    URI uri = new URI("redis://host:9000");
    assertEquals(0, JedisURIHelper.getDBIndex(uri));
  }

  @Test
  public void shouldValidateInvalidURIs() throws URISyntaxException {
    assertFalse(JedisURIHelper.isValid(new URI("host:9000")));
    assertFalse(JedisURIHelper.isValid(new URI("user:password@host:9000/0")));
    assertFalse(JedisURIHelper.isValid(new URI("host:9000/0")));
    assertFalse(JedisURIHelper.isValid(new URI("redis://host/0")));
  }

  @Test
  public void shouldGetDefaultProtocolWhenNotDefined() {
    assertNull(getRedisProtocol(URI.create("redis://host:1234")));
    assertNull(getRedisProtocol(URI.create("redis://host:1234/1")));
  }

  @Test
  public void shouldGetProtocolFromDefinition() {
    assertEquals(RedisProtocol.RESP3, getRedisProtocol(URI.create("redis://host:1234?protocol=3")));
    assertEquals(RedisProtocol.RESP3, getRedisProtocol(URI.create("redis://host:1234/?protocol=3")));
    assertEquals(RedisProtocol.RESP3,
      getRedisProtocol(URI.create("redis://host:1234/1?protocol=3")));
    assertEquals(RedisProtocol.RESP3,
      getRedisProtocol(URI.create("redis://host:1234/1/?protocol=3")));
  }
}
