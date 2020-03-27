package net.avicus.magma.api.service;

import java.util.List;
import net.avicus.magma.api.graph.types.alert.Alert;
import net.avicus.magma.database.model.impl.Friend;
import net.avicus.magma.database.model.impl.User;
import org.joda.time.DateTime;

public interface AlertsService {

  List<Alert> getAlerts(User user);

  List<Alert> getAlertsAfter(DateTime after);

  boolean delete(Alert alert);

  boolean sendAlert(User to, String name, String url, String message);

  boolean createFriendRequest(User user, User friend, Friend association);

  void destroyFriendRequest(Friend association);

  boolean createFriendAccept(User user, User friend, Friend association);
}
