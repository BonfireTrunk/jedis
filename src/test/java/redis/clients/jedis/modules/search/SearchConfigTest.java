package redis.clients.jedis.modules.search;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import redis.clients.jedis.RedisProtocol;
import redis.clients.jedis.modules.RedisModuleCommandsTestBase;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RunWith(Parameterized.class)
public class SearchConfigTest extends RedisModuleCommandsTestBase {

  @BeforeAll
  public static void prepare() {
    RedisModuleCommandsTestBase.prepare();
  }
//
//  @AfterClass
//  public static void tearDown() {
////    RedisModuleCommandsTestBase.tearDown();
//  }

  public SearchConfigTest(RedisProtocol protocol) {
    super(protocol);
  }

  @Disabled
  @Test
  public void config() {
    Map<String, Object> map = client.ftConfigGet("TIMEOUT");
    assertEquals(1, map.size());
    String value = (String) map.get("TIMEOUT");
    try {
      assertNotNull(value);
    } finally {
      assertEquals("OK", client.ftConfigSet("timeout", value));
    }
  }

  @Disabled
  @Test
  public void configOnTimeout() {
    // confirm default
    assertEquals(Collections.singletonMap("ON_TIMEOUT", "return"), client.ftConfigGet("ON_TIMEOUT"));

    try {
      assertEquals("OK", client.ftConfigSet("ON_TIMEOUT", "fail"));
      assertEquals(Collections.singletonMap("ON_TIMEOUT", "fail"), client.ftConfigGet("ON_TIMEOUT"));

    } finally {
      // restore to default
      assertEquals("OK", client.ftConfigSet("ON_TIMEOUT", "return"));
    }
  }

  @Disabled
  @Test
  public void dialectConfig() {
    // confirm default
    assertEquals(Collections.singletonMap("DEFAULT_DIALECT", "1"), client.ftConfigGet("DEFAULT_DIALECT"));

    try {
      assertEquals("OK", client.ftConfigSet("DEFAULT_DIALECT", "2"));
      assertEquals(Collections.singletonMap("DEFAULT_DIALECT", "2"), client.ftConfigGet("DEFAULT_DIALECT"));

    } finally {
      // restore to default
      assertEquals("OK", client.ftConfigSet("DEFAULT_DIALECT", "1"));
    }
  }
}
