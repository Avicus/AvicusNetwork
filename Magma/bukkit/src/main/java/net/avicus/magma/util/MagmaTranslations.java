package net.avicus.magma.util;

import static net.avicus.magma.util.TranslationProvider.$NULL$;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.avicus.compendium.locale.LocaleBundle;
import net.avicus.compendium.locale.text.LocalizedFormat;
import net.avicus.compendium.locale.text.LocalizedText;
import net.avicus.magma.Magma;
import org.bukkit.ChatColor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MagmaTranslations {

  // ----
  public static final LocalizedFormat COMMANDS_SERVER_CONNECTING = $NULL$;
  public static final LocalizedFormat COMMANDS_SERVER_CURRENT = $NULL$;
  public static final LocalizedFormat COMMANDS_SERVER_ERROR_OFFLINE = $NULL$;
  public static final LocalizedFormat COMMANDS_SERVER_QUERY_NONE = $NULL$;
  public static final LocalizedFormat COMMANDS_SERVER_QUERY_GROUP_NONE = $NULL$;
  public static final LocalizedFormat COMMANDS_STAFF_NONE = $NULL$;
  public static final LocalizedFormat COMMANDS_STAFF_TITLE = $NULL$;
  public static final LocalizedFormat COMMANDS_TOGGLEPERMISSIBLE_TOGGLED = $NULL$;
  public static final LocalizedFormat ERROR_COMMANDS_PERMISSION_PREMIUM = $NULL$;
  public static final LocalizedFormat ERROR_UNKNOWN_PLAYER = $NULL$;
  public static final LocalizedFormat ERROR_NO_UNREAD_ALERTS = $NULL$;
  public static final LocalizedFormat ERROR_NO_ALERTS = $NULL$;
  public static final LocalizedFormat ERROR_API_CONNECT = $NULL$;
  public static final LocalizedFormat FREEZE_BROADCAST_FROZE = $NULL$;
  public static final LocalizedFormat FREEZE_BROADCAST_JOINED = $NULL$;
  public static final LocalizedFormat FREEZE_BROADCAST_UNFROZE = $NULL$;
  public static final LocalizedFormat FREEZE_FROZE = $NULL$;
  public static final LocalizedFormat FREEZE_FROZEN = $NULL$;
  public static final LocalizedFormat FREEZE_UNFROZE = $NULL$;
  public static final LocalizedFormat FREEZE_UNFROZEN = $NULL$;
  public static final LocalizedFormat JOIN_DISALLOW_PERMISSION = $NULL$;
  public static final LocalizedFormat PRESTIGE_COMMANDS_PRESTIGE_CONFIRM = $NULL$;
  public static final LocalizedFormat GENERIC_TYPE_BOOLEAN_TRUE = $NULL$;
  public static final LocalizedFormat GENERIC_TYPE_BOOLEAN_FALSE = $NULL$;
  public static final LocalizedFormat GENERIC_TYPE_CONFIRM_CANCEL_NAME = $NULL$;
  public static final LocalizedFormat GENERIC_TYPE_CONFIRM_CANCEL_DESCRIPTION = $NULL$;
  public static final LocalizedFormat GENERIC_TYPE_CONFIRM_CONFIRM_NAME = $NULL$;
  public static final LocalizedFormat GENERIC_TYPE_CONFIRM_CONFIRM_DESCRIPTION = $NULL$;
  public static final LocalizedFormat GENERIC_UNREAD_ALERTS = $NULL$;
  public static final LocalizedFormat GENERIC_DELETED_ALERT = $NULL$;
  public static final LocalizedFormat PRESTIGE_LEVELUP_SUCCESS = $NULL$;
  public static final LocalizedFormat PRESTIGE_LEVELUP_FAIL = $NULL$;
  public static final LocalizedFormat PRESTIGE_LEVELUP_MAX = $NULL$;
  public static final LocalizedFormat PRESTIGE_LEVELUP_CANCELED = $NULL$;
  public static final LocalizedFormat PRESTIGE_LEVELUP_BROADCAST = $NULL$;
  public static final LocalizedFormat PRESTIGE_LEVELUP_NEARBY = $NULL$;
  public static final LocalizedFormat PRESTIGE_LEVELUP_MAXBROADCAST = $NULL$;
  public static final LocalizedFormat GUI_GENERIC_BACK = $NULL$;
  public static final LocalizedFormat GUI_GENERIC_ONLINE_NOT = $NULL$;
  public static final LocalizedFormat GUI_SERVER_TITLE = $NULL$;
  public static final LocalizedFormat GUI_SERVER_CLICK_LEFT = $NULL$;
  public static final LocalizedFormat GUI_SERVER_CLICK_RIGHT = $NULL$;
  public static final LocalizedFormat REPORT_COOLDOWN_PLURAL = $NULL$;
  public static final LocalizedFormat REPORT_COOLDOWN_SINGULAR = $NULL$;
  public static final LocalizedFormat REPORT_RECENT_NONE = $NULL$;
  public static final LocalizedFormat REPORT_RECENT_TITLE_GLOBAL = $NULL$;
  public static final LocalizedFormat REPORT_RECENT_TITLE_PLAYER = $NULL$;
  public static final LocalizedFormat REPORT_RECENT_TITLE_SERVER = $NULL$;
  public static final LocalizedFormat REPORT_RECENT_VIEW_PLAYER = $NULL$;
  public static final LocalizedFormat REPORT_SENT = $NULL$;
  public static final LocalizedFormat RTP_GENERIC_SUCCESS_PLAYER = $NULL$;
  public static final LocalizedFormat RTP_SERVER_CONNECT = $NULL$;
  public static final LocalizedFormat RTP_SERVER_CONNECTING = $NULL$;
  public static final LocalizedFormat RTP_PLAYER_TELEPORT_LOCAL = $NULL$;
  public static final LocalizedFormat RTP_PLAYER_TELEPORT_REMOTE = $NULL$;
  public static final LocalizedFormat RTP_PLAYER_TELEPORT_FAIL_PARTICIPATING = $NULL$;
  public static final LocalizedFormat RTP_PLAYER_TELEPORTING_LOCAL = $NULL$;
  public static final LocalizedFormat RTP_PLAYER_TELEPORTING_REMOTE = $NULL$;
  public static final LocalizedFormat SETTING_REPORTNOTIFICATION_NAME = $NULL$;
  public static final LocalizedFormat SETTING_REPORTNOTIFICATION_SUMMARY = $NULL$;
  public static final LocalizedFormat SETTING_STAFFCHANNELS_NAME = $NULL$;
  public static final LocalizedFormat SETTING_STAFFCHANNELS_SUMMARY = $NULL$;
  public static final LocalizedFormat SETTING_STAFFCHANNELS_DISABLED = $NULL$;
  public static final LocalizedFormat SETTING_BAR_NAME = $NULL$;
  public static final LocalizedFormat SETTING_BAR_DESCRIPTION = $NULL$;
  public static final LocalizedFormat SETTING_PREMIUM_NAME = $NULL$;
  public static final LocalizedFormat SETTING_PREMIUM_SUMMARY = $NULL$;
  public static final LocalizedFormat SETTING_ALERT_NOTIFY = $NULL$;
  public static final LocalizedFormat SETTING_ALERT_SUMMARY = $NULL$;
  public static final LocalizedFormat GUI_RARITY_COMMON = $NULL$;
  public static final LocalizedFormat GUI_RARITY_UNCOMMON = $NULL$;
  public static final LocalizedFormat GUI_RARITY_RARE = $NULL$;
  public static final LocalizedFormat GUI_RARITY_EXTREMELY = $NULL$;
  public static final LocalizedFormat GUI_CLICK_KEY = $NULL$;
  public static final LocalizedFormat GUI_CRATE_REVEAL = $NULL$;
  public static final LocalizedFormat ERROR_NO_KEY = $NULL$;
  public static final LocalizedFormat GUI_PAGE_NEXT = $NULL$;
  public static final LocalizedFormat GUI_PAGE_PREV = $NULL$;
  public static final LocalizedFormat GUI_RANK_GADGET = $NULL$;
  public static final LocalizedFormat GUI_RANK_RECEIVED = $NULL$;
  public static final LocalizedFormat GUI_ALERTS = $NULL$;
  public static final LocalizedFormat GUI_OPEN = $NULL$;
  public static final LocalizedFormat GUI_DELETE = $NULL$;

  private static final LocaleBundle BUNDLE;

  static {
    BUNDLE = TranslationProvider.loadBundle(Magma.get());
    TranslationProvider.map(MagmaTranslations.class, BUNDLE);
  }

  public static LocalizedText bool(final boolean value) {
    return value ? GENERIC_TYPE_BOOLEAN_TRUE.with(ChatColor.GREEN)
        : GENERIC_TYPE_BOOLEAN_FALSE.with(ChatColor.RED);
  }
}
