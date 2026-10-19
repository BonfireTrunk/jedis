package redis.clients.jedis;

import today.bonfire.oss.sop.PooledObjectFactory;
import today.bonfire.oss.sop.SimpleObjectPoolConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class JedisPoolUnitTest {

  private JedisPool pool;
  private Jedis mockJedis;

  @BeforeEach
  public void setUp() throws Exception {
    mockJedis = mock(Jedis.class);
    PooledObjectFactory<Jedis> mockFactory = mock(PooledObjectFactory.class);

    when(mockFactory.createObject()).thenReturn(mockJedis);

    SimpleObjectPoolConfig config = SimpleObjectPoolConfig.builder().maxPoolSize(1).build();
    pool = spy(new JedisPool(config, mockFactory));

  }

  @AfterEach
  public void tearDown() {
    if (pool != null && !pool.isClosed()) {
      pool.close();
    }
  }

  @Test
  public void testWithResourceClosesConnection() {
    pool.withResource(jedis -> assertEquals(mockJedis, jedis));

    verify(mockJedis, times(1)).close();
  }

  @Test
  public void testWithResourceGetClosesConnection() {
    String result = pool.withResourceGet(jedis -> "test-result");

    verify(mockJedis, times(1)).close();
  }

  @Test
  public void testWithResourceGetReturnsResult() {
    when(mockJedis.get(eq("test-key"))).thenReturn("test-result");
    String result = pool.withResourceGet(jedis -> jedis.get("test-key"));

    verify(mockJedis, times(1)).close();
  }

}
