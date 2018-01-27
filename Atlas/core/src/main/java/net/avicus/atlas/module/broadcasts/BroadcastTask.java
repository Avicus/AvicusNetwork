package net.avicus.atlas.module.broadcasts;

import net.avicus.atlas.match.Match;
import net.avicus.atlas.util.AtlasTask;

/**
 * Task that handles the display of a single broadcast
 */
public class BroadcastTask extends AtlasTask {

  /**
   * Match that the broadcast exists in.
   */
  private final Match match;
  /**
   * Broadcast that is being displayed.
   */
  private final Broadcast broadcast;
  /**
   * Running total of the number of times the broadcast has been displayed.
   */
  private int broadcasts = 0;

  /**
   * Construct a task.
   *
   * @param match match the broadcast exists in
   * @param broadcast broadcast to be displayed
   */
  public BroadcastTask(Match match, Broadcast broadcast) {
    super();
    this.match = match;
    this.broadcast = broadcast;
  }

  /**
   * Start the task.
   * Repeat interval is inferred from the broadcast settings.
   */
  public void start() {
    repeat(0, this.broadcast.tickInterval());
  }

  /**
   * Display the broadcast (if the check passes)
   */
  @Override
  public void run() {

    if (this.broadcast.test(this.match)) {
      if (this.broadcast.getRepetitionCount().isPresent()
          && this.broadcast.getRepetitionCount().get() >= broadcasts) {
        cancel0();
      }

      this.broadcast.broadcast(this.match);
      broadcasts++;
    }
  }
}
