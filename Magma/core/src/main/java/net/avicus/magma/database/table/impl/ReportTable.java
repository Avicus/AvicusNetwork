package net.avicus.magma.database.table.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import net.avicus.magma.database.model.impl.Report;
import net.avicus.magma.database.model.impl.Server;
import net.avicus.magma.database.model.impl.User;
import net.avicus.quest.database.Database;
import net.avicus.quest.model.Table;
import net.avicus.quest.query.Filter;
import net.avicus.quest.query.Operator;

public class ReportTable extends Table<Report> {

  public ReportTable(Database database, String name, Class<Report> model) {
    super(database, name, model);
  }

  public List<Report> findByUser(User user) {
    return select().where("user_id", user.getId()).execute();
  }

  public void createReport(User sender, User reported, Server server, String reason) {
    Report report = new Report(sender.getId(), reported.getId(), reason, server.getName(),
        new Date());
    insert(report).execute();
  }

  public List<Report> getRecentReports(Optional<Server> server, Optional<User> user) {
    Filter query = new Filter();

    if (server.isPresent()) {
      query = query.where("server", server.get().getName(), Operator.EQUALS);
    }

    if (user.isPresent()) {
      query = query.where("user_id", user.get().getId(), Operator.EQUALS);
    }

    return select().where(query).limit(100).order("created_at", "DESC").execute();
  }
}
