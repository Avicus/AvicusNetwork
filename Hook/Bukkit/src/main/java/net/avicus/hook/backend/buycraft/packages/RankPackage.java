package net.avicus.hook.backend.buycraft.packages;

import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import net.avicus.hook.Hook;
import net.avicus.hook.backend.buycraft.BuycraftPackage;
import net.avicus.magma.database.Database;
import net.avicus.magma.database.model.impl.Rank;
import net.avicus.magma.database.model.impl.RankMember;
import net.avicus.magma.database.model.impl.User;
import org.joda.time.DateTime;
import org.joda.time.ReadablePeriod;

public class RankPackage implements BuycraftPackage {

  private final Database database;
  private final Rank rank;
  private final Optional<ReadablePeriod> duration;
  private final Logger log;

  public RankPackage(Database database, Rank rank, Optional<ReadablePeriod> duration) {
    this.database = database;
    this.rank = rank;
    this.duration = duration;
    this.log = Hook.plugin().getLogger();
  }

  public RankPackage(Database database, Rank rank) {
    this(database, rank, Optional.empty());
  }

  public RankPackage(Database database, Rank rank, ReadablePeriod duration) {
    this(database, rank, Optional.of(duration));
  }

  @Override
  public void execute(Status status, User user, Map<String, String> variables) {
    this.log.info("Executing [" + status + "]: " + user);

    switch (status) {
      /**
       * Purchase rank or renewal.
       */
      case INITIAL:
      case RENEWAL:
        extendMembership(user);
        break;

      /**
       * Rank expires.
       */
      case EXPIRY:
        // Do nothing, this is handled by a cleanup task.
        break;

      /**
       * Chargeback or refund.
       */
      case CHARGEBACK:
      case REFUND:
        deleteMembership(user);
        break;
    }
  }

  /**
   * Create an expiration date from now.
   *
   * @return The expiry date.
   */
  private Date expiryDate() {
    if (this.duration.isPresent()) {
      DateTime now = DateTime.now();
      return now.plus(this.duration.get()).toDate();
    } else {
      return null;
    }
  }

  /**
   * Find the existing membership of this rank for a user.
   */
  private Optional<RankMember> existingMembership(User user) {
    return this.database.getRankMembers().findByUser(user)
        .stream()
        .filter(m -> m.getRankId() == this.rank.getId())
        .findFirst();
  }

  /**
   * Create or renew a membership.
   */
  private void extendMembership(User user) {
    RankMember member = existingMembership(user).orElse(null);

    // New membership
    if (member == null) {
      member = new RankMember(user.getId(), this.rank.getId(), expiryDate());
      this.database.getRankMembers().insert(member).execute();
    }
    // Renew membership
    else {
      this.database.getRankMembers().setExpiration(member, expiryDate());
    }
  }

  /**
   * @param user
   */
  private void deleteMembership(User user) {
    RankMember member = existingMembership(user).orElse(null);

    if (member != null) {
      this.database.getRankMembers().delete(member);
    }
  }
}
