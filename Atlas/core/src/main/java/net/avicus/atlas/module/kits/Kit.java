package net.avicus.atlas.module.kits;

import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.match.registry.RegisterableObject;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.loadouts.Loadout;
import net.avicus.atlas.module.locales.LocalizedXmlString;
import net.avicus.atlas.util.Events;
import net.avicus.atlas.util.Messages;
import net.avicus.atlas.util.ScopableItemStack;
import net.avicus.compendium.locale.text.Localizable;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;

@ToString
public class Kit implements RegisterableObject<Kit> {

  @Getter
  private final String id;
  @Getter
  private final Optional<Check> applicationCheck;
  @Getter
  private final LocalizedXmlString name;
  @Getter
  private final Optional<LocalizedXmlString> description;
  @Getter
  private final ScopableItemStack icon;
  private final Optional<Loadout> loadout;
  private final List<KitAbility> abilities;
  @Getter
  private final List<KitPermission> permissions;

  public Kit(String id,
      Optional<Check> applicationCheck,
      LocalizedXmlString name,
      Optional<LocalizedXmlString> description,
      ScopableItemStack icon,
      Optional<Loadout> loadout,
      List<KitAbility> abilities,
      List<KitPermission> permissions) {
    this.id = id;
    this.applicationCheck = applicationCheck;
    this.name = name;
    this.description = description;
    this.icon = icon;
    this.loadout = loadout;
    this.abilities = abilities;
    this.permissions = permissions;
  }

  public void enable() {
    this.abilities.forEach(Events::register);
  }

  public void disable() {
    this.abilities.forEach(Events::unregister);
  }

  public boolean hasAbility(KitAbility ability) {
    return this.abilities.contains(ability);
  }

  public void apply(Player player) {
    // Loadouts
    if (this.loadout.isPresent()) {
      this.loadout.get().apply(player);
    }

    applyPermissions(player);
  }


  private void applyPermissions(Permissible permissible) {
    PermissionAttachment attach = permissible.addAttachment(Atlas.get());
    attach.setPermission("atlas.active-kit." + this.getName().translateDefault().replace(' ', '_'),
        true);

    for (KitPermission perm : this.getPermissions()) {
      attach.setPermission(perm.getNode(), perm.isValue());
    }

    permissible.recalculatePermissions();
  }

  public void removePermissions(Permissible permissible) {
    boolean changed = false;

    for (PermissionAttachmentInfo attachInfo : permissible.getEffectivePermissions()) {
      if (attachInfo.getPermission()
          .startsWith("atlas.active-kit." + this.getName().translateDefault().replace(' ', '_'))) {
        permissible.removeAttachment(attachInfo.getAttachment());
        changed = true;
      }
    }

    if (changed) {
      permissible.recalculatePermissions();
    }
  }

  public void displaySelectedMessage(Player player) {
    final Localizable name = this.getName().toText();
    name.style().color(ChatColor.GOLD).bold();

    player.sendMessage(Messages.GENERIC_KIT_SELECTED.with(ChatColor.YELLOW, name));
  }

  @Override
  public Kit getObject() {
    return this;
  }
}
