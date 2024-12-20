package redis.clients.jedis;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.exceptions.JedisConnectionException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ConnectionTest {

  private Connection client;

  @AfterEach
  public void tearDown() throws Exception {
    if (client != null) {
      client.close();
    }
  }

  @Test
  public void checkUnknownHost() {
    assertThrows(JedisConnectionException.class, () -> {
      client = new Connection("someunknownhost", Protocol.DEFAULT_PORT);
      client.connect();
    });
  }

  @Test
  public void checkWrongPort() {
    assertThrows(JedisConnectionException.class, () -> {
      client = new Connection(Protocol.DEFAULT_HOST, 55665);
      client.connect();
    });
  }

  @Test
  public void connectIfNotConnectedWhenSettingTimeoutInfinite() {
    client = new Connection("localhost", 6379);
    client.setTimeoutInfinite();
  }

  @Test
  public void checkCloseable() {
    client = new Connection("localhost", 6379);
    client.connect();
    client.close();
  }

  @Test
  public void checkIdentityString() {
    client = new Connection("localhost", 6379);

    String idString = "id: 0x" + Integer.toHexString(client.hashCode()).toUpperCase();

    String identityString = client.toIdentityString();
    assertThat(identityString, Matchers.startsWith("Connection{"));
    assertThat(identityString, Matchers.endsWith("}"));
    assertThat(identityString, Matchers.containsString(idString));

    client.connect();
    identityString = client.toIdentityString();
    assertThat(identityString, Matchers.startsWith("Connection{"));
    assertThat(identityString, Matchers.endsWith("}"));
    assertThat(identityString, Matchers.containsString(idString));
    assertThat(identityString, Matchers.containsString(", L:"));
    assertThat(identityString, Matchers.containsString(" - R:"));

    client.close();
    identityString = client.toIdentityString();
    assertThat(identityString, Matchers.startsWith("Connection{"));
    assertThat(identityString, Matchers.endsWith("}"));
    assertThat(identityString, Matchers.containsString(idString));
    assertThat(identityString, Matchers.containsString(", L:"));
    assertThat(identityString, Matchers.containsString(" ! R:"));
  }
}
