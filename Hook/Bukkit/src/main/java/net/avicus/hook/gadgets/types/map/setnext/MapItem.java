package net.avicus.hook.gadgets.types.map.setnext;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.countdown.CyclingCountdown;
import net.avicus.atlas.map.AtlasMap;
import net.avicus.atlas.map.rotation.Rotation;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.states.StatesModule;
import net.avicus.compendium.StringUtil;
import net.avicus.compendium.TextStyle;
import net.avicus.compendium.menu.inventory.ClickableInventoryMenuItem;
import net.avicus.compendium.menu.inventory.StaticInventoryMenuItem;
import net.avicus.hook.gadgets.types.map.AtlasGadget;
import net.avicus.hook.utils.Messages;
import net.avicus.magma.channel.staff.StaffChannels;
import net.avicus.magma.event.CheckPrerequisitesEvent;
import net.avicus.magma.game.MinecraftMap;
import net.avicus.magma.module.gadgets.EmptyGadgetContext;
import net.avicus.magma.util.Events;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MapItem extends StaticInventoryMenuItem implements ClickableInventoryMenuItem {

  private final MinecraftMap map;
  private final Player player;
  private final AtlasGadget gadget;
  private final EmptyGadgetContext<AtlasGadget> context;

  public MapItem(MinecraftMap map, Player player, AtlasGadget gadget,
      EmptyGadgetContext<AtlasGadget> context) {
    this.map = map;
    this.player = player;
    this.gadget = gadget;
    this.context = context;
  }

  @Override
  public void onClick(InventoryClickEvent event) {
    if (!Events.call(new CheckPrerequisitesEvent("gadget.set-next", this.map, this.player))
        .isCancelled()) {
      Rotation rotation = Atlas.get().getMatchManager().getRotation();
      if (rotation.isRestartQueued() || rotation.isRestarting()) {
        player.sendMessage(Messages.ERROR_GADGETS_SN_RESTARTING.with(org.bukkit.ChatColor.AQUA));
        return;
      }
      if (rotation.isNextRequestQueued()) {
        player.sendMessage(Messages.ERROR_GADGETS_SN_QUEUED.with(org.bukkit.ChatColor.RED));
        return;
      } else if (rotation.getMatch().getMap().getSlug().equals(this.map.getSlug())) {
        player.sendMessage(Messages.ERROR_GADGETS_SN_PLAYING.with(org.bukkit.ChatColor.RED));
        return;
      } else {
        Match next = null;
        try {
          next = Atlas.get().getMatchManager().getFactory().create((AtlasMap) this.map);
          rotation.next(next, true);
          rotation.setNextRequestMap((AtlasMap) this.map);
          rotation.setNextRequestQueued(true);
        } catch (Exception e) {
          // Map parsing failed, send an alert to map devs and tell the user to select something else.
          StaffChannels.MAPDEV_CHANNEL.simpleLocalSend(null, new TextComponent(
              map.getName() + " was requested to be played and failed to parse!"));
          player.sendMessage(Messages.ERROR_GADGETS_SN_ERROR.with(org.bukkit.ChatColor.RED));
          return;
        }
        StaffChannels.MAPDEV_CHANNEL.simpleLocalSend(null, new TextComponent(
            "[GADGET] " + player.getName() + " set the next map to " + map.getName() + "."));
        Match match = Atlas.getMatch();
        if (match.getRequiredModule(StatesModule.class).isCycling()) {
          try {
            rotation.cycleMatch(new CyclingCountdown(match, next));
          } catch (Exception e) {
            e.printStackTrace();
            player.sendMessage(Messages.ERROR_GADGETS_SN_ERROR.with(org.bukkit.ChatColor.RED));
            return;
          }
        }
      }
      player.closeInventory();
      player.sendMessage(Messages.UI_SN_SUCCESS.with(org.bukkit.ChatColor.GREEN, map.getName()));
      gadget.getManager().getGadgets().deleteBackpackGadget(this.context);
    }
  }

  @Override
  public ItemStack getItemStack() {
    ItemStack stack = new ItemStack(Material.EMPTY_MAP);
    ItemMeta meta = stack.getItemMeta();
    meta.setDisplayName(niceMapName());
    // Authors
    List<String> authorNames = map.getAuthors().stream()
        .map(author -> ChatColor.GREEN + author.getName() + ChatColor.AQUA)
        .collect(Collectors.toList());
    String authorString = StringUtil.listToEnglishCompound(authorNames);
    List<String> lore = new ArrayList<>();
    lore.add(ChatColor.AQUA + "By: " + authorString);
    lore.add(Messages.UI_CLICK_MAP.with(TextStyle.ofColor(org.bukkit.ChatColor.BLUE))
        .render(this.player).toLegacyText());
    meta.setLore(lore);
    stack.setItemMeta(meta);
    return stack;
  }

  private String niceMapName() {
    StringBuilder builder = new StringBuilder();

    builder.append(ChatColor.GOLD);
    builder.append(this.map.getName());
    builder.append(" ");
    builder.append(ChatColor.GRAY);
    builder.append("(");
    builder.append(ChatColor.AQUA);
    builder.append(this.map.getVersion());
    builder.append(ChatColor.GRAY);
    builder.append(")");

    return builder.toString();
  }
}
