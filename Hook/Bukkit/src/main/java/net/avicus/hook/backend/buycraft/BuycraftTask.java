package net.avicus.hook.backend.buycraft;

import java.util.TimerTask;
import net.buycraft.plugin.platform.standalone.runner.StandaloneBuycraftRunnerBuilder;

public class BuycraftTask extends TimerTask {

  private final StandaloneBuycraftRunnerBuilder builder;

  public BuycraftTask(StandaloneBuycraftRunnerBuilder builder) {
    this.builder = builder;
  }

  @Override
  public void run() {
    this.builder.start();
  }
}
