package net.avicus.hook.credits;

import com.google.common.base.Preconditions;
import com.sk89q.bukkit.util.CommandsManagerRegistration;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.avicus.compendium.TextStyle;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.locale.text.LocalizableFormat;
import net.avicus.compendium.locale.text.LocalizedNumber;
import net.avicus.compendium.locale.text.UnlocalizedFormat;
import net.avicus.hook.Hook;
import net.avicus.hook.HookConfig;
import net.avicus.hook.credits.reward.CreditRewardListener;
import net.avicus.hook.utils.Events;
import net.avicus.hook.utils.HookTask;
import net.avicus.hook.utils.Messages;
import net.avicus.magma.database.model.impl.CreditTransaction;
import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.network.user.Users;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.PermissionAttachmentInfo;

public class Credits {

  private static final LocalizableFormat REWARD_DISPLAY = new UnlocalizedFormat(
      "+{0} credits » {1}");
  private static final LocalizableFormat REWARD_DISPLAY_MULTIPLIER = new UnlocalizedFormat(
      "+{0} credits ({1}x) » {2}");
  private static final String MULTIPLIER_PERMISSION = "hook.credit.multiplier";
  private static final String MULTIPLIER_PREFIX = MULTIPLIER_PERMISSION + ".";

  private static Map<UUID, Integer> creditBalances;  // user uuid -> credits

  public static void init(CommandsManagerRegistration cmds) {
    cmds.register(CreditCommand.class);
    creditBalances = new HashMap<>();

    if (HookConfig.Credits.Rewards.isEnabled()) {
      Events.register(new CreditRewardListener());
    }

    Events.register(new CreditsListener());
    Events.register(new StoreListener());
  }

  public static boolean isGadgetStoreOpener(ItemStack stack) {
    return stack != null &&
        stack.hasItemMeta() &&
        stack.getItemMeta().hasLore() &&
        stack.getItemMeta().getLore().contains(ChatColor.BLACK + "Gadget Store");
  }

  public static ItemStack createGadgetStoreOpener(Player player) {
    ItemStack stack = new ItemStack(Material.GOLD_NUGGET);
    ItemMeta meta = stack.getItemMeta();

    meta.setDisplayName(
        Messages.UI_GADGET_STORE.with(ChatColor.YELLOW).render(player)
            .toLegacyText());
    meta.setLore(Arrays.asList(ChatColor.BLACK + "Gadget Store"));

    stack.setItemMeta(meta);
    return stack;
  }

  private static Optional<Double> multiplier(Player player) {
    if (!player.hasPermission(MULTIPLIER_PERMISSION)) {
      return Optional.empty();
    }

    double greatest = -1;

    for (PermissionAttachmentInfo perm : player.getEffectivePermissions()) {
      if (!perm.getValue()) {
        continue;
      }
      if (!perm.getPermission().startsWith(MULTIPLIER_PREFIX)) {
        continue;
      }

      try {
        double value = Double.parseDouble(perm.getPermission().replace(MULTIPLIER_PREFIX, ""));
        if (value > greatest) {
          greatest = value;
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    if (greatest < 0) {
      return Optional.empty();
    }
    return Optional.of(greatest);
  }

  private static void modifyLocalBalance(UUID user, int amount) {
    creditBalances.put(user, creditBalances.getOrDefault(user, 0) + amount);
  }

  public static void load(User user) {
    creditBalances
        .put(user.getUniqueId(), Hook.database().getCreditTransactions().sumCredits(user.getId()));
  }

  public static void unload(User user) {
    creditBalances.remove(user.getUniqueId());
  }

  public static int getCredits(Player player) {
    return creditBalances.getOrDefault(player.getUniqueId(), 0);
  }

  public static boolean hasAtLeast(Player player, int credits) {
    return getCredits(player) >= credits;
  }

  public static void give(Player player, int amount) {
    User user = Users.user(player);
    amount = Math.abs(amount);

    PlayerModifyCreditEvent event = new PlayerModifyCreditEvent(player, amount);
    Events.call(event);

    double multiplier = multiplier(player).orElse(1.0);

    CreditTransaction transaction = new CreditTransaction(user.getId(), amount, multiplier,
        new Date());

    HookTask.of(() -> Hook.database().getCreditTransactions().insert(transaction).execute())
        .nowAsync();
    modifyLocalBalance(user.getUniqueId(), amount);
  }

  public static void take(Player player, int amount) {
    User user = Users.user(player);
    amount = -Math.abs(amount);

    PlayerModifyCreditEvent event = new PlayerModifyCreditEvent(player, amount);
    Events.call(event);

    CreditTransaction transaction = new CreditTransaction(user.getId(), amount, 1.0, new Date());

    HookTask.of(() -> Hook.database().getCreditTransactions().insert(transaction).execute())
        .nowAsync();
    modifyLocalBalance(user.getUniqueId(), amount);
  }

  public static void reward(Player player, int amount, Localizable reason) {
    reward(player, amount, reason, true);
  }

  public static void reward(Player player, int amount, Localizable reason, boolean multiply) {
    Preconditions.checkArgument(amount > 0);

    double multiplier = multiplier(player).orElse(1.0);
    int multipliedAmount = amount;
    if (multiply) {
      multipliedAmount = (int) Math.floor(multiplier * (double) amount);
    }

    player.sendMessage(rewardDisplay(player, multipliedAmount, reason, multiply));
    give(player, multipliedAmount);
  }

  public static void reward(Player player, int amount, LocalizableFormat reason) {
    reward(player, amount, reason, true);
  }

  public static void reward(Player player, int amount, LocalizableFormat reason, boolean multiply) {
    reward(player, amount, reason.with(), multiply);
  }

  private static Localizable rewardDisplay(Player player, int amount, Localizable reason) {
    return rewardDisplay(player, amount, reason, true);
  }

  private static Localizable rewardDisplay(Player player, int amount, Localizable reason,
      boolean showMultiply) {
    Double multiplier = multiplier(player).orElse(null);

    Localizable textAmount = new LocalizedNumber(amount, TextStyle.ofColor(ChatColor.GOLD).bold());

    if (multiplier == null || !showMultiply) {
      return REWARD_DISPLAY.with(ChatColor.GRAY, textAmount, reason);
    } else {
      Localizable textMultiplier = new LocalizedNumber(multiplier, 1, 2,
          TextStyle.ofColor(ChatColor.GOLD).bold());
      return REWARD_DISPLAY_MULTIPLIER.with(ChatColor.GRAY, textAmount, textMultiplier, reason);
    }
  }
}
