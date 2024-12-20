package redis.clients.jedis.modules.search;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import redis.clients.jedis.RedisProtocol;
import redis.clients.jedis.modules.RedisModuleCommandsTestBase;
import redis.clients.jedis.search.Document;
import redis.clients.jedis.search.FTCreateParams;
import redis.clients.jedis.search.FTSearchParams;
import redis.clients.jedis.search.FieldName;
import redis.clients.jedis.search.IndexDataType;
import redis.clients.jedis.search.SearchResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static redis.clients.jedis.util.AssertUtil.assertOK;

@RunWith(Parameterized.class)
public class JsonSearchWithGsonTest extends RedisModuleCommandsTestBase {

  private static final String index = "gson-index";

  @BeforeAll
  public static void prepare() {
    RedisModuleCommandsTestBase.prepare();
  }
//
//  @AfterClass
//  public static void tearDown() {
////    RedisModuleCommandsTestBase.tearDown();
//  }

  public JsonSearchWithGsonTest(RedisProtocol protocol) {
    super(protocol);
  }

  class Account {

    String name;
    String phone;
    Integer age;

    public Account(String name, String phone, Integer age) {
      this.name = name;
      this.phone = phone;
      this.age = age;
    }
  }

  @Test
  public void returnNullField() {
    Gson nullGson = new GsonBuilder().serializeNulls().create();

    assertOK(client.ftCreate(index, FTCreateParams.createParams().on(IndexDataType.JSON),
        redis.clients.jedis.search.schemafields.TextField.of(FieldName.of("$.name").as("name")),
        redis.clients.jedis.search.schemafields.TextField.of(FieldName.of("$.phone").as("phone")),
        redis.clients.jedis.search.schemafields.NumericField.of(FieldName.of("$.age").as("age"))));

    Account object = new Account("Jane", null, null);
    String jsonString = nullGson.toJson(object);
    client.jsonSet("account:2", jsonString);

    SearchResult sr = client.ftSearch(index, "*",
        FTSearchParams.searchParams().returnFields("name", "phone", "age"));
    assertEquals(1, sr.getTotalResults());
    Document doc = sr.getDocuments().get(0);
    assertEquals("Jane", doc.get("name"));
    assertNull(doc.get("phone"));
    assertNull(doc.get("age"));

    sr = client.ftSearch(index, "*",
        FTSearchParams.searchParams().returnFields("name"));
    assertEquals(1, sr.getTotalResults());
    doc = sr.getDocuments().get(0);
    assertEquals("Jane", doc.get("name"));

    sr = client.ftSearch(index, "*",
        FTSearchParams.searchParams().returnFields("phone"));
    assertEquals(1, sr.getTotalResults());
    doc = sr.getDocuments().get(0);
    assertNull(doc.get("phone"));

    sr = client.ftSearch(index, "*",
        FTSearchParams.searchParams().returnFields("age"));
    assertEquals(1, sr.getTotalResults());
    doc = sr.getDocuments().get(0);
    assertNull(doc.get("age"));
  }
}
