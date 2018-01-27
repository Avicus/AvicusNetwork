package net.avicus.magma.network.user;

import java.util.Comparator;
import javax.annotation.Nonnull;
import net.avicus.magma.database.model.impl.Rank;
import net.avicus.magma.database.model.impl.User;

public final class UserRankEntry implements Comparable<UserRankEntry> {

  public static final Comparator<UserRankEntry> COMPARATOR = UserRankEntry::compareTo;
  public final User user;
  public final Rank rank;

  public UserRankEntry(final User user, final Rank rank) {
    this.user = user;
    this.rank = rank;
  }

  @Override
  public int compareTo(@Nonnull UserRankEntry that) {
    // that before this
    return Integer.compare(that.rank.getPriority(), this.rank.getPriority());
  }
}
