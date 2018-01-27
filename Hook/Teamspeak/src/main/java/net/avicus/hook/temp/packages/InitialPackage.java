package net.avicus.hook.temp.packages;

import lombok.Getter;
import net.avicus.hook.wrapper.HookClient;

public class InitialPackage extends Package {

  @Getter
  private final int deleteDelay;
  @Getter
  private final int cooldown;

  public InitialPackage(int price, String successMessage, int deleteDelay, int cooldown) {
    super(price, successMessage);
    this.deleteDelay = deleteDelay;
    this.cooldown = cooldown;
  }

  @Override
  public void purchase(HookClient client) {
    super.purchase(client);
  }
}
