package net.avicus.magma.database.model.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.ToString;
import net.avicus.magma.database.Database;
import net.avicus.quest.annotation.Column;
import net.avicus.quest.annotation.Id;
import net.avicus.quest.model.Model;

@ToString
public class ServerGroup extends Model {

  @Getter
  @Id
  @Column
  private int id;

  @Getter
  @Column
  private String name;

  @Getter
  @Column
  private String slug;

  @Getter
  @Column
  private String description;

  @Getter
  @Column
  private String icon;

  public ServerGroup(String name, String slug, String description, String icon) {
    this.name = name;
    this.slug = slug;
    this.description = description;
    this.icon = icon;
  }

  public ServerGroup() {

  }

  public List<Integer> serverIds(Database database) {
    List<Server> members = database.getServers().findByServerGroup(this.id);
    return members.stream().map(Server::getId).collect(Collectors.toList());
  }

  public boolean isInside(Database database, int serverId) {
    Optional<Server> server = database.getServers().findById(serverId);
    if (!server.isPresent()) {
      return false;
    }

    return server.get().getServerGroupId() == this.id;
  }
}
