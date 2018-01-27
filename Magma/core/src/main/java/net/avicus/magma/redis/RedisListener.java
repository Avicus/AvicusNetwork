package net.avicus.magma.redis;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lambdaworks.redis.pubsub.RedisPubSubListener;

public class RedisListener implements RedisPubSubListener<String, String> {

  private static final JsonParser parser = new JsonParser();
  private final Redis redis;

  public RedisListener(Redis redis) {
    this.redis = redis;
  }

  @Override
  public void message(String channel, String body) {
    JsonObject json = parser.parse(body).getAsJsonObject();

    this.redis.handlers()
        .stream()
        .filter(handler -> handler.matches(channel))
        .forEach(handler -> handler.handle(json));
  }

  @Override
  public void message(String s, String k1, String s2) {

  }

  @Override
  public void subscribed(String s, long l) {

  }

  @Override
  public void psubscribed(String s, long l) {

  }

  @Override
  public void unsubscribed(String s, long l) {

  }

  @Override
  public void punsubscribed(String s, long l) {

  }
}
