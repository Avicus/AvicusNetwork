package net.avicus.magma.api;

import java.io.IOException;
import lombok.Getter;
import net.avicus.magma.api.service.AlertsService;
import net.avicus.magma.api.service.PresentsService;
import net.avicus.magma.api.service.connected.ConnectedAlertsService;
import net.avicus.magma.api.service.connected.ConnectedPresentsService;
import net.avicus.magma.api.service.mock.MockAlertsService;
import net.avicus.magma.api.service.mock.MockPresentsService;

@Getter
public class API {

  private final APIClient client;

  private final AlertsService alerts;
  private final PresentsService presents;

  public API(String url, String key, boolean mock) throws IOException {
    if (mock) {
      this.alerts = new MockAlertsService();
      this.presents = new MockPresentsService();
      this.client = null;
    } else {
      this.client = new APIClient(url, key);
      this.alerts = new ConnectedAlertsService(this.client);
      this.presents = new ConnectedPresentsService(this.client);
    }
  }
}
