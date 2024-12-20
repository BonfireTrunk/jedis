package redis.clients.jedis.mocked.unified;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UnifiedJedisConnectionManagementCommandsTest extends UnifiedJedisMockedTestBase {

  @Test
  public void testPing() {
    when(commandObjects.ping()).thenReturn(stringCommandObject);
    when(commandExecutor.broadcastCommand(stringCommandObject)).thenReturn("foo");

    String result = jedis.ping();

    assertThat(result, equalTo("foo"));

    verify(commandExecutor).broadcastCommand(stringCommandObject);
    verify(commandObjects).ping();
  }

}
