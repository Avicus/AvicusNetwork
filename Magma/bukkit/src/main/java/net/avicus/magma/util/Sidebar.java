package net.avicus.magma.util;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import lombok.Getter;
import net.avicus.magma.NetworkIdentification;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class Sidebar {

  private static final Random random = new Random();
  private static final List<ChatColor> colors = Arrays.asList(
      ChatColor.DARK_BLUE,
      ChatColor.DARK_GREEN,
      ChatColor.DARK_AQUA,
      ChatColor.DARK_PURPLE,
      ChatColor.GOLD,
      ChatColor.BLUE,
      ChatColor.GREEN,
      ChatColor.AQUA,
      ChatColor.LIGHT_PURPLE,
      ChatColor.YELLOW
  );
  // Each row has its own scoreboard team
  protected final String[] rows = new String[Constants.MAX_ROWS + 3];
  protected final int[] scores = new int[Constants.MAX_ROWS + 3];
  protected final Team[] teams = new Team[Constants.MAX_ROWS + 3];
  protected final String[] players = new String[Constants.MAX_ROWS + 3];
  @Getter
  private final Scoreboard scoreboard;
  @Getter
  private final Objective objective;

  public Sidebar(Scoreboard scoreboard) {
    this(scoreboard, UUID.randomUUID().toString().substring(0, 5));
  }

  public Sidebar(Scoreboard scoreboard, String title) {
    this.scoreboard = scoreboard;
    this.objective = this.scoreboard.registerNewObjective(Constants.IDENTIFIER, "dummy");
    this.objective.setDisplayName(title);
    this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);

    for (int i = 1; i <= Constants.MAX_ROWS + 2; ++i) {
      this.rows[i] = null;
      this.scores[i] = -1;

      this.players[i] = String.valueOf(ChatColor.COLOR_CHAR) + (char) i;

      this.teams[i] = this.scoreboard.registerNewTeam(Constants.IDENTIFIER + "-row-" + i);
      this.teams[i].setPrefix("");
      this.teams[i].setSuffix("");
      this.teams[i].addEntry(this.players[i]);
    }
  }

  public void addURL() {
    this.rows[15] = "";
    this.scores[15] = 2;

    this.players[15] = String.valueOf(ChatColor.COLOR_CHAR) + (char) 15;

    this.teams[15].setPrefix("");
    this.teams[15].setSuffix("");
    this.objective.getScore(this.players[15]).setScore(2);
    this.teams[15].addEntry(this.players[15]);

    this.rows[16] = NetworkIdentification.URL;
    this.scores[16] = 1;

    this.players[16] = String.valueOf(ChatColor.COLOR_CHAR) + (char) 16;

    String text =
        colors.get(random.nextInt(colors.size())) + ChatColor.BOLD.toString()
            + NetworkIdentification.URL;

    int split = Constants.MAX_PREFIX - 1;
    String prefix = StringUtils.substring(text, 0, split);
    String lastColors = org.bukkit.ChatColor.getLastColors(prefix);
    String suffix = lastColors + StringUtils
        .substring(text, split, split + Constants.MAX_SUFFIX - lastColors.length());

    this.teams[16].setPrefix(prefix);
    this.teams[16].setSuffix(suffix);
    this.objective.getScore(this.players[16]).setScore(1);
    this.teams[16].addEntry(this.players[16]);
  }

  public void setTitle(String title) {
    this.objective.setDisplayName(title);
  }

  public void setRow(int maxScore, int row, @Nullable String text) {
    if (row < 0 || row >= Constants.MAX_ROWS) {
      return;
    }

    int score = text == null ? -1 : maxScore - row + 2;
    if (this.scores[row] != score) {
      this.scores[row] = score;

      if (score == -1) {
        this.scoreboard.resetScores(this.players[row]);
      } else {
        this.objective.getScore(this.players[row]).setScore(score);
      }
    }

    if (!Objects.equals(this.rows[row], text)) {
      this.rows[row] = text;

      if (text != null) {
                    /*
                     Split the row text into prefix and suffix, limited to 16 chars each. Because the player name
                     is a color code, we have to restore the color at the split in the suffix. We also have to be
                     careful not to split in the middle of a color code.
                    */
        int split =
            Constants.MAX_PREFIX - 1; // Start by assuming there is a color code right on the split
        if (text.length() < Constants.MAX_PREFIX || text.charAt(split) != ChatColor.COLOR_CHAR) {
          // If there isn't, we can fit one more char in the prefix
          split++;
        }

        // Split and truncate the text, and restore the color in the suffix
        String prefix = StringUtils.substring(text, 0, split);
        String lastColors = org.bukkit.ChatColor.getLastColors(prefix);
        String suffix = lastColors + StringUtils
            .substring(text, split, split + Constants.MAX_SUFFIX - lastColors.length());
        this.teams[row].setPrefix(prefix);
        this.teams[row].setSuffix(suffix);
      }
    }
  }

  public class Constants {

    public static final int MAX_ROWS = 14;
    private static final int MAX_PREFIX = 16;
    private static final int MAX_SUFFIX = 16;
    private static final String IDENTIFIER = "magma";
  }
}

