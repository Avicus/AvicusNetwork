package net.avicus.hook.sessions;

import net.avicus.hook.HookConfig.Server.SessionLogging;
import net.avicus.hook.utils.HookTask;

public class SessionUpdateTask extends HookTask {

  public void start() {
    int interval = Math.min(5, SessionLogging.getPadding() / 3);
    repeatAsync(0, 20 * interval);
  }

  @Override
  public void run() throws Exception {
    Sessions.updateAll();
  }
}
