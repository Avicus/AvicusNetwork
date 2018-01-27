package net.avicus.atlas.module.results;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import lombok.Getter;
import net.avicus.atlas.module.groups.Competitor;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.locale.text.LocalizableFormat;
import net.avicus.compendium.locale.text.LocalizedNumber;
import net.avicus.compendium.locale.text.UnlocalizedFormat;
import net.avicus.compendium.locale.text.UnlocalizedText;
import net.avicus.compendium.utils.Formats;

public class RankingDisplay {

  private final int toResolve;
  @Getter
  private TreeMap<Integer, HashSet<Competitor>> ranking;

  /**
   * Constructor
   *
   * @param toResolve Number of places to resolve to.
   * @param ranking Map of ranked competitors in DESCENDING order.
   */
  public RankingDisplay(int toResolve, TreeMap<Integer, HashSet<Competitor>> ranking) {
    this.toResolve = toResolve;

    this.ranking = normalizeRanking(ranking);
  }

  private TreeMap<Integer, HashSet<Competitor>> normalizeRanking(
      TreeMap<Integer, HashSet<Competitor>> original) {
    // Fix for callers providing raw integer values based on other stats than true rank position.
    TreeMap<Integer, HashSet<Competitor>> normalizedRanking = new TreeMap<>();
    int rank = 0;
    for (Map.Entry<Integer, HashSet<Competitor>> entry : original.entrySet()) {
      rank++;
      normalizedRanking.put(rank, entry.getValue());
    }
    return normalizedRanking;
  }

  public List<Localizable> getRankDisplay(boolean showAll) {
    List<Localizable> res = new ArrayList<>();
    for (Map.Entry<Integer, HashSet<Competitor>> entry : ranking.entrySet()) {
      if (toResolve < entry.getKey() && !showAll) {
        break;
      }

      if (entry.getValue().isEmpty()) {
        continue;
      }

      LocalizableFormat format = Formats.humanList(entry.getValue().size());

      List<Localizable> args = entry.getValue()
          .stream()
          .map(Competitor::getColoredName)
          .collect(Collectors.toList());

      LocalizableFormat placeFormat = new UnlocalizedFormat("{0}. ");

      res.add(new UnlocalizedText("{0} {1}",
          placeFormat.with(new LocalizedNumber(ranking.headMap(entry.getKey()).size() + 1)),
          format.with((Localizable[]) args.toArray(new Localizable[0]))));
    }
    return res;
  }

  public List<Localizable> getRankDisplay() {
    return getRankDisplay(false);
  }

  public void updateRankings(TreeMap<Integer, HashSet<Competitor>> ranking) {
    this.ranking = normalizeRanking(ranking);
  }
}