package net.avicus.hook.temp.packages;

import java.util.Date;
import lombok.Getter;
import net.avicus.hook.Main;
import net.avicus.hook.wrapper.HookClient;
import net.avicus.magma.database.model.impl.CreditTransaction;

public abstract class Package {

  @Getter
  private final int price;
  private final String successMessage;

  public Package(int price, String successMessage) {
    this.price = price;
    this.successMessage = successMessage;
  }

  public void charge(HookClient client) {
    Main.getExecutor().execute(() -> {
      int amount = -Math.abs(this.price);
      CreditTransaction transaction = new CreditTransaction(client.getUser().getId(), amount, 1.0,
          new Date());
      client.message("Yoy have been charged [b]" + this.price + "[/b] credits!");
      Main.getHook().getDatabase().getCreditTransactions().insert(transaction).execute();
      client.message(this.successMessage);
    });
  }

  public void purchase(HookClient client) {
    charge(client);
  }
}
