package redis.clients.jedis.modules.json;

import org.junit.jupiter.api.Test;
import redis.clients.jedis.json.Path2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Path2Test {

  @Test
  public void _null() {
    assertThrows(NullPointerException.class, () ->
        Path2.of(null));
  }

  @Test
  public void empty() {
    assertThrows(IllegalArgumentException.class, () ->
        Path2.of(""));
  }

  @Test
  public void root() {
    assertEquals("$", Path2.ROOT_PATH.toString());
    assertEquals(Path2.ROOT_PATH, new Path2("$"));
    assertEquals(Path2.ROOT_PATH, Path2.of("$"));
  }

  @Test
  public void test() {
    assertEquals("$.a.b", Path2.of("$.a.b").toString());
    assertEquals("$.a.b", new Path2("$.a.b").toString());
    assertEquals("$.a.b", Path2.of(".a.b").toString());
    assertEquals("$.a.b", new Path2(".a.b").toString());
    assertEquals("$.a.b", Path2.of("a.b").toString());
    assertEquals("$.a.b", new Path2("a.b").toString());
  }

  @Test
  public void equals() {
    assertTrue(new Path2("a.b").equals(Path2.of(".a.b")));
    assertTrue(Path2.of("a.b").equals(new Path2(".a.b")));
  }
}
