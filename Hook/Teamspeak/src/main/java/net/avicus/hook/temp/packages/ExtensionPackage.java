package net.avicus.hook.temp.packages;

import lombok.Getter;


public class ExtensionPackage extends Package {

  @Getter
  private final int extensionTime;

  public ExtensionPackage(int price, String successMessage, int extensionTime) {
    super(price, successMessage);
    this.extensionTime = extensionTime;
  }
}
