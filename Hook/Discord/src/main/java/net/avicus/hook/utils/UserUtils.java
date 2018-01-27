package net.avicus.hook.utils;

import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;

public class UserUtils {

  public static boolean hasRole(User user, Role role) {
    if (role == null) {
      return false;
    }

    return role.getGuild().getMember(user).getRoles().contains(role);
  }

  public static boolean hasRoleOrHigher(User user, Role role) {
    if (role == null) {
      return false;
    }

    Role highest = role.getGuild().getMember(user).getRoles().get(0);
    if (highest == null) {
      return false;
    }

    return highest.getPosition() >= role.getPosition();
  }
}
