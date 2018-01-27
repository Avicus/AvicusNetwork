package net.avicus.magma.api;

import lombok.Getter;
import net.avicus.magma.api.service.AlertsService;
import net.avicus.magma.api.service.PresentsService;

@Getter
public class API {

  private final APIClient client;

  private final AlertsService alerts;
  private final PresentsService presents;

  public API(APIClient client) {
    this.client = client;

    this.alerts = new AlertsService(this.client);
    this.presents = new PresentsService(this.client);
  }
}
