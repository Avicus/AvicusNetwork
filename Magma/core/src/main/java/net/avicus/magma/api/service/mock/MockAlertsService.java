package net.avicus.magma.api.service.mock;

import com.google.common.collect.Lists;
import java.util.List;
import net.avicus.magma.api.graph.types.alert.Alert;
import net.avicus.magma.api.service.AlertsService;
import net.avicus.magma.database.model.impl.Friend;
import net.avicus.magma.database.model.impl.User;
import org.joda.time.DateTime;

public class MockAlertsService implements AlertsService {

  @Override
  public List<Alert> getAlerts(User user) {
    return Lists.newArrayList();
  }

  @Override
  public List<Alert> getAlertsAfter(DateTime after) {
    return Lists.newArrayList();
  }

  @Override
  public boolean delete(Alert alert) {
    return false;
  }

  @Override
  public boolean sendAlert(User to, String name, String url, String message) {
    return false;
  }

  @Override
  public boolean createFriendRequest(User user, User friend, Friend association) {
    return false;
  }

  @Override
  public void destroyFriendRequest(Friend association) {

  }

  @Override
  public boolean createFriendAccept(User user, User friend, Friend association) {
    return false;
  }
}
