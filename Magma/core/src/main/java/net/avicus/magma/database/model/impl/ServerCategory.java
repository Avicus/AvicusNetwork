package net.avicus.magma.database.model.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.Getter;
import net.avicus.magma.database.Database;
import net.avicus.quest.annotation.Column;
import net.avicus.quest.annotation.Id;
import net.avicus.quest.model.Model;

public class ServerCategory extends Model {

  @Getter
  @Id
  @Column
  private int id;

  @Getter
  @Column(name = "name")
  private String name;

  @Column(name = "tracking_options")
  private String trakingOptions;

  @Column(name = "communication_options")
  private String communicationOptions;

  @Column(name = "infraction_options")
  private String infractionOptions;
  private ServerOptions options = null;

  public List<Integer> serverIds(Database database) {
    List<Server> members = database.getServers().findByServerCategory(this.id);
    return members.stream().map(Server::getId).collect(Collectors.toList());
  }

  public boolean isInside(Database database, int serverId) {
    Optional<Server> server = database.getServers().findById(serverId);
    if (!server.isPresent()) {
      return false;
    }

    return server.get().getServerCategoryId() == this.id;
  }

  public ServerOptions getOptions() {
    if (this.options == null) {
      this.options = ServerOptions.create(this);
    }
    return this.options;
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof ServerCategory && this.id == ((ServerCategory) obj).getId();
  }

  @Data
  public static class ServerOptions {

    private final boolean externalPunishments;
    private final boolean externalStaff;
    private final boolean externalPMs;
    private final boolean externalReports;
    private final boolean externalFriends;
    private final boolean publishStaff;
    private final boolean publishPMs;
    private final boolean publishPunishments;
    private final boolean publishReports;
    private final boolean publishFriends;
    private final String remotePrefix;

    private final boolean recordWarns;
    private final boolean recordKicks;
    private final boolean recordBans;
    private final boolean recordTempBans;
    private final boolean ignoreWarns;
    private final boolean ignoreKicks;
    private final boolean ignoreBans;
    private final boolean ignoreTempBans;

    public static ServerOptions create(ServerCategory category) {
      // Defaults
      boolean externalPunishments = false;
      boolean externalStaff = false;
      boolean externalPMs = false;
      boolean externalReports = false;
      boolean externalFriends = false;
      boolean publishStaff = false;
      boolean publishPMs = false;
      boolean publishPunishments = false;
      boolean publishReports = false;
      boolean publishFriends = false;
      String remotePrefix = "";

      boolean recordWarns = false;
      boolean recordKicks = false;
      boolean recordBans = false;
      boolean recordTempBans = false;
      boolean ignoreWarns = false;
      boolean ignoreKicks = false;
      boolean ignoreBans = false;
      boolean ignoreTempBans = false;

      if (category.communicationOptions != null && !category.communicationOptions.isEmpty()) {
        JsonElement comm = new JsonParser().parse(category.communicationOptions);
        if (!comm.isJsonNull()) {
          JsonObject communication = comm.getAsJsonObject();
          if (communication.has("publish") && communication.get("publish").isJsonObject()) {
            JsonObject publish = communication.getAsJsonObject("publish");
            if (publish.has("staff_chat")) {
              publishStaff = publish.get("staff_chat").getAsBoolean();
            }
            if (publish.has("punishments")) {
              publishPunishments = publish.get("punishments").getAsBoolean();
            }
            if (publish.has("reports")) {
              publishReports = publish.get("reports").getAsBoolean();
            }
            if (publish.has("pms")) {
              publishPMs = publish.get("pms").getAsBoolean();
            }
            if (publish.has("friends")) {
              publishFriends = publish.get("friends").getAsBoolean();
            }
          }
          if (communication.has("external") && communication.get("external").isJsonObject()) {
            JsonObject external = communication.getAsJsonObject("external");
            if (external.has("staff_chat")) {
              externalStaff = external.get("staff_chat").getAsBoolean();
            }
            if (external.has("punishments")) {
              externalPunishments = external.get("punishments").getAsBoolean();
            }
            if (external.has("reports")) {
              externalReports = external.get("reports").getAsBoolean();
            }
            if (external.has("pms")) {
              externalPMs = external.get("pms").getAsBoolean();
            }
            if (external.has("friends")) {
              externalFriends = external.get("friends").getAsBoolean();
            }
          }
          if (communication.has("prefix")) {
            remotePrefix = communication.get("prefix").getAsString();
          }
        }
      }

      if (category.infractionOptions != null && !category.infractionOptions.isEmpty()) {
        JsonElement inf = new JsonParser().parse(category.infractionOptions);
        if (!inf.isJsonNull()) {
          JsonObject infraction = inf.getAsJsonObject();
          if (infraction.has("publish") && infraction.get("publish").isJsonObject()) {
            JsonObject publish = infraction.getAsJsonObject("publish");
            if (publish.has("warns")) {
              recordWarns = publish.get("warns").getAsBoolean();
            }
            if (publish.has("kicks")) {
              recordKicks = publish.get("kicks").getAsBoolean();
            }
            if (publish.has("bans")) {
              recordBans = publish.get("bans").getAsBoolean();
            }
            if (publish.has("temp_bans")) {
              recordTempBans = publish.get("temp_bans").getAsBoolean();
            }
          }
          if (infraction.has("ignore") && infraction.get("ignore").isJsonObject()) {
            JsonObject ignore = infraction.getAsJsonObject("ignore");
            if (ignore.has("warns")) {
              ignoreWarns = ignore.get("warns").getAsBoolean();
            }
            if (ignore.has("kicks")) {
              ignoreKicks = ignore.get("kicks").getAsBoolean();
            }
            if (ignore.has("bans")) {
              ignoreBans = ignore.get("bans").getAsBoolean();
            }
            if (ignore.has("temp_bans")) {
              ignoreTempBans = ignore.get("temp_bans").getAsBoolean();
            }
          }
        }
      }

      return new ServerOptions(
          externalPunishments,
          externalStaff,
          externalPMs,
          externalReports,
          externalFriends,
          publishStaff,
          publishPMs,
          publishPunishments,
          publishReports,
          publishFriends,
          remotePrefix,
          recordWarns,
          recordKicks,
          recordBans,
          recordTempBans,
          ignoreWarns,
          ignoreKicks,
          ignoreBans,
          ignoreTempBans
      );
    }
  }
}
