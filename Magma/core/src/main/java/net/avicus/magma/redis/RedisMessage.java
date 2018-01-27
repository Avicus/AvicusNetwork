package net.avicus.magma.redis;

import com.google.gson.JsonObject;

public interface RedisMessage {

  String channel();

  JsonObject write();
}
