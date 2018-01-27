package net.avicus.magma.api.http;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

// Single-element enum to implement Singleton.
public enum Singleton {
  // Just one of me so constructor will be called once.
  Client;
  // The thread-safe client.
  public final CloseableHttpClient threadSafeClient;
  // The pool monitor.
  public final IdleConnectionMonitorThread monitor;

  // The constructor creates it - thus late
  private Singleton() {
    PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
    // Increase max total connection to 200
    cm.setMaxTotal(200);
    // Increase default max connection per route to 20
    cm.setDefaultMaxPerRoute(20);
    // Build the client.
    threadSafeClient = HttpClients.custom()
        .setConnectionManager(cm)
        .build();
    // Start up an eviction thread.
    monitor = new IdleConnectionMonitorThread(cm);
    // Don't stop quitting.
    monitor.setDaemon(true);
    monitor.start();
  }

  public CloseableHttpClient get() {
    return threadSafeClient;
  }

}
