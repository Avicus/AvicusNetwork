package net.avicus.magma.api.http;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

// Watches for stale connections and evicts them.
public class IdleConnectionMonitorThread extends Thread {

  // The manager to watch.
  private final PoolingHttpClientConnectionManager cm;
  // Use a BlockingQueue to stop everything.
  private final BlockingQueue<Stop> stopSignal = new ArrayBlockingQueue<Stop>(1);

  IdleConnectionMonitorThread(PoolingHttpClientConnectionManager cm) {
    super();
    this.cm = cm;
  }

  @Override
  public void run() {
    try {
      // Holds the stop request that stopped the process.
      Stop stopRequest;
      // Every 5 seconds.
      while ((stopRequest = stopSignal.poll(5, TimeUnit.SECONDS)) == null) {
        // Close expired connections
        cm.closeExpiredConnections();
        // Optionally, close connections that have been idle too long.
        cm.closeIdleConnections(60, TimeUnit.SECONDS);
      }
      // Acknowledge the stop request.
      stopRequest.stopped();
    } catch (InterruptedException ex) {
      // terminate
    }
  }

  public void shutdown() throws InterruptedException {
    // Signal the stop to the thread.
    Stop stop = new Stop();
    stopSignal.add(stop);
    // Wait for the stop to complete.
    stop.waitForStopped();
    // Close the pool - Added
    try {
      Singleton.Client.threadSafeClient.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    // Close the connection manager.
    cm.close();
  }

}
