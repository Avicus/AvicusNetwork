package net.avicus.magma.api.http;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Stop {

  // The return queue.
  private final BlockingQueue<Stop> stop = new ArrayBlockingQueue<Stop>(1);

  // Called by the process that is being told to stop.
  public void stopped() {
    // Push me back up the queue to indicate we are now stopped.
    stop.add(this);
  }

  // Called by the process requesting the stop.
  public void waitForStopped() throws InterruptedException {
    // Wait until the callee acknowledges that it has stopped.
    stop.take();
  }

}
