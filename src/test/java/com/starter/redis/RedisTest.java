package com.starter.redis;

import com.starter.common.DockerComposeExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.apache.commons.lang3.tuple.Pair;
import redis.clients.jedis.Jedis;

@Testcontainers
@ExtendWith(DockerComposeExtension.class)
public class RedisTest {
  public static DockerComposeContainer environment;
  private static Jedis jedis;

  @BeforeAll
  public static void setUp() {
    environment = DockerComposeExtension.getEnvironment();
    Pair<String, Integer> redisHostPort = DockerComposeExtension.redisHostPort();
    jedis = new Jedis(redisHostPort.getLeft(), redisHostPort.getRight());
  }

  @Test
  public void test1() {
    jedis.set("key1", "value1");
    jedis.set("key2", "value2");

    Assertions.assertEquals("value1", jedis.get("key1"));
    Assertions.assertEquals("value2", jedis.get("key2"));

    jedis.del("key1");

    Assertions.assertNull(jedis.get("key1"));
  }
}
