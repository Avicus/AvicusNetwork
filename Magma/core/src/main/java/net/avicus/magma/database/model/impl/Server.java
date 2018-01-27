package net.avicus.magma.database.model.impl;

import java.util.Date;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.avicus.magma.database.table.impl.ServerCategoryTable;
import net.avicus.quest.annotation.Column;
import net.avicus.quest.annotation.Id;
import net.avicus.quest.model.Model;

@ToString
public class Server extends Model {

  public static Server local;
  @Getter
  @Id
  @Column
  private int id;

  @Getter
  @Column
  private String name;

  @Getter
  @Column
  private String host;

  @Getter
  @Column
  private int port;

  @Getter
  @Setter
  @Column
  private boolean permissible;

  @Getter
  @Column(name = "created_at")
  private Date createdAt;

  @Getter
  @Column(name = "server_group_id")
  private int serverGroupId;

  @Getter
  @Column(name = "server_category_id")
  private int serverCategoryId;

  @Getter
  @Setter
  private String state;
  @Getter
  @Setter
  private String activeMap;
  @Getter
  @Setter
  private int spectators;
  @Getter
  @Setter
  private int players;
  @Getter
  @Setter
  private int maxPlayers;
  private ServerCategory category = null;

  public Server() {

  }

  /**
   * Creates a new server.
   */
  public Server(String name, String host, int port, boolean permissible) {
    this.name = name;
    this.host = host;
    this.port = port;
    this.permissible = permissible;
    this.createdAt = new Date();
  }

  public Optional<String> getPermission() {
    return this.permissible ? Optional.of("hook.server." + this.name.toLowerCase())
        : Optional.empty();
  }

  public Optional<ServerCategory> getCategory(ServerCategoryTable table) {
    if (this.getServerCategoryId() == 0) {
      return Optional.empty();
    }

    if (this.category == null) {
      this.category = table.fromServer(this).orElse(null);
    }

    return Optional.ofNullable(this.category);
  }

  /**
   * If this server is the local (current) server.
   *
   * @return {@code true} if local, {@code false} otherwise
   */
  public boolean isLocal() {
    return this.id == local.id;
  }
}
