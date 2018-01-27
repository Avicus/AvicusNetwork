package net.avicus.magma.channel.staff;

import net.avicus.compendium.settings.PlayerSettings;
import net.avicus.magma.channel.distributed.DistributedSimpleDescriptorChannel;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class AbstractStaffChannel extends DistributedSimpleDescriptorChannel {

  protected AbstractStaffChannel(String id, String permission, BaseComponent descriptor) {
    super(id, permission, descriptor);
  }

  @Override
  public boolean canRead(final CommandSender viewer) {
    return
        !(viewer instanceof Player && !PlayerSettings.get((Player) viewer, StaffChannels.SETTING))
            && super.canRead(viewer);
  }
}
