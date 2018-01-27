package net.avicus.magma.database.table.impl;

import java.math.BigDecimal;
import net.avicus.magma.database.model.impl.CreditTransaction;
import net.avicus.quest.database.Database;
import net.avicus.quest.model.Table;
import net.avicus.quest.query.RowList;

public class CreditTransactionTable extends Table<CreditTransaction> {

  public CreditTransactionTable(Database database, String name, Class<CreditTransaction> model) {
    super(database, name, model);
  }

  public int sumCredits(int userId) {
    RowList list = getDatabase().select(getName()).columns("SUM(`amount`)").where("user_id", userId)
        .execute();
    try {
      BigDecimal big = (BigDecimal) list.first().getMap().values().stream().findAny()
          .orElse(new BigDecimal(0));
      return big.intValue();
    } catch (Exception e) {
      return 0;
    }
  }
}
