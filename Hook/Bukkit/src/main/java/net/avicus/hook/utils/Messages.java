package net.avicus.hook.utils;

import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.locale.text.LocalizableFormat;
import net.avicus.compendium.locale.text.LocalizedFormat;
import net.avicus.compendium.locale.text.LocalizedText;
import net.avicus.hook.Hook;
import org.bukkit.ChatColor;

public class Messages {

  public static final LocalizedFormat ERROR_NO_FRIENDS = get("errors.no-friends");
  public static final LocalizedFormat ERROR_NO_SUCH_USER = get("errors.no-such-user");
  public static final LocalizedFormat ERROR_NO_ATLAS = get("errors.no-atlas");
  public static final LocalizedFormat MAP_RATINGS_RATE_MESSAGE = get("map-ratings.message");
  public static final LocalizedFormat MAP_RATINGS_RATE_BUTTON_HOVER = get(
      "map-ratings.button-hover");
  public static final LocalizedFormat MAP_RATINGS_OUT_OF_RANGE = get("map-ratings.out-of-range");
  public static final LocalizedFormat MAP_RATINGS_RATE_SUCCESS = get("map-ratings.success");
  public static final LocalizedFormat MAP_RATINGS_RATE_PREVIOUS = get("map-ratings.previous");
  public static final LocalizedFormat MAP_RATINGS_RATE_SUCCESS_BOOK = get(
      "map-ratings.success-book");
  public static final LocalizedFormat MAP_RATINGS_FEEDBACK_BOOK_TITLE = get(
      "map-ratings.feedback.book-title");
  public static final LocalizedFormat MAP_RATINGS_FEEDBACK_SUCCESS = get(
      "map-ratings.feedback.success");
  public static final LocalizedFormat MAP_RATINGS_SUMMARY_OVERALL = get(
      "map-ratings.summary.overall");
  public static final LocalizedFormat MAP_RATINGS_SUMMARY_RATING_OF = get(
      "map-ratings.summary.rating-of");
  public static final LocalizedFormat MAP_RATINGS_NOT_YET_STARTED = get(
      "map-ratings.not-yet.started");
  public static final LocalizedFormat MAP_RATINGS_NOT_YET_JOINED = get(
      "map-ratings.not-yet.joined");
  public static LocalizedFormat ERROR_NO_PUNISHMENTS = get("errors.no-punishments");
  public static LocalizedFormat ERROR_CLEAN_HISTORY = get("errors.clean-history");
  public static LocalizedFormat ERROR_NO_PLAYERS = get("errors.no-players");
  public static LocalizedFormat ERROR_ALREADY_FRIENDS = get("errors.already-friends");
  public static LocalizedFormat ERROR_ALREADY_REQUESTED_FRIENDS = get(
      "errors.already-requested-friends");
  public static LocalizedFormat ERROR_NOT_FRIENDS = get("errors.not-friends");
  public static LocalizedFormat ERROR_FRIEND_YOURSELF = get("errors.friend-yourself");

  public static LocalizedFormat ERROR_NO_PERMISSION_PREMIUM = get("errors.no-permission-premium");
  public static LocalizedFormat ERROR_NO_PERMISSION_STATS_HISTORY = get(
      "errors.no-permission-stats-history");
  public static LocalizedFormat ERROR_ERROR_OCCURRED = get("errors.error-occurred");
  public static LocalizedFormat ERROR_TRACK_IN_PROGRESS = get("errors.track-in-progress");
  public static LocalizedFormat ERROR_CANNOT_AFFORD = get("errors.cannot-afford");
  public static LocalizedFormat ERROR_UNMET_REQUIREMENTS = get("errors.unmet-requirements");
  public static LocalizedFormat ERROR_NO_REPLY = get("errors.no-reply");
  public static LocalizedFormat ERROR_CANNOT_USE = get("errors.cannot-use");
  public static LocalizedFormat ERROR_AUTH_NOT_FOUND = get("errors.auth-not-found");
  public static LocalizedFormat ERROR_GADGETS_SN_QUEUED = get("errors.gadgets.set-next.queued");
  public static LocalizedFormat ERROR_GADGETS_SN_PLAYING = get("errors.gadgets.set-next.playing");
  public static LocalizedFormat ERROR_GADGETS_SN_ERROR = get("errors.gadgets.set-next.error");
  public static LocalizedFormat ERROR_GADGETS_SN_RESTARTING = get(
      "errors.gadgets.set-next.restarting");

  public static LocalizedFormat GENERIC_STATS_SINCE = get("generic.stats-since");
  public static LocalizedFormat GENERIC_TELEPORTED = get("generic.teleported");
  public static LocalizedFormat GENERIC_CREDITS = get("generic.credits");
  public static LocalizedFormat GENERIC_APPEALED = get("generic.appealed");
  public static LocalizedFormat GENERIC_APPEALED_DELETED = get("generic.appealed-deleted");
  public static LocalizedFormat GENERIC_JOINED_SERVER = get("generic.joined-server");
  public static LocalizedFormat GENERIC_LEFT_SERVER = get("generic.left-server");
  public static LocalizedFormat GENERIC_SWITCHED_SERVERS = get("generic.switched-servers");
  public static LocalizedFormat GENERIC_CANCELLED_FRIEND = get("generic.cancelled-friend");
  public static LocalizedFormat GENERIC_REQUESTED_FRIEND = get("generic.requested-friend");
  public static LocalizedFormat GENERIC_ACCEPTED_FRIEND = get("generic.accepted-friend");
  public static LocalizedFormat GENERIC_REMOVED_FRIEND = get("generic.removed-friend");

  public static LocalizedFormat GENERIC_TRACK_PLAYING = get("generic.track-playing");
  public static LocalizedFormat GENERIC_MESSAGE_FROM = get("generic.message-from");
  public static LocalizedFormat GENERIC_MESSAGE_TO = get("generic.message-to");
  public static LocalizedFormat GENERIC_GADGET_PURCHASED = get("generic.gadget-purchased");
  public static LocalizedFormat GENERIC_RESETTING_STATS = get("generic.resetting-stats");
  public static LocalizedFormat GENERIC_STATS_RESET_CANCELLED = get(
      "generic.stats-reset-cancelled");
  public static LocalizedFormat GENERIC_STATS_RESET = get("generic.stats-reset");
  public static LocalizedFormat GENERIC_REGISTERED = get("generic.registered");
  public static LocalizedFormat GENERIC_CRATE_REWARD = get("generic.crate-reward");
  public static LocalizedFormat UI_ENABLED = get("ui.enabled");
  public static LocalizedFormat UI_DISABLED = get("ui.disabled");

  public static LocalizedFormat UI_FRIENDS_ONLINE = get("ui.friends-online");
  public static LocalizedFormat UI_FRIENDS_ONLINE_PLURAL = get("ui.friends-online-plural");
  public static LocalizedFormat UI_USAGES = get("ui.usages");
  public static LocalizedFormat UI_KILLS = get("ui.kills");
  public static LocalizedFormat UI_DEATHS = get("ui.deaths");
  public static LocalizedFormat UI_KD = get("ui.kd");
  public static LocalizedFormat UI_CURRENTLY_ON = get("ui.currently-on");
  public static LocalizedFormat UI_LAST_SEEN = get("ui.last-seen");
  public static LocalizedFormat UI_NOT_ONLINE = get("ui.not-online");
  public static LocalizedFormat UI_ONLINE_USERS = get("ui.online-users");
  public static LocalizedFormat UI_PLAYER_HISTORY = get("ui.player-history");
  public static LocalizedFormat UI_GADGET_STORE = get("ui.gadget-store");
  public static LocalizedFormat UI_BADGE = get("ui.badge");
  public static LocalizedFormat UI_MORPH = get("ui.morph");
  public static LocalizedFormat UI_MORPH_ACTIVE = get("ui.morph-active");
  public static LocalizedFormat UI_MORPH_OFF = get("ui.morph-off");
  public static LocalizedFormat UI_SOUND = get("ui.sound");
  public static LocalizedFormat UI_NYAN = get("ui.nyan");
  public static LocalizedFormat UI_BLASTER = get("ui.blaster");
  public static LocalizedFormat UI_GUN = get("ui.gun");
  public static LocalizedFormat UI_CLICK_TRACK = get("ui.click-track");
  public static LocalizedFormat UI_CLICK_MAP = get("ui.click-map");
  public static LocalizedFormat UI_TRASH_BIN = get("ui.trash-bin");
  public static LocalizedFormat UI_TRASH_INFO = get("ui.trash-info");

  public static LocalizedFormat UI_BACK = get("ui.back");
  public static LocalizedFormat UI_PREV_PAGE = get("ui.prev-page");
  public static LocalizedFormat UI_NEXT_PAGE = get("ui.next-page");
  public static LocalizedFormat UI_BACKPACK_USE = get("ui.backpack-use");
  public static LocalizedFormat UI_BACKPACK_TRASH = get("ui.backpack-trash");
  public static LocalizedFormat UI_CONFIRM = get("ui.confirm");
  public static LocalizedFormat UI_CANCEL = get("ui.cancel");
  public static LocalizedFormat UI_CONFIRM_CANCEL = get("ui.confirm-cancel");
  public static LocalizedFormat UI_CREDIT_BOMB = get("ui.credit-bomb");
  public static LocalizedFormat UI_TRACK = get("ui.track");
  public static LocalizableFormat UI_SN = get("ui.set-next");
  public static LocalizableFormat UI_START_VOTE = get("ui.start-vote");
  public static LocalizableFormat UI_SN_SUCCESS = get("ui.set-success");
  public static LocalizedFormat UI_REWARD_MONUMENT_DAMAGED = get("ui.reward.monument-damaged");
  public static LocalizedFormat UI_REWARD_MONUMENT_DESTROYED = get("ui.reward.monument-destroyed");
  public static LocalizedFormat UI_REWARD_FLAG_CAPTURED = get("ui.reward.flag-captured");
  public static LocalizedFormat UI_REWARD_WOOL_PLACE = get("ui.reward.wool-place");
  public static LocalizedFormat UI_REWARD_WOOL_PICKUP = get("ui.reward.wool-pickup");
  public static LocalizedFormat UI_REWARD_LEAKABLE_LEAK = get("ui.reward.leakable-leak");
  public static LocalizedFormat UI_REWARD_SCOREBOX_ENTER = get("ui.reward.scorebox-enter");
  public static LocalizedFormat UI_REWARD_HILL_CAPTURE = get("ui.reward.hill-capture");
  public static LocalizedFormat UI_REWARD_KILL_PLAYER = get("ui.reward.kill-player");
  public static LocalizedFormat UI_REWARD_WIN = get("ui.reward.win");
  public static LocalizedFormat UI_REWARD_PARTICIPATION = get("ui.reward.participation");
  public static LocalizedFormat UI_REWARD_RATE = get("ui.reward.rate");
  public static LocalizedFormat UI_REWARD_ITEM = get("ui.reward.item");
  public static LocalizedFormat UI_NAME_HISTORY = get("ui.name-history");
  public static LocalizedFormat UI_STATS_RESET = get("ui.stats-reset");
  public static LocalizedFormat UI_AFK_LINE_1 = get("ui.afk.line-1");
  public static LocalizedFormat UI_AFK_LINE_2 = get("ui.afk.line-2");
  public static LocalizedFormat UI_ACHIEVEMENT = get("ui.achievement");
  public static LocalizedFormat PUNISHMENT_BROADCAST = get("punishment.broadcast");
  public static LocalizedFormat PUNISHMENT_BROADCAST_TIME = get("punishment.broadcast-time");
  public static LocalizedFormat PUNISHMENT_APPEAL = get("punishment.appeal");
  public static LocalizedFormat PUNISHMENT_MUTED = get("punishment.muted");
  public static LocalizedFormat PUNISHMENT_WARNED = get("punishment.warned");
  public static LocalizedFormat PUNISHMENT_KICKED = get("punishment.kicked");
  public static LocalizedFormat PUNISHMENT_TEMPBANNED = get("punishment.tempbanned");
  public static LocalizedFormat PUNISHMENT_BANNED = get("punishment.banned");
  public static LocalizedFormat PUNISHMENT_WEB_BANNED = get("punishment.web-banned");
  public static LocalizedFormat PUNISHMENT_WEB_TEMPBANNED = get("punishment.web-tempbanned");
  public static LocalizedFormat PUNISHMENT_TOURNAMENT_BANNED = get("punishment.tm-banned");
  public static LocalizedFormat PUNISHMENT_DISCORD_WARNED = get("punishment.discord-warned");
  public static LocalizedFormat PUNISHMENT_DISCORD_KICKED = get("punishment.discord-kicked");
  public static LocalizedFormat PUNISHMENT_DISCORD_TEMPBANNED = get(
      "punishment.discord-tempbanned");
  public static LocalizedFormat PUNISHMENT_DISCORD_BANNED = get("punishment.discord-banned");
  public static LocalizedFormat PUNISHMENT_SUCCESS = get("punishment.success");
  public static LocalizedFormat PUNISHMENT_IP_BAN = get("punishment.ip-ban");
  public static LocalizedText SETTINGS_PUNISHMENT_ALERT = get("settings.punishment-alert").with();
  public static LocalizedText SETTINGS_PUNISHMENT_ALERT_SUMMARY = get(
      "settings.punishment-alert-summary").with();

  public static LocalizedText SETTINGS_SHOW_TRAILS = get("settings.show-trails").with();
  public static LocalizedText SETTINGS_SHOW_TRAILS_SUMMARY = get("settings.show-trails-summary")
      .with();
  public static LocalizedText SETTINGS_SHOW_ARROW_TRAILS = get("settings.show-arrow-trails").with();
  public static LocalizedText SETTINGS_SHOW_ARROW_TRAILS_SUMMARY = get(
      "settings.show-arrow-trails-summary").with();
  public static LocalizedText PRIVATE_MESSAGES = get("settings.private-messages").with();
  public static LocalizedText PRIVATE_MESSAGES_SUMMARY = get("settings.private-messages-summary")
      .with();
  public static LocalizedText SETTINGS_ACHIEVEMENTS = get("settings.achievements").with();
  public static LocalizedText SETTINGS_ACHIEVEMENTS_SUMMARY = get(
      "settings.achievements-summary").with();

  public static Localizable enabledOrDisabled(boolean value) {
    ChatColor color = value ? ChatColor.GREEN : ChatColor.RED;
    LocalizableFormat format = value ? UI_ENABLED : UI_DISABLED;
    return format.with(color);
  }

  private static LocalizedFormat get(String key) {
    return Hook.locales().getFormat(key);
  }
}
