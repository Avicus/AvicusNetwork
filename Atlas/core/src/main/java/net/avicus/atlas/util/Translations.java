package net.avicus.atlas.util;


import net.avicus.atlas.AtlasConfig;
import net.avicus.compendium.locale.LocaleBundle;
import net.avicus.compendium.locale.TranslationProvider;
import net.avicus.compendium.locale.text.LocalizedFormat;
import net.avicus.compendium.locale.text.LocalizedText;
import org.bukkit.ChatColor;

public final class Translations {

  // ----
  public static final LocalizedFormat COMMANDS_GENERIC_PING_SELF;
  public static final LocalizedFormat COMMANDS_GENERIC_PING_OTHER;
  public static final LocalizedFormat COMMANDS_GROUP_MAX_SUCCESS;
  public static final LocalizedFormat GAMETYPE_NAME_PLURAL;
  public static final LocalizedFormat GAMETYPE_NAME_SINGULAR;
  public static final LocalizedFormat GAMETYPE_CTW_NAME;
  public static final LocalizedFormat GAMETYPE_DTC_NAME;
  public static final LocalizedFormat GAMETYPE_DTM_NAME;
  public static final LocalizedFormat GAMETYPE_ELIMINATION_NAME;
  public static final LocalizedFormat GAMETYPE_HILL_NAME;
  public static final LocalizedFormat GAMETYPE_LCS_NAME;
  public static final LocalizedFormat GAMETYPE_LTS_NAME;
  public static final LocalizedFormat GAMETYPE_SCORE_NAME;
  public static final LocalizedFormat GAMETYPE_WALLS_NAME;
  public static final LocalizedFormat ERROR_UNKNOWN_PLAYER;
  public static final LocalizedFormat ERROR_PERMISSION_MODTIME;
  public static final LocalizedFormat MODULE_OBSERVER_MENU_TITLE;
  public static final LocalizedFormat MODULE_OBSERVER_MENU_COMMAND;
  public static final LocalizedFormat MODULE_OBSERVER_MENU_CURRENT;
  public static final LocalizedFormat MODULE_OBSERVER_MENU_ITEM_GAMEMODE_NAME;
  public static final LocalizedFormat MODULE_OBSERVER_MENU_ITEM_GAMEMODE_DESCRIPTION;
  public static final LocalizedFormat MODULE_OBSERVER_MENU_ITEM_NIGHTVISION_NAME;
  public static final LocalizedFormat MODULE_OBSERVER_MENU_ITEM_NIGHTVISION_DESCRIPTION;
  public static final LocalizedFormat MODULE_OBSERVER_MENU_ERROR_OPEN_PARTICIPANT;
  public static final LocalizedFormat NETWORK_PROTOCOL_UNSUPPORTED_GAMEMODE_CTF_SEVEN;
  public static final LocalizedFormat SETTING_DEATHMESSAGE_NAME;
  public static final LocalizedFormat SETTING_DEATHMESSAGE_DESCRIPTION;
  public static final LocalizedFormat SETTING_VOTESHOW_NAME;
  public static final LocalizedFormat SETTING_VOTESHOW_DESCRIPTION;
  public static final LocalizedFormat TYPE_BOOLEAN_TRUE;
  public static final LocalizedFormat TYPE_BOOLEAN_FALSE;
  public static final LocalizedFormat VISUAL_EFFECT_BLOOD_SETTING_NAME;
  public static final LocalizedFormat VISUAL_EFFECT_BLOOD_SETTING_DESCRIPTION;

  public static final LocalizedFormat STATS_FACTS_RANDOM;
  public static final LocalizedFormat STATS_FACTS_COMMAND;
  public static final LocalizedFormat STATS_FACTS_ERROR_AFTER;
  public static final LocalizedFormat STATS_FACTS_ERROR_NONE;
  public static final LocalizedFormat STATS_FACTS_ERROR_DISABLED;

  public static final LocalizedFormat STATS_FACTS_ALL;
  public static final LocalizedFormat STATS_FACTS_FALL_TEXT;
  public static final LocalizedFormat STATS_FACTS_FALL_TAGLINE_TAG1;
  public static final LocalizedFormat STATS_FACTS_FALL_TAGLINE_TAG2;
  public static final LocalizedFormat STATS_FACTS_FALL_TAGLINE_TAG3;
  public static final LocalizedFormat STATS_FACTS_KILLS_MOST_TEXT;
  public static final LocalizedFormat STATS_FACTS_KILLS_MOST_TAGLINE_TAG1;
  public static final LocalizedFormat STATS_FACTS_KILLS_MOST_TAGLINE_TAG2;
  public static final LocalizedFormat STATS_FACTS_KILLS_MOST_TAGLINE_TAG3;
  public static final LocalizedFormat STATS_FACTS_KILLS_MOST_TAGLINE_TAG4;
  public static final LocalizedFormat STATS_FACTS_KILLS_WEAPON_MOST;
  public static final LocalizedFormat STATS_FACTS_KILLS_ASSISTS_HIGHEST;
  public static final LocalizedFormat STATS_FACTS_KILLS_ASSISTS_TAGLINE_TAG1;
  public static final LocalizedFormat STATS_FACTS_KILLS_ASSISTS_TAGLINE_TAG2;
  public static final LocalizedFormat STATS_FACTS_KILLS_ASSISTS_TAGLINE_TAG3;
  public static final LocalizedFormat STATS_FACTS_SNIPE_TEXT;
  public static final LocalizedFormat STATS_FACTS_SNIPE_TAGLINE_TAG1;
  public static final LocalizedFormat STATS_FACTS_SNIPE_TAGLINE_TAG2;
  public static final LocalizedFormat STATS_FACTS_SNIPE_TAGLINE_TAG3;
  public static final LocalizedFormat STATS_FACTS_SNIPE_TAGLINE_TAG4;

  public static final LocalizedFormat STATS_RECAP_MATCH;
  public static final LocalizedFormat STATS_RECAP_LIFE;
  public static final LocalizedFormat STATS_DAMAGE_KILLS_KILLS;
  public static final LocalizedFormat STATS_DAMAGE_KILLS_CAUSE_WEAPON;
  public static final LocalizedFormat STATS_DAMAGE_KILLS_CAUSE_BOW;
  public static final LocalizedFormat STATS_DAMAGE_KILLS_MOST;
  public static final LocalizedFormat STATS_DAMAGE_DEATHS_CATEGORY_PLAYER;
  public static final LocalizedFormat STATS_DAMAGE_DEATHS_CATEGORY_NATURAL;
  public static final LocalizedFormat STATS_DAMAGE_DEATHS_CATEGORY_SELF;
  public static final LocalizedFormat STATS_DAMAGE_DEATHS_CAUSE_WEAPON;
  public static final LocalizedFormat STATS_DAMAGE_DEATHS_CAUSE_BOW;
  public static final LocalizedFormat STATS_DAMAGE_DEATHS_CAUSE_FALL_MOST;
  public static final LocalizedFormat STATS_DAMAGE_DEATHS_CAUSE_FALL_TOTAL;
  public static final LocalizedFormat STATS_DAMAGE_ASSIST_ASSISTS;
  public static final LocalizedFormat STATS_DAMAGE_ASSIST_WEAPON;

  public static final LocalizedFormat STATS_OBJECTIVES_SUMMARY;
  public static final LocalizedFormat STATS_OBJECTIVES_DESTROYABLES_DAMAGED;
  public static final LocalizedFormat STATS_OBJECTIVES_DESTROYABLES_DAMAGEDPLURAL;
  public static final LocalizedFormat STATS_OBJECTIVES_DESTROYABLES_REPAIRED;
  public static final LocalizedFormat STATS_OBJECTIVES_DESTROYABLES_REPAIREDPLURAL;
  public static final LocalizedFormat STATS_OBJECTIVES_LEAKABLES_LEAKED;
  public static final LocalizedFormat STATS_OBJECTIVES_MONUMENTS_DESTROYED;
  public static final LocalizedFormat STATS_OBJECTIVES_FLAGS_CAPTURED;
  public static final LocalizedFormat STATS_OBJECTIVES_FLAGS_CAPTUREDPLURAL;
  public static final LocalizedFormat STATS_OBJECTIVES_FLAGS_DROPPED;
  public static final LocalizedFormat STATS_OBJECTIVES_FLAGS_DROPPEDPLURAL;
  public static final LocalizedFormat STATS_OBJECTIVES_FLAGS_PICKED;
  public static final LocalizedFormat STATS_OBJECTIVES_FLAGS_PICKEDPLURAL;
  public static final LocalizedFormat STATS_OBJECTIVES_HILLS_CAPTURE;
  public static final LocalizedFormat STATS_OBJECTIVES_HILLS_CAPTUREPLURAL;
  public static final LocalizedFormat STATS_OBJECTIVES_HILLS_CAPTUREASSIST;
  public static final LocalizedFormat STATS_OBJECTIVES_HILLS_CAPTUREASSISTPLURAL;
  public static final LocalizedFormat STATS_OBJECTIVES_POINTS_EARNEDPLURAL;
  public static final LocalizedFormat STATS_OBJECTIVES_POINTS_EARNED;
  public static final LocalizedFormat STATS_OBJECTIVES_WOOLS_PLACED;
  public static final LocalizedFormat STATS_OBJECTIVES_WOOLS_PLACEDPLURAL;
  public static final LocalizedFormat STATS_OBJECTIVES_WOOLS_TOUCHEDPLURAL;
  public static final LocalizedFormat STATS_OBJECTIVES_WOOLS_TOUCHED;
  static final LocaleBundle BUNDLE;

  static {
    BUNDLE = TranslationProvider.loadBundle(AtlasConfig.localesPath, "en_US");
    COMMANDS_GENERIC_PING_SELF = BUNDLE.getFormat("commands.generic.ping.self");
    COMMANDS_GENERIC_PING_OTHER = BUNDLE.getFormat("commands.generic.ping.other");
    COMMANDS_GROUP_MAX_SUCCESS = BUNDLE.getFormat("commands.group.max.success");
    GAMETYPE_NAME_PLURAL = BUNDLE.getFormat("gametype.name.plural");
    GAMETYPE_NAME_SINGULAR = BUNDLE.getFormat("gametype.name.singular");
    GAMETYPE_CTW_NAME = BUNDLE.getFormat("gametype.ctw.name");
    GAMETYPE_DTC_NAME = BUNDLE.getFormat("gametype.dtc.name");
    GAMETYPE_DTM_NAME = BUNDLE.getFormat("gametype.dtm.name");
    GAMETYPE_ELIMINATION_NAME = BUNDLE.getFormat("gametype.elimination.name");
    GAMETYPE_HILL_NAME = BUNDLE.getFormat("gametype.hill.name");
    GAMETYPE_LCS_NAME = BUNDLE.getFormat("gametype.lcs.name");
    GAMETYPE_LTS_NAME = BUNDLE.getFormat("gametype.lts.name");
    GAMETYPE_SCORE_NAME = BUNDLE.getFormat("gametype.score.name");
    GAMETYPE_WALLS_NAME = BUNDLE.getFormat("gametype.walls.name");
    ERROR_UNKNOWN_PLAYER = BUNDLE.getFormat("error.unknown-player");
    ERROR_PERMISSION_MODTIME = BUNDLE.getFormat("error.permission.modtime");
    MODULE_OBSERVER_MENU_TITLE = BUNDLE.getFormat("module.observer.menu.title");
    MODULE_OBSERVER_MENU_COMMAND = BUNDLE.getFormat("module.observer.menu.command");
    MODULE_OBSERVER_MENU_CURRENT = BUNDLE.getFormat("module.observer.menu.current");
    MODULE_OBSERVER_MENU_ITEM_GAMEMODE_NAME = BUNDLE.getFormat("module.observer.menu.item.gamemode.name");
    MODULE_OBSERVER_MENU_ITEM_GAMEMODE_DESCRIPTION = BUNDLE.getFormat("module.observer.menu.item.gamemode.description");
    MODULE_OBSERVER_MENU_ITEM_NIGHTVISION_NAME = BUNDLE.getFormat("module.observer.menu.item.nightvision.name");
    MODULE_OBSERVER_MENU_ITEM_NIGHTVISION_DESCRIPTION = BUNDLE.getFormat("module.observer.menu.item.nightvision.description");
    MODULE_OBSERVER_MENU_ERROR_OPEN_PARTICIPANT = BUNDLE.getFormat("module.observer.menu.error.open-participant");
    NETWORK_PROTOCOL_UNSUPPORTED_GAMEMODE_CTF_SEVEN = BUNDLE.getFormat("network.protocol.unsupported.gamemode.ctf-seven");
    SETTING_DEATHMESSAGE_NAME = BUNDLE.getFormat("setting.deathmessage.name");
    SETTING_DEATHMESSAGE_DESCRIPTION = BUNDLE.getFormat("setting.deathmessage.description");
    SETTING_VOTESHOW_NAME = BUNDLE.getFormat("setting.voteshow.name");
    SETTING_VOTESHOW_DESCRIPTION = BUNDLE.getFormat("setting.voteshow.description");
    TYPE_BOOLEAN_TRUE = BUNDLE.getFormat("type.boolean.true");
    TYPE_BOOLEAN_FALSE = BUNDLE.getFormat("type.boolean.false");
    VISUAL_EFFECT_BLOOD_SETTING_NAME = BUNDLE.getFormat("visual-effect.blood.setting.name");
    VISUAL_EFFECT_BLOOD_SETTING_DESCRIPTION = BUNDLE.getFormat("visual-effect.blood.setting.description");
    STATS_FACTS_RANDOM = BUNDLE.getFormat("stats.facts.random");
    STATS_FACTS_COMMAND = BUNDLE.getFormat("stats.facts.command");
    STATS_FACTS_ERROR_AFTER = BUNDLE.getFormat("stats.facts.error.after");
    STATS_FACTS_ERROR_NONE = BUNDLE.getFormat("stats.facts.error.none");
    STATS_FACTS_ERROR_DISABLED = BUNDLE.getFormat("stats.facts.error.disabled");
    STATS_FACTS_ALL = BUNDLE.getFormat("stats.facts.all");
    STATS_FACTS_FALL_TEXT = BUNDLE.getFormat("stats.facts.fall.text");
    STATS_FACTS_FALL_TAGLINE_TAG1 = BUNDLE.getFormat("stats.facts.fall.tagline.tag1");
    STATS_FACTS_FALL_TAGLINE_TAG2 = BUNDLE.getFormat("stats.facts.fall.tagline.tag2");
    STATS_FACTS_FALL_TAGLINE_TAG3 = BUNDLE.getFormat("stats.facts.fall.tagline.tag3");
    STATS_FACTS_KILLS_MOST_TEXT = BUNDLE.getFormat("stats.facts.kills.most.text");
    STATS_FACTS_KILLS_MOST_TAGLINE_TAG1 = BUNDLE.getFormat("stats.facts.kills.most.tagline.tag1");
    STATS_FACTS_KILLS_MOST_TAGLINE_TAG2 = BUNDLE.getFormat("stats.facts.kills.most.tagline.tag2");
    STATS_FACTS_KILLS_MOST_TAGLINE_TAG3 = BUNDLE.getFormat("stats.facts.kills.most.tagline.tag3");
    STATS_FACTS_KILLS_MOST_TAGLINE_TAG4 = BUNDLE.getFormat("stats.facts.kills.most.tagline.tag4");
    STATS_FACTS_KILLS_WEAPON_MOST = BUNDLE.getFormat("stats.facts.kills.weapon.most");
    STATS_FACTS_KILLS_ASSISTS_HIGHEST = BUNDLE.getFormat("stats.facts.kills.assists.highest");
    STATS_FACTS_KILLS_ASSISTS_TAGLINE_TAG1 = BUNDLE.getFormat("stats.facts.kills.assists.tagline.tag1");
    STATS_FACTS_KILLS_ASSISTS_TAGLINE_TAG2 = BUNDLE.getFormat("stats.facts.kills.assists.tagline.tag2");
    STATS_FACTS_KILLS_ASSISTS_TAGLINE_TAG3 = BUNDLE.getFormat("stats.facts.kills.assists.tagline.tag3");
    STATS_FACTS_SNIPE_TEXT = BUNDLE.getFormat("stats.facts.snipe.text");
    STATS_FACTS_SNIPE_TAGLINE_TAG1 = BUNDLE.getFormat("stats.facts.snipe.tagline.tag1");
    STATS_FACTS_SNIPE_TAGLINE_TAG2 = BUNDLE.getFormat("stats.facts.snipe.tagline.tag2");
    STATS_FACTS_SNIPE_TAGLINE_TAG3 = BUNDLE.getFormat("stats.facts.snipe.tagline.tag3");
    STATS_FACTS_SNIPE_TAGLINE_TAG4 = BUNDLE.getFormat("stats.facts.snipe.tagline.tag4");
    STATS_RECAP_MATCH = BUNDLE.getFormat("stats.recap.match");
    STATS_RECAP_LIFE = BUNDLE.getFormat("stats.recap.life");
    STATS_DAMAGE_KILLS_KILLS = BUNDLE.getFormat("stats.damage.kills.kills");
    STATS_DAMAGE_KILLS_CAUSE_WEAPON = BUNDLE.getFormat("stats.damage.kills.cause.weapon");
    STATS_DAMAGE_KILLS_CAUSE_BOW = BUNDLE.getFormat("stats.damage.kills.cause.bow");
    STATS_DAMAGE_KILLS_MOST = BUNDLE.getFormat("stats.damage.kills.most");
    STATS_DAMAGE_DEATHS_CATEGORY_PLAYER = BUNDLE.getFormat("stats.damage.deaths.category.player");
    STATS_DAMAGE_DEATHS_CATEGORY_NATURAL = BUNDLE.getFormat("stats.damage.deaths.category.natural");
    STATS_DAMAGE_DEATHS_CATEGORY_SELF = BUNDLE.getFormat("stats.damage.deaths.category.self");
    STATS_DAMAGE_DEATHS_CAUSE_WEAPON = BUNDLE.getFormat("stats.damage.deaths.cause.weapon");
    STATS_DAMAGE_DEATHS_CAUSE_BOW = BUNDLE.getFormat("stats.damage.deaths.cause.bow");
    STATS_DAMAGE_DEATHS_CAUSE_FALL_MOST = BUNDLE.getFormat("stats.damage.deaths.cause.fall.most");
    STATS_DAMAGE_DEATHS_CAUSE_FALL_TOTAL = BUNDLE.getFormat("stats.damage.deaths.cause.fall.total");
    STATS_DAMAGE_ASSIST_ASSISTS = BUNDLE.getFormat("stats.damage.assist.assists");
    STATS_DAMAGE_ASSIST_WEAPON = BUNDLE.getFormat("stats.damage.assist.weapon");
    STATS_OBJECTIVES_SUMMARY = BUNDLE.getFormat("stats.objectives.summary");
    STATS_OBJECTIVES_DESTROYABLES_DAMAGED = BUNDLE.getFormat("stats.objectives.destroyables.damaged");
    STATS_OBJECTIVES_DESTROYABLES_DAMAGEDPLURAL = BUNDLE.getFormat("stats.objectives.destroyables.damagedplural");
    STATS_OBJECTIVES_DESTROYABLES_REPAIRED = BUNDLE.getFormat("stats.objectives.destroyables.repaired");
    STATS_OBJECTIVES_DESTROYABLES_REPAIREDPLURAL = BUNDLE.getFormat("stats.objectives.destroyables.repairedplural");
    STATS_OBJECTIVES_LEAKABLES_LEAKED = BUNDLE.getFormat("stats.objectives.leakables.leaked");
    STATS_OBJECTIVES_MONUMENTS_DESTROYED = BUNDLE.getFormat("stats.objectives.monuments.destroyed");
    STATS_OBJECTIVES_FLAGS_CAPTURED = BUNDLE.getFormat("stats.objectives.flags.captured");
    STATS_OBJECTIVES_FLAGS_CAPTUREDPLURAL = BUNDLE.getFormat("stats.objectives.flags.capturedplural");
    STATS_OBJECTIVES_FLAGS_DROPPED = BUNDLE.getFormat("stats.objectives.flags.dropped");
    STATS_OBJECTIVES_FLAGS_DROPPEDPLURAL = BUNDLE.getFormat("stats.objectives.flags.droppedplural");
    STATS_OBJECTIVES_FLAGS_PICKED = BUNDLE.getFormat("stats.objectives.flags.picked");
    STATS_OBJECTIVES_FLAGS_PICKEDPLURAL = BUNDLE.getFormat("stats.objectives.flags.pickedplural");
    STATS_OBJECTIVES_HILLS_CAPTURE = BUNDLE.getFormat("stats.objectives.hills.capture");
    STATS_OBJECTIVES_HILLS_CAPTUREPLURAL = BUNDLE.getFormat("stats.objectives.hills.captureplural");
    STATS_OBJECTIVES_HILLS_CAPTUREASSIST = BUNDLE.getFormat("stats.objectives.hills.captureassist");
    STATS_OBJECTIVES_HILLS_CAPTUREASSISTPLURAL = BUNDLE.getFormat("stats.objectives.hills.captureassistplural");
    STATS_OBJECTIVES_POINTS_EARNEDPLURAL = BUNDLE.getFormat("stats.objectives.points.earnedplural");
    STATS_OBJECTIVES_POINTS_EARNED = BUNDLE.getFormat("stats.objectives.points.earned");
    STATS_OBJECTIVES_WOOLS_PLACED = BUNDLE.getFormat("stats.objectives.wools.placed");
    STATS_OBJECTIVES_WOOLS_PLACEDPLURAL = BUNDLE.getFormat("stats.objectives.wools.placedplural");
    STATS_OBJECTIVES_WOOLS_TOUCHEDPLURAL = BUNDLE.getFormat("stats.objectives.wools.touchedplural");
    STATS_OBJECTIVES_WOOLS_TOUCHED = BUNDLE.getFormat("stats.objectives.wools.touched");
  }

  private Translations() {
  }

  public static LocaleBundle getBundle() {
    return BUNDLE;
  }

  public static LocalizedText bool(final boolean value) {
    return value ? TYPE_BOOLEAN_TRUE.with(ChatColor.GREEN) : TYPE_BOOLEAN_FALSE.with(ChatColor.RED);
  }
}
