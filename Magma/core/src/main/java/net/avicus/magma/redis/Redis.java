package net.avicus.magma.redis;

import com.lambdaworks.redis.ClientOptions;
import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisURI;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.api.sync.RedisCommands;
import com.lambdaworks.redis.pubsub.StatefulRedisPubSubConnection;
import com.lambdaworks.redis.pubsub.api.sync.RedisPubSubCommands;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;
import net.avicus.magma.Enableable;

public class Redis implements Enableable {

  private final RedisClient client;
  private final List<RedisHandler> handlers;
  private Optional<RedisCommands<String, String>> connection;
  private Optional<RedisPubSubCommands<String, String>> subscriber;

  public Redis(RedisURI uri, ClientOptions options) {
    this.client = RedisClient.create(uri);
    this.client.setOptions(options);
    this.connection = Optional.empty();
    this.subscriber = Optional.empty();
    this.handlers = new ArrayList<>();
  }

  public static Builder builder(String host) {
    return new Builder(host);
  }

  public boolean hset(String name, String key, String value) {
    return this.connection.get().hset(name, key, value);
  }

  public String get(String key) {
    return this.connection.get().get(key);
  }

  public boolean set(String key, String value) {
    return this.connection.get().set(key, value) != null;
  }

  public boolean del(String key) {
    return this.connection.get().del(key) != null;
  }

  public Map<String, String> hgetall(String name) {
    return this.connection.get().hgetall(name);
  }

  public void register(RedisHandler handler) {
    this.handlers.add(handler);
    this.subscriber.get().subscribe(handler.channels());
  }

  public Collection<RedisHandler> handlers() {
    return this.handlers;
  }

  public void publish(RedisMessage message) {
    this.connection.get().publish(message.channel(), message.write().toString());
  }

  public void reset() {
    try {
      this.connection.get().reset();
    } catch (CancellationException ignored) {
    }
  }

  @Override
  public void enable() {
    if (this.connection.isPresent()) {
      throw new IllegalStateException("Redis has already been enabled.");
    }

    StatefulRedisPubSubConnection<String, String> pubsub = this.client.connectPubSub();
    this.subscriber = Optional.ofNullable(pubsub.sync());
    this.subscriber.get().addListener(new RedisListener(this));

    StatefulRedisConnection<String, String> connection = this.client.connect();
    this.connection = Optional.ofNullable(connection.sync());
  }

  @Override
  public void disable() {
    if (!this.connection.isPresent()) {
      throw new IllegalStateException("Redis hasn't been enabled.");
    }

    this.connection.get().close();
    this.connection = Optional.empty();
  }

  public static class Builder {

    private final String host;
    private Optional<Integer> timeout = Optional.empty();
    private Optional<String> password = Optional.empty();
    private Optional<Integer> database = Optional.empty();
    private Optional<Boolean> reconnect = Optional.empty();

    Builder(String host) {
      this.host = host;
    }

    public Builder timeout(int timeout) {
      this.timeout = Optional.of(timeout);
      return this;
    }

    public Builder password(String password) {
      this.password = Optional.of(password);
      return this;
    }

    public Builder database(int database) {
      this.database = Optional.of(database);
      return this;
    }

    public Builder reconnect(boolean reconnect) {
      this.reconnect = Optional.of(reconnect);
      return this;
    }

    public Redis build() {
      RedisURI.Builder uriBuilder = RedisURI.Builder.redis(this.host);

      if (this.timeout.isPresent()) {
        uriBuilder.withTimeout(this.timeout.get(), TimeUnit.MILLISECONDS);
      }

      if (this.database.isPresent()) {
        uriBuilder.withDatabase(this.database.get());
      }

      if (this.password.isPresent()) {
        uriBuilder.withPassword(this.password.get());
      }

      ClientOptions.Builder options = new ClientOptions.Builder();

      if (this.reconnect.isPresent()) {
        options.autoReconnect(this.reconnect.get());
      }

      return new Redis(uriBuilder.build(), options.build());
    }
  }
}
