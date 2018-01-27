package net.avicus.hook;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.Getter;
import net.avicus.hook.wrapper.HookRole;
import net.avicus.magma.database.model.impl.Rank;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.utils.PermissionUtil;

public class RoleManagementService {

  private static Hook hook = Main.getHook();
  private final Guild guild;
  // A map that links a role id to our wrappers.
  @Getter
  private ConcurrentHashMap<Long, HookRole> rankRelations = new ConcurrentHashMap<>();

  public RoleManagementService(Guild guild) {
    this.guild = guild;
    mapRanks();
  }

  public void mapRanks() {
    rankRelations.clear();
    for (Role role : this.guild.getRoles().stream()
        .filter(r -> PermissionUtil.canInteract(guild.getSelfMember(), r))
        .collect(Collectors.toSet())) {
      hook.getDatabase().getRanks().findByName(role.getName()).ifPresent(r -> {
        rankRelations.put(role.getIdLong(), new HookRole(r, role));
      });
    }
  }

  public Optional<HookRole> getRoleFromRank(Rank rank) {
    Optional<HookRole> result = Optional.empty();

    List<HookRole> found = rankRelations.values()
        .stream()
        .filter(group -> group.getRank().getId() == rank.getId())
        .collect(Collectors.toList());

    if (!found.isEmpty()) {
      result = Optional.of(found.get(0));
    }

    return result;
  }

  public Optional<HookRole> getRankFromRole(Role role) {
    Optional<HookRole> result = Optional.empty();

    List<HookRole> found = rankRelations.values()
        .stream()
        .filter(group -> group.getRole().getIdLong() == role.getIdLong())
        .collect(Collectors.toList());

    if (!found.isEmpty()) {
      result = Optional.of(found.get(0));
    }

    return result;
  }
}
