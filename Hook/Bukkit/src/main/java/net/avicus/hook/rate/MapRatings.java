package net.avicus.hook.rate;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.event.match.MatchLoadEvent;
import net.avicus.atlas.event.match.MatchStateChangeEvent;
import net.avicus.atlas.map.AtlasMap;
import net.avicus.atlas.map.author.Minecrafter;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.module.states.StatesModule;
import net.avicus.atlas.rate.MapRateEvent;
import net.avicus.compendium.TextStyle;
import net.avicus.compendium.commands.exception.MustBePlayerCommandException;
import net.avicus.compendium.commands.exception.TranslatableCommandErrorException;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.locale.text.LocalizedFormat;
import net.avicus.compendium.locale.text.LocalizedNumber;
import net.avicus.compendium.locale.text.LocalizedText;
import net.avicus.compendium.locale.text.UnlocalizedComponent;
import net.avicus.compendium.locale.text.UnlocalizedText;
import net.avicus.hook.Hook;
import net.avicus.hook.HookConfig;
import net.avicus.hook.credits.Credits;
import net.avicus.hook.utils.Events;
import net.avicus.hook.utils.HookTask;
import net.avicus.hook.utils.Messages;
import net.avicus.magma.channel.staff.StaffChannels;
import net.avicus.magma.database.table.impl.MapRatingTable;
import net.avicus.magma.network.user.Users;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class MapRatings implements Listener {

  private static final Joiner NEW_LINE_JOINER = Joiner.on('\n');
  private static final ChatColor[] COLORS = new ChatColor[]{
      ChatColor.RED,
      ChatColor.GOLD,
      ChatColor.YELLOW,
      ChatColor.GREEN,
      ChatColor.DARK_GREEN
  };
  private static final String SEPARATOR = "\0";
  private static final int MIN_RATING = 1;
  private static final int MAX_RATING = 5;
  private static final int[] ALL_RATINGS = new int[]{
      MIN_RATING,
      2,
      3,
      4,
      MAX_RATING
  };
  private static final int CREDIT_REWARD = 4;
  private final Map<UUID, Integer> ratingCache = Maps.newHashMap();
  private Match match;
  private AtlasMap map;
  private boolean transitioned;

  private static double average(final Collection<Integer> values) {
    return values.stream().mapToInt(Integer::valueOf).average().orElse(0.0d);
  }

  @Command(aliases = {"rate"}, desc = "Rate the current map.", usage = "<1-5>", min = 1, max = 1)
  public static void rate(final CommandContext args, final CommandSender source)
      throws CommandException {
    final int rating = args.getInteger(0);
    if (MIN_RATING > rating || rating > MAX_RATING) {
      throw new TranslatableCommandErrorException(Messages.MAP_RATINGS_OUT_OF_RANGE);
    }

    MustBePlayerCommandException.ensurePlayer(source);

    final Match match = Atlas.getMatch();
    if (match == null) {
      throw new CommandException("No match available");
    }

    Hook.plugin().getServer().getPluginManager()
        .callEvent(new MapRateEvent(match, (Player) source, rating));
  }

  private static Localizable createSummaryComponent(int rating, int ratings) {
    final LocalizedNumber quantityComponent = new LocalizedNumber(ratings);
    quantityComponent.style()
        .color(COLORS[rating - 1])
        .hover(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{
            Messages.MAP_RATINGS_SUMMARY_RATING_OF.with(new LocalizedNumber(rating)).render(null)}));
    return new UnlocalizedText("[{0}]", TextStyle.ofColor(ChatColor.WHITE), quantityComponent);
  }

  private static Localizable createInteractiveButton(Player viewer, int rating) {
    return createButton(viewer, rating, true);
  }

  private static Localizable createButton(Player viewer, int rating, boolean interactive) {
    final LocalizedNumber numberComponent = new LocalizedNumber(rating);
    numberComponent.style().color(COLORS[rating - 1]);

    final Localizable component = numberComponent.duplicate();
    if (interactive) {
      component.style()
          .click(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/rate " + rating))
          .hover(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{
              Messages.MAP_RATINGS_RATE_BUTTON_HOVER.with(numberComponent).render(viewer)}));
    }

    return new UnlocalizedText("[{0}]", TextStyle.ofColor(ChatColor.WHITE), component);
  }

  private static ItemStack createFeedbackBook(String key, UnlocalizedComponent mapName,
                                              UnlocalizedText mapVersion, Player viewer) {
    ItemStack stack = new ItemStack(Material.BOOK_AND_QUILL);
    BookMeta meta = (BookMeta) stack.getItemMeta();
    meta.setDisplayName(
        Messages.MAP_RATINGS_FEEDBACK_BOOK_TITLE.with(mapName, mapVersion).render(viewer)
            .toLegacyText());
    meta.setLore(Collections.singletonList(ChatColor.BLACK + key));
    meta.addPage(
        ""); // A single empty page is required for the player to be able to edit the book properly.
    stack.setItemMeta(meta);
    return stack;
  }

  private static UnlocalizedComponent createUnlocalizedBold(BaseComponent component) {
    final UnlocalizedComponent uc = new UnlocalizedComponent(component);
    uc.style().bold();
    return uc;
  }

  private static UnlocalizedText createUnlocalizedBold(String text) {
    final UnlocalizedText component = new UnlocalizedText(text);
    component.style().bold();
    return component;
  }

  private static String serialize(String slug, String name, String version) {
    return slug + SEPARATOR + name + SEPARATOR + version;
  }

  @EventHandler
  public void matchStateChange(final MatchStateChangeEvent event) {
    if (event.isChangeToPlaying()) {
      this.match = event.getMatch();
      this.map = this.match.getMap();
    }

    if (event.isChangeToNotPlaying()) {
      this.transitioned = true;
      HookTask.of(() -> {
        for (final Player player : event.getMatch().getPlayers()) {
          this.display(player);
        }
      }).later(HookConfig.MapRatings.getDelay() * 20);
    }
  }

  @EventHandler
  public void matchLoad(final MatchLoadEvent event) {
    this.displaySummary();
    this.transitioned = false;
  }

  private void displaySummary() {
    if (this.ratingCache.isEmpty()) {
      return;
    }

    final Locale locale = Locale.US;
    final Collection<Integer> values = this.ratingCache.values();
    final Localizable[] args = new Localizable[ALL_RATINGS.length + 3];
    args[0] = new UnlocalizedText(this.map.getName());
    args[1] = new UnlocalizedText(this.map.getVersion().toString());
    args[args.length - 1] = new LocalizedNumber(average(values));
    for (final int rating : ALL_RATINGS) {
      args[rating + 1] = createSummaryComponent(rating,
          Collections.frequency(values, rating));
    }

    LocalizedText summary = Messages.MAP_RATINGS_SUMMARY_OVERALL.with(ChatColor.GRAY, args);
    StaffChannels.MAPDEV_CHANNEL
        .simpleLocalSend(Bukkit.getConsoleSender(), summary.render(null));

    // Send to authors who can't see the MD channel.
    this.map.getAuthors().stream()
        .filter(author -> author instanceof Minecrafter)
        .map(author -> (Minecrafter) author)
        .map(author -> Bukkit.getPlayer(author.getUuid()))
        .filter(Objects::nonNull)
        .filter(OfflinePlayer::isOnline)
        .filter(author -> !StaffChannels.MAPDEV_CHANNEL.canRead(author))
        .forEach(author -> author.sendMessage(summary));

    this.ratingCache.clear();
  }

  @EventHandler
  public void mapRate(final MapRateEvent event) {
    final Player source = event.getPlayer();

    if (this.match == null || (!this.transitioned && !this.match
        .getRequiredModule(StatesModule.class).isPlaying())) {
      source.sendMessage(Messages.MAP_RATINGS_NOT_YET_STARTED.with(ChatColor.RED));
      event.setCancelled(true);
      return;
    }

    GroupsModule groups = this.match.getRequiredModule(GroupsModule.class);

    if (this.match != null && !this.transitioned && groups.isObserving(source)) {
      source.sendMessage(Messages.MAP_RATINGS_NOT_YET_JOINED.with(ChatColor.RED));
      event.setCancelled(true);
      return;
    }

    final int rating = event.getRating();
    final Locale locale = source.getLocale();
    final String mapSlug = this.map.getSlug();
    final String mapName = this.map.getName();
    final String mapVersion = this.map.getVersion().toString();
    final UnlocalizedComponent mapNameComponent = createUnlocalizedBold(
        this.map.getClickableName(source));
    final UnlocalizedText mapVersionComponent = createUnlocalizedBold(mapVersion);

    this.ratingCache.put(source.getUniqueId(), rating);

    HookTask.of(() -> {
      final MapRatingTable mr = Hook.database().getMapRatings();
      final int playerId = Users.user(source).getId();
      final boolean previouslyRatedVersion = mr.findRating(mapSlug, mapVersion, playerId)
          .isPresent();
      final boolean sameRating = mr.findRating(mapSlug, mapVersion, playerId, rating).isPresent();
      final LocalizedFormat message =
          sameRating ? Messages.MAP_RATINGS_RATE_PREVIOUS : Messages.MAP_RATINGS_RATE_SUCCESS;

      mr.setRating(mapSlug, mapVersion, playerId, rating);

      HookTask.of(() -> {
        source.sendMessage(message.with(sameRating ? ChatColor.YELLOW : ChatColor.GREEN,
            createButton(source, rating, false), mapNameComponent, mapVersionComponent));

        if (HookConfig.MapRatings.isBookEnabled() && groups.isObserving(source)) {
          source.sendMessage(Messages.MAP_RATINGS_RATE_SUCCESS_BOOK.with(ChatColor.GREEN));
          source.getInventory().addItem(
              createFeedbackBook(serialize(mapSlug, mapName, mapVersion), mapNameComponent,
                  mapVersionComponent, source));
        }

        if (!previouslyRatedVersion) {
          Credits.reward(source, CREDIT_REWARD,
              Messages.UI_REWARD_RATE.with(mapNameComponent, mapVersionComponent));
          Events.call(new MapRatedEvent(event.getMatch(), event.getPlayer(), event.getRating()));
        } else {
          event.setCancelled(true);
        }
      }).now();
    }).nowAsync();
  }

  private void display(final Player player) {
    final Locale locale = player.getLocale();
    final Localizable[] args = new Localizable[ALL_RATINGS.length];
    for (final int rating : ALL_RATINGS) {
      args[rating - 1] = createInteractiveButton(player, rating);
    }

    player.sendMessage(Messages.MAP_RATINGS_RATE_MESSAGE.with(args));
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void playerEditBook(final PlayerEditBookEvent event) {
    final Player player = event.getPlayer();
    final List<String> lore = event.getPreviousBookMeta().getLore();
    if (lore == null) {
      return;
    }

    final String[] parts = ChatColor.stripColor(lore.get(0)).split(SEPARATOR);
    if (parts.length != 3) {
      return;
    }

    final List<String> pages = event.getNewBookMeta().getPages();
    HookTask.of(() -> {
      final int playerId = Users.user(player).getId();
      Hook.database().getMapRatings().setFeedback(parts[0], parts[2], playerId,
          ChatColor.stripColor(NEW_LINE_JOINER.join(pages)));
      HookTask.of(() -> player.sendMessage(Messages.MAP_RATINGS_FEEDBACK_SUCCESS
          .with(ChatColor.GREEN, createUnlocalizedBold(parts[1]), new UnlocalizedText(parts[2]))))
          .now();
    }).nowAsync();
  }
}
