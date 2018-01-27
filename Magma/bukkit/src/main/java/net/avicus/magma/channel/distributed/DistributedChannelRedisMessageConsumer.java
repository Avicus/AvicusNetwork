package net.avicus.magma.channel.distributed;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import net.avicus.magma.Magma;
import net.avicus.magma.channel.report.ReportChannel;
import net.avicus.magma.channel.staff.StaffChannel;
import net.avicus.magma.database.model.impl.Server;
import net.avicus.magma.database.model.impl.ServerCategory;
import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.util.AsyncRedisHandler;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;

public class DistributedChannelRedisMessageConsumer extends
    AsyncRedisHandler<DistributedChannelRedisMessage> {

  static final String ID = "channel";

  DistributedChannelRedisMessageConsumer() {
    super(new String[]{ID});
  }

  @Override
  public DistributedChannelRedisMessage readAsync(JsonObject json) {
    final String channelId = json.get("channel").getAsString();
    final DistributedChannel channel = Magma.get().getChannelManager()
        .getChannel(channelId, DistributedChannel.class);
    final int serverId = json.get("server").getAsInt();
    final Server server = Magma.get().database().getServers().findById(serverId).orElseThrow(
        () -> new IllegalArgumentException("Could not resolve Server for id " + serverId));
    final User source = this.getUser(json);
    final BaseComponent[] components = ComponentSerializer
        .parse(json.get("components").getAsString());
    final Map<String, String> context = this.readMap(json, "context");
    return new DistributedChannelRedisMessage(channel, server, source, components, context);
  }

  private User getUser(JsonObject json) {
    if (!json.has("source")) {
      return null;
    }

    int source = json.get("source").getAsInt();
    return source == User.CONSOLE.getId() ? User.CONSOLE
        : Magma.get().database().getUsers().findById(source).get();
  }

  private Map<String, String> readMap(JsonObject json, String key) {
    if (!json.has(key)) {
      return Collections.emptyMap();
    }

    Map<String, String> map = Maps.newHashMap();
    JsonObject object = json.getAsJsonObject(key);
    for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
      map.put(entry.getKey(), entry.getValue().getAsString());
    }
    return map;
  }

  @Override
  public void handle(DistributedChannelRedisMessage message) {
    if (!message.getServer().isLocal()) {
      Optional<ServerCategory> local = Magma.get().localServer()
          .getCategory(Magma.get().database().getServerCategories());
      Optional<ServerCategory> serverCategory = Magma.get().database().getServerCategories()
          .fromServer(message.getServer());
      if (serverCategory.isPresent() && local.isPresent()) {
        if (!local.get().equals(serverCategory.get())) {
          if (message.getChannel() instanceof ReportChannel) {
            if (!local.get().getOptions().isExternalReports() || !serverCategory.get().getOptions()
                .isPublishReports()) {
              return;
            }
          } else if (message.getChannel() instanceof StaffChannel) {
            if (!local.get().getOptions().isExternalStaff() || !serverCategory.get().getOptions()
                .isPublishStaff()) {
              return;
            }
          }
        }
      }
      message.getChannel()
          .distributedRead(message.getServer(), message.getSource(), message.getComponents(),
              message.getContext());
    }
  }
}
