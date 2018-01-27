package net.avicus.hook.temp;

import com.github.theholywaffle.teamspeak3.api.ChannelProperty;
import com.github.theholywaffle.teamspeak3.api.CommandFuture;
import com.github.theholywaffle.teamspeak3.api.wrapper.ServerGroup;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import lombok.Data;
import lombok.Getter;
import net.avicus.compendium.config.Config;
import net.avicus.hook.HookConfig;
import net.avicus.hook.Main;
import net.avicus.hook.temp.packages.ExtensionPackage;
import net.avicus.hook.temp.packages.InitialPackage;
import net.avicus.hook.temp.packages.Package;
import net.avicus.hook.wrapper.HookClient;
import org.joda.time.DateTime;

public class TemporaryChannels {

  private static final HashMap<Integer, InitialPackage> initialPrices = new HashMap<>();
  private static final HashMap<Integer, ExtensionPackage> extensionPrices = new HashMap<>();
  private static Optional<InitialPackage> defaultInitial = Optional.empty();
  private static Optional<ExtensionPackage> defaultExtension = Optional.empty();
  private static List<Integer> cooling = new ArrayList<>();

  private static HashMap<Integer, TaskInfo> deleteTasks = new HashMap<>();

  private static Map<ChannelProperty, String> defaultProperties = new HashMap<>();
  private static Map<String, Integer> defaultPermissions = new HashMap<>();

  public static void init() {
    if (!HookConfig.TempChannels.isEnabled()) {
      return;
    }

    String initialSuccess = HookConfig.TempChannels.getInitialSuccess();
    String extensionSuccess = HookConfig.TempChannels.getExtensionSuccess();

    defaultProperties.put(ChannelProperty.CHANNEL_CODEC, "4");
    defaultProperties.put(ChannelProperty.CHANNEL_FLAG_SEMI_PERMANENT, "1");
    defaultProperties.put(ChannelProperty.CHANNEL_CODEC_QUALITY, "5");
    defaultProperties
        .put(ChannelProperty.CPID, Integer.valueOf(HookConfig.TempChannels.getRootId()).toString());
    defaultProperties.put(ChannelProperty.CHANNEL_PASSWORD, UUID.randomUUID().toString());

    defaultPermissions.put("i_channel_needed_modify_power", 75);
    defaultPermissions.put("i_channel_needed_delete_power", 75);
    defaultPermissions.put("i_channel_needed_permission_modify_power", 75);

    for (Config section : HookConfig.TempChannels.Pricing.getInitial()) {
      int deleteDelay = section.getInt("delete-delay");
      int cost = section.getInt("cost");
      int cooldown = section.getInt("cooldown");

      if (section.contains("default") && section.getBoolean("default")) {
        defaultInitial = Optional
            .of(new InitialPackage(cost, initialSuccess, deleteDelay, cooldown));
      } else {
        int rankId = section.getInt("rank-id");
        initialPrices
            .put(rankId, new InitialPackage(cost, initialSuccess, deleteDelay, cooldown));
      }
    }

    for (Config section : HookConfig.TempChannels.Pricing.getExtensions()) {
      int cost = section.getInt("cost");
      int time = section.getInt("time");

      if (section.contains("default") && section.getBoolean("default")) {
        defaultExtension = Optional.of(new ExtensionPackage(cost, extensionSuccess, time));
      } else {
        int rankId = section.getInt("rank-id");
        extensionPrices.put(rankId, new ExtensionPackage(cost, extensionSuccess, time));
      }
    }

    Main.getHook().getConfirmableCommands().put("tempchannel", TemporaryChannels::tempCommand);
    Main.getHook().getConfirmableCommands().put("extend", TemporaryChannels::extendCommand);
  }

  public static void createChannel(HookClient client, InitialPackage pack) {
    CommandFuture<Integer> channelCreate = Main.getHook().getApiAsync()
        .createChannel(client.getClient().getNickname() + "'s Channel", defaultProperties);
    channelCreate.onSuccess((c) -> {
      Main.getHook().getApi().moveClient(client.getClient().getId(), c);
      Main.getHook().getApi().setClientChannelGroup(5, c, client.getClient().getDatabaseId());
      for (Map.Entry<String, Integer> entry : defaultPermissions.entrySet()) {
        Main.getHook().getApi().addChannelPermission(c, entry.getKey(), entry.getValue());
      }

      Main.getHook().goHome();

      HookConfig.TempChannels.getCreationHelp().forEach(client::message);

      client.message("Your channel will be deleted in " + secondsToClock(pack.getDeleteDelay()));
      client.message("Use !extend to extend the lifetime of this channel.");

      deleteTasks.put(c, new TaskInfo(Main.getExecutor().schedule(() -> {
        Main.getHook().getApi().deleteChannel(c);
        if (client.getClient() != null) {
          client.message(
              "You channel has been deleted! You can buy a new one in " + secondsToClock(
                  pack.getCooldown()));
        }
      }, pack.getDeleteDelay(), TimeUnit.SECONDS), new DateTime(),
          new DateTime().plusSeconds(pack.getDeleteDelay())));
    });
  }

  public static void extendCommand(HookClient sender, List<String> args) {
    int channel = sender.getClient().getChannelId();
    if (deleteTasks.containsKey(channel)) {
      Optional<ExtensionPackage> purchase = attemptPurchase(sender, extensionPrices,
          defaultExtension, false);
      if (purchase.isPresent()) {
        purchase.get().purchase(sender);
        TaskInfo task = deleteTasks.get(channel);
        deleteTasks.remove(channel);
        task.getFuture().cancel(true);
        DateTime newDelay = task.getEnd()
            .plus(new DateTime().plusSeconds(purchase.get().getExtensionTime()).getMillis());
        deleteTasks.put(channel, new TaskInfo(Main.getExecutor().schedule(() -> {
          Main.getHook().getApi().deleteChannel(channel);
        }, newDelay.getMillis() - new DateTime().getMillis(), TimeUnit.SECONDS), new DateTime(),
            newDelay));

        sender.message("Chanel extended for " + secondsToClock(purchase.get().getExtensionTime()));
      }
      ;
    } else {
      sender.message("This channel may not be extended.");
    }
  }

  public static void tempCommand(HookClient sender, List<String> args) {
    Main.getExecutor().execute(() -> {
      Optional<InitialPackage> purchase = attemptPurchase(sender, initialPrices, defaultInitial,
          true);
      if (purchase.isPresent()) {
        cooling.add(sender.getClient().getId());
        Main.getExecutor().schedule(() -> {
        }, purchase.get().getCooldown(), TimeUnit.SECONDS);
        purchase.get().purchase(sender);
        createChannel(sender, purchase.get());
      }
    });
  }

  private static <U extends Package> Optional<U> attemptPurchase(HookClient sender,
      HashMap<Integer, U> options, Optional<U> defaultPackage, boolean cool) {
    int balance = Main.getHook().getDatabase().getCreditTransactions()
        .sumCredits(sender.getUser().getId());

    List<ServerGroup> groups = Main.getHook().getApi().getServerGroupsByClient(sender.getClient());

    final List<U> matches = new ArrayList<U>();
    if (defaultPackage.isPresent()) {
      matches.add(defaultPackage.get());
    }

    for (ServerGroup group : groups) {
      if (options.containsKey(group.getId())) {
        matches.add(options.get(group.getId()));
      }
    }

    if (matches.isEmpty()) {
      sender.message("You are now allowed to create temporary channels!");
      return Optional.empty();
    }

    Optional<U> found = Optional.empty();

    matches.sort((Comparator.comparingInt((Package::getPrice))));

    for (U pack : matches) {
      if (pack.getPrice() <= balance) {
        found = Optional.of(pack);
        break;
      }
    }

    if (!found.isPresent()) {
      sender.message("You do not have enough credits to do this!");
      sender.message("You have: " + balance + "credits!");
      return Optional.empty();
    }

    if (cooling.contains(sender.getClient().getId()) && cool) {
      sender.message("You must wait to do that!");
      return Optional.empty();
    }

    return found;
  }

  public static String secondsToClock(int seconds) {
    int hours = seconds / 3600;
    int minutes = (seconds % 3600) / 60;
    int secs = seconds % 60;

    if (hours == 0) {
      return String.format("[b]%02d : %02d[/b]", minutes, secs);
    }

    return String.format("[b]%02d : %02d : %02d[/b]", hours, minutes, secs);
  }

  @Data
  private static class TaskInfo {

    @Getter
    private final ScheduledFuture<?> future;
    @Getter
    private final DateTime started;
    @Getter
    private final DateTime end;

    public TaskInfo(ScheduledFuture<?> future, DateTime started, DateTime end) {
      this.future = future;
      this.started = started;
      this.end = end;
    }
  }
}
