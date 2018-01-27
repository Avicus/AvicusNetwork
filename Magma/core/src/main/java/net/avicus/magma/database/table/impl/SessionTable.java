package net.avicus.magma.database.table.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import net.avicus.magma.database.model.impl.Session;
import net.avicus.quest.database.Database;
import net.avicus.quest.model.Table;
import net.avicus.quest.query.Filter;
import net.avicus.quest.query.Operator;
import net.avicus.quest.query.RowList;
import org.joda.time.Duration;
import org.joda.time.Seconds;

public class SessionTable extends Table<Session> {

  public SessionTable(Database database, String name, Class<Session> model) {
    super(database, name, model);
  }

  public Optional<Session> findLatest(int userId) {
    List<Session> result = select().where("user_id", userId).order("created_at", "DESC").limit(1)
        .execute();
    for (Session session : result) {
      return Optional.of(session);
    }
    return Optional.empty();
  }

  public Duration timeOnline(int userId) {
    RowList list = getDatabase().select(getName())
        .columns("SUM(`duration`)")
        .where("user_id", userId)
        .execute();
    try {
      BigDecimal big = (BigDecimal) list.first().getMap().values().stream().findAny()
          .orElse(new BigDecimal(0));
      return Duration.standardSeconds(big.intValue());
    } catch (Exception e) {
      return Seconds.ZERO.toStandardDuration();
    }
  }

  public Optional<Session> findLatestByIp(String ip) {
    List<Session> result = select().where("ip", ip).order("created_at", "DESC").limit(1).execute();
    for (Session session : result) {
      return Optional.of(session);
    }
    return Optional.empty();
  }

  public List<Session> findByCreatedAt(Date from, Date to) {
    Filter filter = new Filter("created_at", from, Operator.GREATER_OR_EQUAL);
    filter.and("created_at", to, Operator.LESS);
    return select().where(filter).execute();
  }

  public List<Session> findByExpiredAt(Date from, Date to, boolean gracefulOnly) {
    Filter filter = new Filter("updated_at", from, Operator.GREATER_OR_EQUAL);
    filter.and("updated_at", to, Operator.LESS);
    if (gracefulOnly) {
      filter.and("graceful", true);
    }
    return select().where(filter).execute();
  }

  public List<Session> activeSessions() {
    Date now = new Date();
    Filter filter = new Filter("updated_at", now, Operator.GREATER_OR_EQUAL);
    return select().where(filter).execute();
  }
}
