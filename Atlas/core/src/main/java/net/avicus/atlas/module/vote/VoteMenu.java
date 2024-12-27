package net.avicus.atlas.module.vote;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.GameType;
import net.avicus.atlas.map.AtlasMap;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.util.Messages;
import net.avicus.atlas.util.Translations;
import net.avicus.compendium.locale.text.LocalizedNumber;
import net.avicus.compendium.locale.text.UnlocalizedFormat;
import net.avicus.compendium.menu.inventory.ClickableInventoryMenuItem;
import net.avicus.compendium.menu.inventory.InventoryMenu;
import net.avicus.compendium.menu.inventory.InventoryMenuItem;
import net.avicus.magma.profile.Skins;
import net.avicus.magma.util.Inventories;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitTask;

public class VoteMenu extends InventoryMenu implements Runnable {

  private static final UnlocalizedFormat VOTES_FORMAT = new UnlocalizedFormat("{0}: {1}");
  private BukkitTask task;

  private VoteMenu(VoteModule module, Player player) {
    super(player,
        Messages.VOTE_TITLE.with(ChatColor.RED).render(player).toLegacyText(),
        Math.max(1, Inventories.rowCount(module.getOptions().size())),
        createContents(module, player));
  }

  public static VoteMenu create(VoteModule module, Player player) {
    return new VoteMenu(module, player);
  }

  private static List<InventoryMenuItem> createContents(VoteModule module, Player player) {
    List<InventoryMenuItem> contents = Lists.newArrayList();

    for (Map.Entry<Integer, Match> entry : module.getOptions().entrySet()) {
      contents.add(Item.of(module, entry.getValue(), player));
    }

    return contents;
  }

  public static ItemStack create(Player player) {
    final ItemStack stack = new ItemStack(Material.PAPER);
    final ItemMeta meta = stack.getItemMeta();
    meta.setDisplayName(
        Messages.VOTE_TITLE.with(ChatColor.GOLD).render(player).toLegacyText());
    meta.setLore(Collections.singletonList(ChatColor.BLACK + "Vote Menu"));
    stack.setItemMeta(meta);
    return stack;
  }

  public static boolean matches(ItemStack stack) {
    if (stack == null) {
      return false;
    }

    final ItemMeta meta = stack.getItemMeta();
    return meta.hasLore() && meta.getLore().contains(ChatColor.BLACK + "Vote Menu");
  }

  @Override
  public void open() {
    super.open();

    if (this.task != null) {
      this.task.cancel();
    }

    this.task = Bukkit.getScheduler().runTaskTimer(Atlas.get(), this, 0, 20);
  }

  @Override
  public void close() {
    super.close();
    this.task.cancel();
  }

  @Override
  public void run() {
    this.update(false);
  }

  public static class Item implements ClickableInventoryMenuItem, InventoryMenuItem {

    private final VoteModule module;
    private final Match map;
    private final Player player;

    private Item(VoteModule module, Match map, Player player) {
      this.module = module;
      this.map = map;
      this.player = player;
    }

    public static Item of(VoteModule module, Match map, Player player) {
      return new Item(module, map, player);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
      this.module.cast(this.player, this.map);
    }

    @Override
    public ItemStack getItemStack() {
      final boolean active = this.module.isCast(this.player, this.map);
      final ItemStack stack = new ItemStack(Material.SKULL_ITEM, 1,
          (byte) SkullType.PLAYER.ordinal());
      final SkullMeta meta = (SkullMeta) stack.getItemMeta();
      final AtlasMap info = this.map.getMap();
      meta.setDisplayName(
          ChatColor.RESET + "" + ChatColor.DARK_AQUA + (active ? ChatColor.BOLD : "") + info
              .getName());
      final List<String> lore = Lists.newArrayList();
      lore.add(VOTES_FORMAT.with(ChatColor.WHITE, Messages.VOTE_VOTES.with(ChatColor.GRAY),
          new LocalizedNumber(Collections.frequency(this.module.votes(), this.map)))
          .render(this.player).toLegacyText());
      final EnumSet<GameType> gameTypes = info.getGameTypes();
      if (!gameTypes.isEmpty()) {
        lore.add("");
        lore.add((gameTypes.size() == 1 ? Translations.GAMETYPE_NAME_SINGULAR
            : Translations.GAMETYPE_NAME_PLURAL).with(ChatColor.GRAY).render(this.player)
            .toLegacyText());
        for (GameType gameType : gameTypes) {
          lore.add(
              "  " + gameType.getName().with(ChatColor.WHITE).render(this.player).toLegacyText());
        }
      }
      meta.setLore(lore);
      meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
      if (active) {
        meta.setOwner(null, Skins.EARTH_DARK_TO_LIGHT_ID, Skins.EARTH_DARK_TO_LIGHT);
        meta.addEnchant(Enchantment.SILK_TOUCH, 1, false);
      } else {
        meta.setOwner(null, Skins.EARTH_ID, Skins.EARTH);
      }
      stack.setItemMeta(meta);
      return stack;
    }

    @Override
    public boolean shouldUpdate() {
      return true;
    }

    @Override
    public void onUpdate() {
    }
  }
}
