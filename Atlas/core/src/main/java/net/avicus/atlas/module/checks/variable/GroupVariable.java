package net.avicus.atlas.module.checks.variable;

import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.module.checks.Variable;
import net.avicus.atlas.module.groups.Group;

/**
 * The group variable contains information about the group that a player is in.
 */
@ToString
public class GroupVariable implements Variable {

  @Getter
  private final Group group;

  public GroupVariable(Group group) {
    this.group = group;
  }
}
