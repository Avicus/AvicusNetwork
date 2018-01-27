package net.avicus.magma.util;

public class Inventories {

  private static final int ROW_SIZE = 9;
  private static final int MIN_STACK_AMOUNT = 1;
  private static final int MAX_STACK_AMOUNT = 64;

  private Inventories() {
  }

  public static int rowCount(int size) {
    return (size + ROW_SIZE - 1) / ROW_SIZE;
  }

  public static int clampedStackAmount(final int quantity) {
    return Math.max(Math.min(MAX_STACK_AMOUNT, quantity), MIN_STACK_AMOUNT);
  }
}
