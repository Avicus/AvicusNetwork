package net.avicus.atlas.util;

import com.google.common.base.Joiner;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.locale.text.LocalizedFormat;
import net.avicus.compendium.locale.text.LocalizedText;
import org.bukkit.DyeColor;

public class Messages {

  private static final Joiner JOINER = Joiner.on(".");
  // Console
  public static final LocalizedFormat CONSOLE_BOOT = get("console", "boot");
  public static final LocalizedFormat CONSOLE_SHUTDOWN = get("console", "shutdown");

  // Errors
  public static final LocalizedFormat ERROR_MATCH_MISSING = get("error", "match-missing");

  public static final LocalizedFormat ERROR_CHAT_GLOBAL_DISABLED = get("error",
      "chat-global-disabled");
  public static final LocalizedFormat ERROR_CHAT_TEAM_DISABLED = get("error", "chat-team-disabled");
  public static final LocalizedFormat ERROR_COMMAND_NOT_ENABLED = get("error",
      "command-not-enabled");
  public static final LocalizedFormat ERROR_CANNOT_TUTORIAL = get("error", "cannot-tutorial");
  public static final LocalizedFormat ERROR_CANNOT_JOIN_OVERFILL = get("error",
      "cannot-join-overfill");
  public static final LocalizedFormat ERROR_CANNOT_JOIN_IMBALANCE = get("error",
      "cannot-join-imbalance");
  public static final LocalizedFormat ERROR_CANNOT_JOIN_FULL = get("error", "cannot-join-full");
  public static final LocalizedFormat ERROR_CANNOT_PICK_TEAM = get("error", "cannot-pick-team");
  public static final LocalizedFormat ERROR_CANNOT_JOIN_ONGOING = get("error",
      "cannot-join-ongoing");
  public static final LocalizedFormat ERROR_CANNOT_JOIN_CYCLING = get("error",
      "cannot-join-cycling");
  public static final LocalizedFormat ERROR_ERROR_OCCURRED = get("error", "error-occurred");
  public static final LocalizedFormat ERROR_TEAM_NOT_FOUND = get("error", "team-not-found");
  public static final LocalizedFormat ERROR_ALREADY_TEAM = get("error", "already-team");
  public static final LocalizedFormat ERROR_ALREADY_SPECTATOR = get("error", "already-spectator");
  public static final LocalizedFormat ERROR_ALREADY_PLAYING = get("error", "already-playing");
  public static final LocalizedFormat ERROR_OBJECTIVE_DAMAGE = get("error", "objective-damage");
  public static final LocalizedFormat ERROR_OBJECTIVE_DAMAGE_OWN = get("error",
      "objective-damage-own");
  public static final LocalizedFormat ERROR_OBJECTIVE_REPAIR_ENEMY = get("error",
      "objective-repair-enemy");
  public static final LocalizedFormat ERROR_OBJECTIVE_BAD_REPAIR = get("error",
      "objective-bad-repair");
  public static final LocalizedFormat ERROR_OBJECTIVE_CANNOT_REPAIR = get("error",
      "objective-cannot-repair");
  public static final LocalizedFormat ERROR_OBJECTIVE_OTHER_WOOL = get("error",
      "objective-other-wool");
  public static final LocalizedFormat ERROR_OBJECTIVE_PLACED_WOOL = get("error",
      "objective-placed-wool");
  public static final LocalizedFormat ERROR_OBJECTIVE_BAD_WOOL = get("error", "objective-bad-wool");
  public static final LocalizedFormat ERROR_OBJECTIVE_BREAK_POST = get("error",
      "objective-break-post");
  public static final LocalizedFormat ERROR_CANNOT_CRAFT = get("error", "cannot-craft");
  public static final LocalizedFormat ERROR_CANNOT_BUILD = get("error", "cannot-build");
  public static final LocalizedFormat ERROR_BUILD_PLAYABLE = get("error", "build-playable");
  public static final LocalizedFormat ERROR_COOLDOWN = get("error", "cooldown");
  public static final LocalizedFormat ERROR_KITS_NOT_ENABLED = get("error", "kits-not-enabled");
  public static final LocalizedFormat ERROR_KITS_NOT_ENABLED_PLAYER = get("error",
      "kits-not-enabled-player");

  public static final LocalizedFormat ERROR_NO_STATES = get("error", "no-states");
  public static final LocalizedFormat ERROR_NOT_PLAYING = get("error", "not-playing");
  public static final LocalizedFormat ERROR_TEAM_INVALID = get("error", "team-invalid");
  public static final LocalizedFormat ERROR_NOT_STARTING = get("error", "not-starting");
  public static final LocalizedFormat ERROR_NO_MATCHES = get("error", "no-matches");
  public static final LocalizedFormat ERROR_CYCLE_FAILED = get("error", "cycle-failed");
  public static final LocalizedFormat ERROR_CYCLE_WHILE_PLAYING = get("error",
      "cycle-while-playing");

  public static final LocalizedFormat ERROR_NO_LIBRARY = get("error", "no-library");
  public static final LocalizedFormat ERROR_CANNOT_READ = get("error", "cannot-read");
  public static final LocalizedFormat ERROR_NO_ROTATION = get("error", "no-rotation");
  public static final LocalizedFormat ERROR_MAP_NOT_FOUND = get("error", "map-not-found");
  public static final LocalizedFormat ERROR_PARSING_FAILED = get("error", "parsing-failed");
  public static final LocalizedFormat ERROR_NO_MAPS = get("error", "no-maps");
  public static final LocalizedFormat ERROR_ROT_FAILED = get("error", "rot-failed");
  public static final LocalizedFormat ERROR_BAD_SLOT = get("error", "bad-slot");
  public static final LocalizedFormat ERROR_ACCEPT_RESOURCE_PACK = get("error",
      "accept-resource-pack");
  public static final LocalizedFormat ERROR_NO_RESOURCE_PACK = get("error", "no-resource-pack");
  public static final LocalizedFormat ERROR_RESOURCE_PACK_DECLINED = get("error",
      "resource-pack-declined");
  public static final LocalizedFormat ERROR_KIT_NOT_FOUND = get("error", "kit-not-found");
  public static final LocalizedFormat ERROR_NO_KIT = get("error", "no-kit");
  public static final LocalizedFormat ERROR_CANNOT_RENAME = get("error", "cannot-rename");

  public static final LocalizedFormat GENERIC_CHAT_GLOBAL_ENABLED = get("generic",
      "chat-global-enabled");
  public static final LocalizedFormat GENERIC_CHAT_TEAM_ENABLED = get("generic",
      "chat-team-enabled");
  public static final LocalizedFormat GENERIC_VERSION = get("generic", "version");
  public static final LocalizedFormat GENERIC_LIBRARIES_RELOADED = get("generic",
      "libraries-reloaded");
  public static final LocalizedFormat GENERIC_WAITING_FOR_MORE = get("generic", "waiting-for-more");
  public static final LocalizedFormat GENERIC_MORE_PLAYERS_NEEDED = get("generic",
      "more-players-needed");
  public static final LocalizedFormat GENERIC_BALANCE_NEEDED = get("generic", "balance-needed");
  public static final LocalizedFormat GENERIC_JOINED = get("generic", "joined");
  public static final LocalizedFormat GENERIC_NO_LIVES = get("generic", "no-lives");
  public static final LocalizedFormat GENERIC_LIVES = get("generic", "lives");
  public static final LocalizedFormat GENERIC_LIVES_PLURAL = get("generic", "lives-plural");
  public static final LocalizedFormat GENERIC_STATE_CHANGE = get("generic", "state-change");
  public static final LocalizedFormat GENERIC_AUTO_RESPAWN = get("generic", "auto-respawn");
  public static final LocalizedFormat GENERIC_RESPAWN = get("generic", "respawn");
  public static final LocalizedFormat GENERIC_PUNCH_RESPAWN = get("generic", "punch-respawn");
  public static final LocalizedFormat GENERIC_DEATH = get("generic", "death");
  public static final LocalizedFormat GENERIC_DUMP = get("generic", "dump");
  public static final LocalizedFormat GENERIC_NEXT_MAP = get("generic", "next-map");
  public static final LocalizedFormat GENERIC_ROT_NEXT = get("generic", "rot-next");
  public static final LocalizedFormat GENERIC_ROT_INSERT = get("generic", "rot-insert");
  public static final LocalizedFormat GENERIC_ROT_APPEND = get("generic", "rot-append");
  public static final LocalizedFormat GENERIC_ROT_REMOVE = get("generic", "rot-remove");
  public static final LocalizedFormat GENERIC_ROT_SET = get("generic", "rot-set");
  public static final LocalizedFormat GENERIC_RESOURCE_PACK_ACCEPTED = get("generic",
      "resource-pack-accepted");
  public static final LocalizedFormat GENERIC_OBJECTIVE_DAMAGED = get("generic",
      "objective-damaged");
  public static final LocalizedFormat GENERIC_OBJECTIVE_REPAIRED = get("generic",
      "objective-repaired");
  public static final LocalizedFormat GENERIC_OBJECTIVE_COMPLETED = get("generic",
      "objective-completed");
  public static final LocalizedFormat GENERIC_OBJECTIVE_COMPLETED_BY = get("generic",
      "objective-completed-by");
  public static final LocalizedFormat GENERIC_LEAKABLE_LEAKED = get("generic", "leakable-leaked");
  public static final LocalizedFormat GENERIC_LEAKABLE_LEAKED_BY = get("generic",
      "leakable-leaked-by");
  public static final LocalizedFormat GENERIC_OBJECTIVE_CAPTURED = get("generic",
      "objective-captured");
  public static final LocalizedFormat GENERIC_OBJECTIVE_PLACED = get("generic", "objective-placed");
  public static final LocalizedFormat GENERIC_OBJECTIVE_TOUCHED = get("generic",
      "objective-touched");
  public static final LocalizedFormat GENERIC_OBJECTIVE_SCORED = get("generic", "objective-scored");
  public static final LocalizedFormat GENERIC_OBJECTIVE_SCORED_PLURAL = get("generic",
      "objective-scored-plural");
  public static final LocalizedFormat GENERIC_OBJECTIVE_TAKEN = get("generic", "objective-taken");
  public static final LocalizedFormat GENERIC_OBJECTIVE_PICKUP = get("generic", "objective-pickup");
  public static final LocalizedFormat GENERIC_OBJECTIVE_RESPAWN = get("generic",
      "objective-respawn");
  public static final LocalizedFormat GENERIC_OBJECTIVE_RESPAWN_PLURAL = get("generic",
      "objective-respawn-plural");
  public static final LocalizedFormat GENERIC_OBJECTIVE_RESPAWNED = get("generic",
      "objective-respawned");
  public static final LocalizedFormat GENERIC_OBJECTIVE_RECOVER = get("generic",
      "objective-recover");
  public static final LocalizedFormat GENERIC_OBJECTIVE_RECOVER_PLURAL = get("generic",
      "objective-recover-plural");
  public static final LocalizedFormat GENERIC_OBJECTIVE_RECOVERED = get("generic",
      "objective-recovered");
  public static final LocalizedFormat GENERIC_OBJECTIVE_DROPPED = get("generic",
      "objective-dropped");
  public static final LocalizedFormat GENERIC_KIT_SELECTED = get("generic", "kit-selected");
  public static final LocalizedFormat GENERIC_GROUP_RENAMED = get("generic", "group-renamed");
  public static final LocalizedFormat GENERIC_COUNTDOWN_AUTO_STARTING_NAME = get("generic",
      "countdown", "auto-starting", "name");
  public static final LocalizedFormat GENERIC_COUNTDOWN_STARTING_NAME = get("generic", "countdown",
      "starting", "name");
  public static final LocalizedFormat GENERIC_COUNTDOWN_CYCLING_NAME = get("generic", "countdown",
      "cycling", "name");
  public static final LocalizedFormat GENERIC_COUNTDOWN_END_NAME = get("generic", "countdown",
      "end", "name");
  public static final LocalizedFormat GENERIC_COUNTDOWN_CHEST_REGENERATE_NAME = get("generic",
      "countdown", "chest-regenerate", "name");
  public static final LocalizedFormat GENERIC_COUNTDOWN_PHASE_APPLY_NAME = get("generic",
      "countdown", "phase-apply", "name");
  public static final LocalizedFormat GENERIC_COUNTDOWN_FLAG_RECOVER_NAME = get("generic",
      "countdown", "flag-recover", "name");
  public static final LocalizedFormat GENERIC_WALLS_FALL_WILL = get("generic", "walls", "fall",
      "will");
  public static final LocalizedFormat GENERIC_WALLS_FALL_FELL = get("generic", "walls", "fall",
      "fell");

  public static final LocalizedFormat MATCH_STARTING = get("match", "starting");
  public static final LocalizedFormat MATCH_STARTING_PLURAL = get("match", "starting-plural");
  public static final LocalizedFormat MATCH_STARTED = get("match", "started");
  public static final LocalizedFormat MATCH_TIME_REMAINING = get("match", "time-remaining");
  public static final LocalizedFormat MATCH_ENDED = get("match", "ended");
  public static final LocalizedFormat MATCH_CYCLING = get("match", "cycling");
  public static final LocalizedFormat MATCH_CYCLING_PLURAL = get("match", "cycling-plural");
  public static final LocalizedFormat MATCH_CYCLED = get("match", "cycled");

  public static final LocalizedFormat UI_JOIN_TEAM = get("ui", "join-team");
  public static final LocalizedFormat UI_TELEPORT_DEVICE = get("ui", "teleport-device");
  public static final LocalizedFormat UI_TELEPORT_DEVICE_TEXT = get("ui", "teleport-device-text");
  public static final LocalizedFormat UI_PLAYERS = get("ui", "players");
  public static final LocalizedFormat UI_SPECTATORS = get("ui", "spectators");
  public static final LocalizedFormat UI_OBJECTIVES = get("ui", "objectives");
  public static final LocalizedFormat UI_HILLS = get("ui", "hills");
  public static final LocalizedFormat UI_MONUMENTS = get("ui", "monuments");
  public static final LocalizedFormat UI_LEAKABLES = get("ui", "leakables");
  public static final LocalizedFormat UI_POINTS = get("ui", "points");
  public static final LocalizedFormat UI_COLORED_WOOL = get("ui", "colored-wool");
  public static final LocalizedFormat UI_WOOL = get("ui", "wool");
  public static final LocalizedFormat UI_WALLS = get("ui", "walls");
  public static final LocalizedFormat UI_FLAGS = get("ui", "flags");
  public static final LocalizedFormat UI_ENTITIES = get("ui", "entities");
  public static final LocalizedFormat UI_COLORED_FLAG = get("ui", "colored-flag");
  public static final LocalizedFormat UI_ROTATION = get("ui", "rotation");
  public static final LocalizedFormat UI_AUTHORS = get("ui", "authors");
  public static final LocalizedFormat UI_CONTRIBUTORS = get("ui", "contributors");
  public static final LocalizedFormat UI_TIPS = get("ui", "tips");
  public static final LocalizedFormat UI_MAPS = get("ui", "maps");
  public static final LocalizedFormat UI_MATCH = get("ui", "match");
  public static final LocalizedFormat UI_BY = get("ui", "by");
  public static final LocalizedFormat UI_CURRENTLY_PLAYING = get("ui", "currently-playing");
  public static final LocalizedFormat UI_WELCOME_LINE_1 = get("ui", "welcome", "line-1");
  public static final LocalizedFormat UI_WELCOME_LINE_2 = get("ui", "welcome", "line-2");
  public static final LocalizedFormat UI_WINS = get("ui", "wins");
  public static final LocalizedFormat UI_TEAM_LOST = get("ui", "team-lost");
  public static final LocalizedFormat UI_TEAM_WON = get("ui", "team-won");
  public static final LocalizedFormat UI_WINNERS = get("ui", "winners");
  public static final LocalizedFormat UI_TIE = get("ui", "tie");
  public static final LocalizedFormat UI_IMPORTANT = get("ui", "important");
  public static final LocalizedFormat UI_PLAYER_LOCATED = get("ui", "player-located");
  public static final LocalizedFormat UI_LOCATION_TARGETED = get("ui", "location-targeted");
  public static final LocalizedFormat UI_COMPASS = get("ui", "compass");
  public static final LocalizedFormat UI_FOOD_LEVEL = get("ui", "food-level");
  public static final LocalizedFormat UI_HEALTH = get("ui", "health");
  public static final LocalizedFormat UI_PLAYERS_REMAINING = get("ui", "players-remaining");
  public static final LocalizedFormat UI_PLAYERS_REMAINING_PLURAL = get("ui",
      "players-remaining-plural");
  public static final LocalizedFormat UI_NUM_PLAYERS = get("ui", "num-players");
  public static final LocalizedFormat UI_NUM_PLAYERS_PLURAL = get("ui", "num-players-plural");
  public static final LocalizedFormat UI_TEAM_MENU = get("ui", "team-menu");
  public static final LocalizedFormat UI_AUTO_JOIN = get("ui", "auto-join");
  public static final LocalizedFormat UI_AUTO_JOIN_TEXT = get("ui", "auto-join-text");
  public static final LocalizedFormat UI_PLAY = get("ui", "play");
  public static final LocalizedFormat UI_SPEC_JOIN_NEXT = get("ui", "spec-join-next");
  public static final LocalizedFormat UI_CURRENT_KIT = get("ui", "current-kit");
  public static final LocalizedFormat UI_KITS = get("ui", "kits");
  public static final LocalizedFormat UI_KIT_MENU = get("ui", "kit-menu");
  public static final LocalizedFormat UI_SHOP_MENU = get("ui", "shop", "menu");
  public static final LocalizedFormat UI_SHOP_POINTS = get("ui", "shop", "points");
  public static final LocalizedFormat UI_SHOP_FAIL = get("ui", "shop", "fail");
  public static final LocalizedFormat UI_SHOP_PRESTIGE = get("ui", "shop", "prestige-required");
  public static final LocalizedFormat UI_SHOP_PURCHASE_FAIL = get("ui", "shop", "purchase", "fail");
  public static final LocalizedFormat UI_SHOP_PURCHASE_SUCCESS = get("ui", "shop", "purchase",
      "success");

  public static final LocalizedFormat UI_VIEW_MAP_ON_WEBSITE = get("ui", "view-map-on-website");
  public static final LocalizedFormat UI_TUTORIAL_FORCE_TIME = get("ui", "tutorial", "force",
      "time");
  public static final LocalizedFormat UI_TUTORIAL_FORCE_SETTING = get("ui", "tutorial", "force",
      "setting");

  public static final LocalizedFormat UI_MVP_TITLE = get("ui", "mvp", "title");
  public static final LocalizedFormat UI_MVP_NOT_ONLINE = get("ui", "mvp", "not-online");
  public static final LocalizedFormat UI_MVP_TAG_1 = get("ui", "mvp", "tag-1");
  public static final LocalizedFormat UI_MVP_TAG_2 = get("ui", "mvp", "tag-2");
  public static final LocalizedFormat UI_MVP_TAG_3 = get("ui", "mvp", "tag-3");
  public static final LocalizedFormat UI_MVP_TAG_4 = get("ui", "mvp", "tag-4");
  public static final LocalizedFormat UI_MVP_TAG_5 = get("ui", "mvp", "tag-5");
  public static final LocalizedFormat UI_MVP_TAG_6 = get("ui", "mvp", "tag-6");
  public static final LocalizedFormat UI_MVP_TAG_7 = get("ui", "mvp", "tag-7");
  public static final LocalizedFormat UI_MVP_TAG_8 = get("ui", "mvp", "tag-8");

  public static final LocalizedFormat UI_ROUND = get("ui", "round");

  public static final LocalizedFormat UI_ARCADE = get("ui", "arcade");
  public static final LocalizedFormat UI_CARTS_SPAWNED = get("ui", "carts", "spawned");
  public static final LocalizedFormat UI_CARTS_PICK = get("ui", "carts", "pick");
  public static final LocalizedFormat UI_CARTS_FINAL = get("ui", "carts", "final");
  public static final LocalizedFormat UI_CARTS_ELIMINATED = get("ui", "carts", "eliminated");

  public static final Localizable SETTINGS_SPECTATOR_VIEW = get("settings", "spectator-view")
      .with();
  public static final Localizable SETTINGS_SPECTATOR_VIEW_SUMMARY = get("settings",
      "spectator-view-summary").with();
  public static final Localizable SETTINGS_SHOW_TUTORIAL = get("settings", "show-tutorial").with();
  public static final Localizable SETTINGS_SHOW_TUTORIAL_SUMMARY = get("settings",
      "show-tutorial-summary").with();

  public static final LocalizedFormat DEATH_BLOCKS = get("death", "blocks");
  public static final LocalizedFormat DEATH_DIED = get("death", "died");
  public static final LocalizedFormat DEATH_BY_PLAYER_MOB = get("death", "by-player-mob");
  public static final LocalizedFormat DEATH_BY_MOB = get("death", "by-mob");
  public static final LocalizedFormat DEATH_BY_PLAYER_ANVIL = get("death", "by-player-anvil");
  public static final LocalizedFormat DEATH_BY_ANVIL = get("death", "by-anvil");
  public static final LocalizedFormat DEATH_BY_BLOCK = get("death", "by-block");
  public static final LocalizedFormat DEATH_BY_PLAYER_TNT = get("death", "by-player-tnt");
  public static final LocalizedFormat DEATH_BY_EXPLOSIVE = get("death", "by-explosive");
  public static final LocalizedFormat DEATH_BY_PLAYER_VOID = get("death", "by-player-void");
  public static final LocalizedFormat DEATH_BY_VOID = get("death", "by-void");
  public static final LocalizedFormat DEATH_BY_PLAYER_PROJECTILE = get("death",
      "by-player-projectile");
  public static final LocalizedFormat DEATH_BY_PROJECTILE = get("death", "by-projectile");
  public static final LocalizedFormat DEATH_BY_LAVA = get("death", "by-lava");
  public static final LocalizedFormat DEATH_BY_MELEE_FISTS = get("death", "by-melee-fists");
  public static final LocalizedFormat DEATH_BY_MELEE = get("death", "by-melee");
  public static final LocalizedFormat DEATH_BY_FALL = get("death", "by-fall");
  public static final LocalizedFormat DEATH_HIT_FLOOR_VOID = get("death", "hit-floor-void");
  public static final LocalizedFormat DEATH_HIT_FLOOR_FALL = get("death", "hit-floor-fall");
  public static final LocalizedFormat DEATH_HIT_LADDER_VOID = get("death", "hit-ladder-void");
  public static final LocalizedFormat DEATH_HIT_LADDER_FALL = get("death", "hit-ladder-fall");
  public static final LocalizedFormat DEATH_HIT_WATER_VOID = get("death", "hit-water-void");
  public static final LocalizedFormat DEATH_HIT_WATER_FALL = get("death", "hit-water-fall");
  public static final LocalizedFormat DEATH_SHOT_FLOOR_VOID = get("death", "shot-floor-void");
  public static final LocalizedFormat DEATH_SHOT_FLOOR_FALL = get("death", "shot-floor-fall");
  public static final LocalizedFormat DEATH_SHOT_LADDER_VOID = get("death", "shot-ladder-void");
  public static final LocalizedFormat DEATH_SHOT_LADDER_FALL = get("death", "shot-ladder-fall");
  public static final LocalizedFormat DEATH_SHOT_WATER_VOID = get("death", "shot-water-void");
  public static final LocalizedFormat DEATH_SHOT_WATER_FALL = get("death", "shot-water-fall");
  public static final LocalizedFormat DEATH_SPLEEF_FLOOR_VOID = get("death", "spleef-floor-void");
  public static final LocalizedFormat DEATH_SPLEEF_FLOOR_FALL = get("death", "spleef-floor-fall");
  public static final LocalizedFormat DEATH_SPLEEF_BY_PLAYER = get("death", "spleef-by-player");

  public static final LocalizedFormat VOTE_TITLE = get("vote", "title");
  public static final LocalizedFormat VOTE_DISABLED = get("vote", "disabled");
  public static final LocalizedFormat VOTE_SET = get("vote", "set");
  public static final LocalizedFormat VOTE_DELAY = get("vote", "delay");
  public static final LocalizedFormat VOTE_CANCELLED = get("vote", "cancelled");
  public static final LocalizedFormat VOTE_CANCELNONE = get("vote", "cancel-none");
  public static final LocalizedFormat VOTE_INVALID = get("vote", "invalid");
  public static final LocalizedFormat VOTE_SUCCESS = get("vote", "success");
  public static final LocalizedFormat VOTE_START = get("vote", "start");
  public static final LocalizedFormat VOTE_COUNTDOWN_TIME = get("vote", "countdown", "time");
  public static final LocalizedFormat VOTE_VOTES = get("vote", "votes");
  public static final LocalizedFormat VOTE_WON = get("vote", "won");
  public static final LocalizedFormat VOTE_NONE = get("vote", "none");
  public static final LocalizedText SETTINGS_HUB_RESTART = get("settings.hub-restart").with();
  public static final LocalizedText SETTINGS_HUB_RESTART_SUMMARY = get(
      "settings.hub-restart-summary").with();
  public static final LocalizedFormat GENERIC_SENT_BY_RESTART = get("generic.sent-by-restart");
  public static LocalizedFormat GENERIC_REMOTE_MATCH_WAITING_PLURAL = get(
      "generic.remote-match-waiting-plural");
  public static LocalizedFormat GENERIC_REMOTE_MATCH_WAITING = get("generic.remote-match-waiting");
  public static LocalizedFormat GENERIC_REMOTE_MATCH_PLURAL = get("generic.remote-match-plural");
  public static LocalizedFormat GENERIC_REMOTE_MATCH = get("generic.remote-match");
  public static LocalizedFormat GENERIC_LOCAL_MATCH = get("generic.local-match");

  public static LocalizedFormat forWoolColor(DyeColor color) {
    return get("wool-color", color.name().toLowerCase().replace("_", "-"));
  }

  public static LocalizedFormat forFlagColor(DyeColor color) {
    return get("flag-color", color.name().toLowerCase().replace("_", "-"));
  }

  protected static LocalizedFormat get(String... path) {
    return Translations.BUNDLE.getFormat(JOINER.join(path));
  }
}
