package net.avicus.atlas.util;


import static net.avicus.compendium.locale.TranslationProvider.$NULL$;

import net.avicus.atlas.Atlas;
import net.avicus.atlas.AtlasConfig;
import net.avicus.compendium.locale.LocaleBundle;
import net.avicus.compendium.locale.TranslationProvider;
import net.avicus.compendium.locale.text.LocalizedFormat;
import net.avicus.compendium.locale.text.LocalizedText;
import org.bukkit.ChatColor;

public final class Translations {

  // ----
  public static final LocalizedFormat COMMANDS_GENERIC_PING_SELF = $NULL$;
  public static final LocalizedFormat COMMANDS_GENERIC_PING_OTHER = $NULL$;
  public static final LocalizedFormat COMMANDS_GROUP_MAX_SUCCESS = $NULL$;
  public static final LocalizedFormat GAMETYPE_NAME_PLURAL = $NULL$;
  public static final LocalizedFormat GAMETYPE_NAME_SINGULAR = $NULL$;
  public static final LocalizedFormat GAMETYPE_CTW_NAME = $NULL$;
  public static final LocalizedFormat GAMETYPE_DTC_NAME = $NULL$;
  public static final LocalizedFormat GAMETYPE_DTM_NAME = $NULL$;
  public static final LocalizedFormat GAMETYPE_ELIMINATION_NAME = $NULL$;
  public static final LocalizedFormat GAMETYPE_HILL_NAME = $NULL$;
  public static final LocalizedFormat GAMETYPE_LCS_NAME = $NULL$;
  public static final LocalizedFormat GAMETYPE_LTS_NAME = $NULL$;
  public static final LocalizedFormat GAMETYPE_SCORE_NAME = $NULL$;
  public static final LocalizedFormat GAMETYPE_WALLS_NAME = $NULL$;
  public static final LocalizedFormat ERROR_UNKNOWN_PLAYER = $NULL$;
  public static final LocalizedFormat ERROR_PERMISSION_MODTIME = $NULL$;
  public static final LocalizedFormat MODULE_OBSERVER_MENU_TITLE = $NULL$;
  public static final LocalizedFormat MODULE_OBSERVER_MENU_COMMAND = $NULL$;
  public static final LocalizedFormat MODULE_OBSERVER_MENU_CURRENT = $NULL$;
  public static final LocalizedFormat MODULE_OBSERVER_MENU_ITEM_GAMEMODE_NAME = $NULL$;
  public static final LocalizedFormat MODULE_OBSERVER_MENU_ITEM_GAMEMODE_DESCRIPTION = $NULL$;
  public static final LocalizedFormat MODULE_OBSERVER_MENU_ITEM_NIGHTVISION_NAME = $NULL$;
  public static final LocalizedFormat MODULE_OBSERVER_MENU_ITEM_NIGHTVISION_DESCRIPTION = $NULL$;
  public static final LocalizedFormat MODULE_OBSERVER_MENU_ERROR_OPEN_PARTICIPANT = $NULL$;
  public static final LocalizedFormat NETWORK_PROTOCOL_UNSUPPORTED_GAMEMODE_CTF_SEVEN = $NULL$;
  public static final LocalizedFormat SETTING_DEATHMESSAGE_NAME = $NULL$;
  public static final LocalizedFormat SETTING_DEATHMESSAGE_DESCRIPTION = $NULL$;
  public static final LocalizedFormat SETTING_VOTESHOW_NAME = $NULL$;
  public static final LocalizedFormat SETTING_VOTESHOW_DESCRIPTION = $NULL$;
  public static final LocalizedFormat TYPE_BOOLEAN_TRUE = $NULL$;
  public static final LocalizedFormat TYPE_BOOLEAN_FALSE = $NULL$;
  public static final LocalizedFormat VISUAL_EFFECT_BLOOD_SETTING_NAME = $NULL$;
  public static final LocalizedFormat VISUAL_EFFECT_BLOOD_SETTING_DESCRIPTION = $NULL$;

  public static final LocalizedFormat STATS_FACTS_RANDOM = $NULL$;
  public static final LocalizedFormat STATS_FACTS_COMMAND = $NULL$;
  public static final LocalizedFormat STATS_FACTS_ERROR_AFTER = $NULL$;
  public static final LocalizedFormat STATS_FACTS_ERROR_NONE = $NULL$;
  public static final LocalizedFormat STATS_FACTS_ERROR_DISABLED = $NULL$;

  public static final LocalizedFormat STATS_FACTS_ALL = $NULL$;
  public static final LocalizedFormat STATS_FACTS_FALL_TEXT = $NULL$;
  public static final LocalizedFormat STATS_FACTS_FALL_TAGLINE_TAG1 = $NULL$;
  public static final LocalizedFormat STATS_FACTS_FALL_TAGLINE_TAG2 = $NULL$;
  public static final LocalizedFormat STATS_FACTS_FALL_TAGLINE_TAG3 = $NULL$;
  public static final LocalizedFormat STATS_FACTS_KILLS_MOST_TEXT = $NULL$;
  public static final LocalizedFormat STATS_FACTS_KILLS_MOST_TAGLINE_TAG1 = $NULL$;
  public static final LocalizedFormat STATS_FACTS_KILLS_MOST_TAGLINE_TAG2 = $NULL$;
  public static final LocalizedFormat STATS_FACTS_KILLS_MOST_TAGLINE_TAG3 = $NULL$;
  public static final LocalizedFormat STATS_FACTS_KILLS_MOST_TAGLINE_TAG4 = $NULL$;
  public static final LocalizedFormat STATS_FACTS_KILLS_WEAPON_MOST = $NULL$;
  public static final LocalizedFormat STATS_FACTS_KILLS_ASSISTS_HIGHEST = $NULL$;
  public static final LocalizedFormat STATS_FACTS_KILLS_ASSISTS_TAGLINE_TAG1 = $NULL$;
  public static final LocalizedFormat STATS_FACTS_KILLS_ASSISTS_TAGLINE_TAG2 = $NULL$;
  public static final LocalizedFormat STATS_FACTS_KILLS_ASSISTS_TAGLINE_TAG3 = $NULL$;
  public static final LocalizedFormat STATS_FACTS_SNIPE_TEXT = $NULL$;
  public static final LocalizedFormat STATS_FACTS_SNIPE_TAGLINE_TAG1 = $NULL$;
  public static final LocalizedFormat STATS_FACTS_SNIPE_TAGLINE_TAG2 = $NULL$;
  public static final LocalizedFormat STATS_FACTS_SNIPE_TAGLINE_TAG3 = $NULL$;
  public static final LocalizedFormat STATS_FACTS_SNIPE_TAGLINE_TAG4 = $NULL$;

  public static final LocalizedFormat STATS_RECAP_MATCH = $NULL$;
  public static final LocalizedFormat STATS_RECAP_LIFE = $NULL$;
  public static final LocalizedFormat STATS_DAMAGE_KILLS_KILLS = $NULL$;
  public static final LocalizedFormat STATS_DAMAGE_KILLS_CAUSE_WEAPON = $NULL$;
  public static final LocalizedFormat STATS_DAMAGE_KILLS_CAUSE_BOW = $NULL$;
  public static final LocalizedFormat STATS_DAMAGE_KILLS_MOST = $NULL$;
  public static final LocalizedFormat STATS_DAMAGE_DEATHS_CATEGORY_PLAYER = $NULL$;
  public static final LocalizedFormat STATS_DAMAGE_DEATHS_CATEGORY_NATURAL = $NULL$;
  public static final LocalizedFormat STATS_DAMAGE_DEATHS_CATEGORY_SELF = $NULL$;
  public static final LocalizedFormat STATS_DAMAGE_DEATHS_CAUSE_WEAPON = $NULL$;
  public static final LocalizedFormat STATS_DAMAGE_DEATHS_CAUSE_BOW = $NULL$;
  public static final LocalizedFormat STATS_DAMAGE_DEATHS_CAUSE_FALL_MOST = $NULL$;
  public static final LocalizedFormat STATS_DAMAGE_DEATHS_CAUSE_FALL_TOTAL = $NULL$;
  public static final LocalizedFormat STATS_DAMAGE_ASSIST_ASSISTS = $NULL$;
  public static final LocalizedFormat STATS_DAMAGE_ASSIST_WEAPON = $NULL$;

  public static final LocalizedFormat STATS_OBJECTIVES_SUMMARY = $NULL$;
  public static final LocalizedFormat STATS_OBJECTIVES_DESTROYABLES_DAMAGED = $NULL$;
  public static final LocalizedFormat STATS_OBJECTIVES_DESTROYABLES_DAMAGEDPLURAL = $NULL$;
  public static final LocalizedFormat STATS_OBJECTIVES_DESTROYABLES_REPAIRED = $NULL$;
  public static final LocalizedFormat STATS_OBJECTIVES_DESTROYABLES_REPAIREDPLURAL = $NULL$;
  public static final LocalizedFormat STATS_OBJECTIVES_LEAKABLES_LEAKED = $NULL$;
  public static final LocalizedFormat STATS_OBJECTIVES_MONUMENTS_DESTROYED = $NULL$;
  public static final LocalizedFormat STATS_OBJECTIVES_FLAGS_CAPTURED = $NULL$;
  public static final LocalizedFormat STATS_OBJECTIVES_FLAGS_CAPTUREDPLURAL = $NULL$;
  public static final LocalizedFormat STATS_OBJECTIVES_FLAGS_DROPPED = $NULL$;
  public static final LocalizedFormat STATS_OBJECTIVES_FLAGS_DROPPEDPLURAL = $NULL$;
  public static final LocalizedFormat STATS_OBJECTIVES_FLAGS_PICKED = $NULL$;
  public static final LocalizedFormat STATS_OBJECTIVES_FLAGS_PICKEDPLURAL = $NULL$;
  public static final LocalizedFormat STATS_OBJECTIVES_HILLS_CAPTURE = $NULL$;
  public static final LocalizedFormat STATS_OBJECTIVES_HILLS_CAPTUREPLURAL = $NULL$;
  public static final LocalizedFormat STATS_OBJECTIVES_HILLS_CAPTUREASSIST = $NULL$;
  public static final LocalizedFormat STATS_OBJECTIVES_HILLS_CAPTUREASSISTPLURAL = $NULL$;
  public static final LocalizedFormat STATS_OBJECTIVES_POINTS_EARNEDPLURAL = $NULL$;
  public static final LocalizedFormat STATS_OBJECTIVES_POINTS_EARNED = $NULL$;
  public static final LocalizedFormat STATS_OBJECTIVES_WOOLS_PLACED = $NULL$;
  public static final LocalizedFormat STATS_OBJECTIVES_WOOLS_PLACEDPLURAL = $NULL$;
  public static final LocalizedFormat STATS_OBJECTIVES_WOOLS_TOUCHEDPLURAL = $NULL$;
  public static final LocalizedFormat STATS_OBJECTIVES_WOOLS_TOUCHED = $NULL$;
  static final LocaleBundle BUNDLE;

  static {
    BUNDLE = TranslationProvider.loadBundle(AtlasConfig.localesPath, "en_US", "es_ES");
    TranslationProvider.map(Translations.class, BUNDLE);
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
