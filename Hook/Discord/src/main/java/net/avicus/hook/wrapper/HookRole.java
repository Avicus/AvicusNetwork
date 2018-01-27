package net.avicus.hook.wrapper;

import lombok.Getter;
import net.avicus.magma.database.model.impl.Rank;
import net.dv8tion.jda.core.entities.Role;

/**
 * Wrapper class that represents a discord {@link net.dv8tion.jda.core.entities.Role}
 * that is connected to a {@link Rank}.
 */
public class HookRole {

  @Getter
  private final Rank rank;
  @Getter
  private final Role role;

  public HookRole(Rank rank, Role role) {
    this.rank = rank;
    this.role = role;
  }
}
