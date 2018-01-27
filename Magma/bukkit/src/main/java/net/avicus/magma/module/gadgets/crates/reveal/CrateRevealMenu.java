package net.avicus.magma.module.gadgets.crates.reveal;

import java.util.Date;
import java.util.List;
import java.util.Random;
import net.avicus.compendium.WeightedRandomizer;
import net.avicus.compendium.menu.IndexedMenuItem;
import net.avicus.compendium.menu.inventory.InventoryMenu;
import net.avicus.compendium.menu.inventory.InventoryMenuItem;
import net.avicus.magma.module.gadgets.Gadget;
import net.avicus.magma.module.gadgets.GadgetContext;
import net.avicus.magma.module.gadgets.crates.CrateGadget;
import net.avicus.magma.network.user.Users;
import net.avicus.magma.util.MagmaTask;
import net.avicus.magma.util.MagmaTranslations;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CrateRevealMenu extends InventoryMenu {

  private static final Random RANDOM = new Random();
  private static final int PICK_DELAY = 20 * 8;
  private static final int PICK_RANDOM = 40;  // padding around when a crate will open

  private final Player player;
  private final int pickDelay;  // when this crate will open

  private final WeightedRandomizer<Gadget> randomizer;
  private final MagmaTask task;
  private final List<Gadget> currentSet;
  private int ticksElapsed;
  private GadgetContext picked;

  public CrateRevealMenu(Player player, CrateGadget crate) {
    super(player, createTitle(player), 2);
    this.player = player;
    this.pickDelay = randomPickDelay();
    this.randomizer = crate.getRandomizer();
    this.task = new CrateRevealTask();
    this.currentSet = this.randomizer.next(9);
  }

  private static int randomPickDelay() {
    int min = PICK_DELAY - PICK_RANDOM;
    int max = PICK_DELAY + PICK_RANDOM;
    return RANDOM.nextInt(max - min) + min;
  }

  private static int delayAt(int ticksElapsed) {
    // Fast initially, slows with time
    double result = Math.pow((double) ticksElapsed / 40.0, 2.0) + 3;
    return (int) Math.floor(result);
  }

  private static String createTitle(Player player) {
    return MagmaTranslations.GUI_CRATE_REVEAL.with(ChatColor.DARK_GRAY)
        .translate(player.getLocale()).toLegacyText();
  }

  public boolean isPicked() {
    return this.picked != null;
  }

  public void spin() {
    open();
    this.task.later(5);
  }

  public void pick() {
    Gadget gadget = this.currentSet.get(4);
    this.picked = gadget.defaultContext();
    MagmaTask.of(() -> gadget.getManager().getGadgets()
        .createBackpackGadget(Users.user(this.player), picked, true, new Date())).nowAsync();
  }

  private void next() {
    this.currentSet.remove(0);
    this.currentSet.add(this.randomizer.next());
    clear();

    for (int i = 0; i < 3; i++) {
      add(new OtherItemIndictor(i, ChatColor.RED, (byte) 14));
    }
    for (int i = 6; i < 9; i++) {
      add(new OtherItemIndictor(i, ChatColor.RED, (byte) 14));
    }

    add(new OtherItemIndictor(3, ChatColor.GOLD, (byte) 1));
    add(new YourItemIndicator(4));
    add(new OtherItemIndictor(5, ChatColor.GOLD, (byte) 1));

    int i = 0;
    for (Gadget gadget : this.currentSet) {
      double likelihood = this.randomizer.getLikelihood(gadget);
      add(new CrateRevealItem(this.player, gadget, 9 + i, likelihood));
      i++;
    }
  }

  @Override
  public void onExit() {
    if (!isPicked()) {
      open();
    }
  }

  private static class YourItemIndicator implements IndexedMenuItem, InventoryMenuItem {

    private final int index;

    public YourItemIndicator(int index) {
      this.index = index;
    }

    @Override
    public int getIndex() {
      return this.index;
    }

    @Override
    public ItemStack getItemStack() {
      ItemStack stack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 5);
      ItemMeta meta = stack.getItemMeta();
      meta.setDisplayName(ChatColor.GREEN.toString() + ChatColor.BOLD + "▼ YOUR ITEM ▼");
      stack.setItemMeta(meta);
      return stack;
    }

    @Override
    public boolean shouldUpdate() {
      return false;
    }

    @Override
    public void onUpdate() {

    }
  }

  private static class OtherItemIndictor implements IndexedMenuItem, InventoryMenuItem {

    private final int index;
    private ChatColor color;
    private byte glassColor;

    public OtherItemIndictor(int index, ChatColor color, byte glassColor) {
      this.index = index;
      this.color = color;
      this.glassColor = glassColor;
    }

    @Override
    public int getIndex() {
      return this.index;
    }

    @Override
    public ItemStack getItemStack() {
      ItemStack stack = new ItemStack(Material.STAINED_GLASS_PANE, 1, this.glassColor);
      ItemMeta meta = stack.getItemMeta();
      meta.setDisplayName(this.color.toString() + ChatColor.BOLD + "▼ CRATE ITEMS ▼");
      stack.setItemMeta(meta);
      return stack;
    }

    @Override
    public boolean shouldUpdate() {
      return false;
    }

    @Override
    public void onUpdate() {

    }
  }

  private class CrateRevealTask extends MagmaTask {

    @Override
    public void run() throws Exception {
      if (ticksElapsed >= pickDelay) {
        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1f, 2.0f);
        pick();
        cancel();
        return;
      }

      player.playSound(player.getLocation(), Sound.CLICK, 0.5f, 2.0f);
      next();

      int delay = delayAt(ticksElapsed);
      later(delay);
      ticksElapsed += delay;
    }
  }
}
