package net.avicus.magma.module.prestige;

import org.bukkit.ChatColor;

public enum PrestigeLevel {
  LEVEL_0(0, 0, ""),
  LEVEL_1(1, 12199, ChatColor.LIGHT_PURPLE + "*"),
  LEVEL_2(2, 24398, ChatColor.GREEN + "*"),
  LEVEL_3(3, 36597, ChatColor.AQUA + "*"),
  LEVEL_4(4, 48796, ChatColor.RED + "*"),
  LEVEL_5(5, 60995, ChatColor.YELLOW + "*"),
  LEVEL_6(6, 73194, ChatColor.DARK_GREEN + "*"),
  LEVEL_7(7, 85393, ChatColor.BLUE + "*"),
  LEVEL_8(8, 97592, ChatColor.DARK_PURPLE + "*"),
  LEVEL_9(9, 109791, ChatColor.GRAY + "*"),
  LEVEL_10(10, 121990, ChatColor.BLACK + "*"),
  LEVEL_11(11, 134189, ChatColor.LIGHT_PURPLE + "❂"),
  LEVEL_12(12, 146388, ChatColor.GREEN + "❂"),
  LEVEL_13(13, 176587, ChatColor.AQUA + "❂"),
  LEVEL_14(14, 188786, ChatColor.RED + "❂"),
  LEVEL_15(15, 200985, ChatColor.YELLOW + "❂"),
  MAX(100, 213184, ChatColor.BLACK + "♟");

  private final int id;
  private final int xp;
  private final String symbol;

  PrestigeLevel(int id, int xp, String symbol) {
    this.id = id;
    this.xp = xp;
    this.symbol = symbol;
  }

  public static PrestigeLevel fromID(int id) {
    switch (id) {
      case 1:
        return LEVEL_1;
      case 2:
        return LEVEL_2;
      case 3:
        return LEVEL_3;
      case 4:
        return LEVEL_4;
      case 5:
        return LEVEL_5;
      case 6:
        return LEVEL_6;
      case 7:
        return LEVEL_7;
      case 8:
        return LEVEL_8;
      case 9:
        return LEVEL_9;
      case 10:
        return LEVEL_10;
      case 11:
        return LEVEL_11;
      case 12:
        return LEVEL_12;
      case 13:
        return LEVEL_13;
      case 14:
        return LEVEL_14;
      case 15:
        return LEVEL_15;
      case 100:
        return MAX;
      default:
        return LEVEL_0;
    }
  }

  public static PrestigeLevel fromDB(net.avicus.magma.database.model.impl.PrestigeLevel db) {
    if (db == null) {
      return LEVEL_0;
    }

    return fromID(db.getLevel());
  }

  public int getId() {
    return this.id;
  }

  public int getXp() {
    return this.xp;
  }

  public String getSymbol() {
    return this.symbol;
  }

  public PrestigeLevel next() {
    switch (this) {
      case LEVEL_0:
        return LEVEL_1;
      case LEVEL_1:
        return LEVEL_2;
      case LEVEL_2:
        return LEVEL_3;
      case LEVEL_3:
        return LEVEL_4;
      case LEVEL_4:
        return LEVEL_5;
      case LEVEL_5:
        return LEVEL_6;
      case LEVEL_6:
        return LEVEL_7;
      case LEVEL_7:
        return LEVEL_8;
      case LEVEL_8:
        return LEVEL_9;
      case LEVEL_9:
        return LEVEL_10;
      case LEVEL_10:
        return LEVEL_11;
      case LEVEL_11:
        return LEVEL_12;
      case LEVEL_12:
        return LEVEL_13;
      case LEVEL_13:
        return LEVEL_14;
      case LEVEL_14:
        return LEVEL_15;
      case LEVEL_15:
        return MAX;
      default:
        return MAX;
    }
  }
}
