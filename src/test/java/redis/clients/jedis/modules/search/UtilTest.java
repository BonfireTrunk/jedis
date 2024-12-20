package redis.clients.jedis.modules.search;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.search.RediSearchUtil;
import redis.clients.jedis.search.schemafields.NumericField;
import redis.clients.jedis.search.schemafields.SchemaField;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UtilTest {

  @Test
  public void floatArrayToByteArray() {
    float[] floats = new float[]{0.2f};
    byte[] bytes = RediSearchUtil.toByteArray(floats);
    byte[] expected = new byte[]{-51, -52, 76, 62};
    Assertions.assertArrayEquals(expected, bytes);
  }

  @Test
  public void getSchemaFieldName() {
    SchemaField field = NumericField.of("$.num").as("num");

    assertEquals("$.num", field.getFieldName().getName());
    assertEquals("num", field.getFieldName().getAttribute());

    assertEquals("$.num", field.getName());
  }
}
