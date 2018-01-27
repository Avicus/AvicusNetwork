package net.avicus.magma.database.model.impl;

import java.util.Optional;
import lombok.Getter;
import lombok.ToString;
import net.avicus.quest.annotation.Column;
import net.avicus.quest.annotation.Id;
import net.avicus.quest.model.Model;


@ToString
public class Rank extends Model {

  @Getter
  @Id
  @Column
  private int id;

  @Getter
  @Column
  private String name;

  @Getter
  @Column(name = "mc_perms")
  private String permissionsRaw;

  @Getter
  @Column(name = "ts_perms")
  private String tsPermissionsRaw;

  @Getter
  @Column(name = "special_perms")
  private String categoryPermissionsRaw;

  @Column(name = "mc_prefix")
  private String prefix;
  @Column(name = "mc_suffix")
  private String suffix;
  @Getter
  @Column(name = "is_staff")
  private boolean staff;
  @Getter
  @Column(name = "inheritance_id")
  private int inheritenceId;
  @Getter
  @Column(name = "priority")
  private int priority;

  public Rank() {
  }

  public Rank(String name) {
    this.name = name;
  }

  public Optional<String> getPrefix() {
    if (this.prefix == null || this.prefix.length() == 0) {
      return Optional.empty();
    }
    return Optional.of(this.prefix);
  }

  public Optional<String> getSuffix() {
    if (this.suffix == null || this.suffix.length() == 0) {
      return Optional.empty();
    }
    return Optional.of(this.suffix);
  }
}
