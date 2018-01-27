package net.avicus.hook.wrapper;

import com.github.theholywaffle.teamspeak3.api.wrapper.ServerGroup;
import lombok.Getter;
import net.avicus.magma.database.model.impl.Rank;

/**
 * Wrapper class that represents a TS {@link com.github.theholywaffle.teamspeak3.api.wrapper.ServerGroup}
 * that is connected to a {@link Rank}.
 */
public class HookGroup {

  @Getter
  private final Rank rank;
  @Getter
  private final ServerGroup serverGroup;

  public HookGroup(Rank rank, ServerGroup serverGroup) {
    this.rank = rank;
    this.serverGroup = serverGroup;
  }
}
