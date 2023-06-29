package com.starter.common;

import java.io.File;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.shaded.org.apache.commons.lang3.tuple.Pair;


public class DockerComposeExtension implements BeforeAllCallback, AfterAllCallback {
  private static final String KAFKA_SERVICE = "kafka_1";
  private static final int KAFKA_PORT = 29092;
  private static final String REDIS_SERVICE = "redis_1";
  private static final int REDIS_PORT = 6379;
  public static DockerComposeContainer environment;

  @Override
  public void afterAll(ExtensionContext context) throws Exception {
    environment.stop();
  }

  @Override
  public void beforeAll(ExtensionContext context) throws Exception {
    environment =
        new DockerComposeContainer(new File("docker-compose.yml")).
            withExposedService(REDIS_SERVICE, REDIS_PORT, Wait.forListeningPort()).
            withExposedService(KAFKA_SERVICE, KAFKA_PORT, Wait.forListeningPort());

    environment.start();
  }

  public static DockerComposeContainer getEnvironment() {
    return environment;
  }

  public static Pair<String, Integer> redisHostPort() {
    return Pair.of(environment.getServiceHost(REDIS_SERVICE, REDIS_PORT),
        environment.getServicePort(REDIS_SERVICE, REDIS_PORT));
  }

  public static String kafkaHostPort() {
    return environment.getServiceHost(KAFKA_SERVICE, KAFKA_PORT) + ":" + environment.getServicePort(KAFKA_SERVICE,
        KAFKA_PORT);
  }
}
