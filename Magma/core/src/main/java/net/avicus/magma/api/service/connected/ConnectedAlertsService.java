package net.avicus.magma.api.service.connected;

import com.google.common.collect.Lists;
import java.util.List;
import net.avicus.magma.api.APIClient;
import net.avicus.magma.api.graph.QLBuilder;
import net.avicus.magma.api.graph.inputs.AlertDeleteInput;
import net.avicus.magma.api.graph.inputs.AlertSendInput;
import net.avicus.magma.api.graph.mutations.alert_send.AlertSendQuery;
import net.avicus.magma.api.graph.types.alert.Alert;
import net.avicus.magma.api.service.AlertsService;
import net.avicus.magma.api.service.Service;
import net.avicus.magma.database.model.impl.Friend;
import net.avicus.magma.database.model.impl.User;
import org.joda.time.DateTime;

public class ConnectedAlertsService extends Service<Alert> implements AlertsService {

  public ConnectedAlertsService(APIClient client) {
    super(client, "alerts");
  }

  @Override
  public List<Alert> getAlerts(User user) {
    return findMany(
        q -> q.alerts(args -> args.userId(user.getId()),
            ret -> ret.id().url().createdAt().seen().message()));
  }

  @Override
  public List<Alert> getAlertsAfter(DateTime after) {
    return getClient().query(QLBuilder
        .query(q -> q.allAfterAlert(args -> args.createdAt(after),
            ret -> ret.url().createdAt().userId().seen().id().message())))
        .map(a -> a.getAllAfterAlert()).orElse(
            Lists.newArrayList());
  }

  @Override
  public boolean delete(Alert alert) {
    return getClient().mutation(QLBuilder
        .mutation(m -> m.alertDelete(new AlertDeleteInput(alert.getId()), a -> a.success())))
        .map(d -> d.getData().getAlertDelete().isSuccess()).orElse(false);
  }

  @Override
  public boolean sendAlert(User to, String name, String url, String message) {
    return getClient().mutation(QLBuilder
        .mutation(m -> m.alertSend(new AlertSendInput(to.getId(), name, url, message),
            AlertSendQuery::success)))
        .map(d -> d.getData().getAlertSend().isSuccess()).orElse(false);
  }

  @Override
  public boolean createFriendRequest(User user, User friend, Friend association) {
    String name = "Friend:" + association.getId();
    String message = String.format("%s has requested to be your friend.", friend.getName());

    return sendAlert(user, name, friend.getProfile(false), message);
  }

  @Override
  public void destroyFriendRequest(Friend association) {
    String name = "Friend:" + association.getId();
    findOne(q -> q.alerts(d -> d.name(name), a -> a.id())).ifPresent(this::delete);
  }

  @Override
  public boolean createFriendAccept(User user, User friend, Friend association) {
    String name = "NewFriend:" + association.getId();
    String message = String.format("%s has accepted your friend request!", friend.getName());

    return sendAlert(user, name, friend.getProfile(false), message);
  }
}
