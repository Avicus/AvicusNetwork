package net.avicus.hook.sessions;

import com.sk89q.bukkit.util.CommandsManagerRegistration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.locale.text.LocalizableFormat;
import net.avicus.compendium.locale.text.LocalizedTime;
import net.avicus.compendium.locale.text.UnlocalizedComponent;
import net.avicus.compendium.locale.text.UnlocalizedFormat;
import net.avicus.compendium.locale.text.UnlocalizedText;
import net.avicus.hook.Hook;
import net.avicus.hook.HookConfig.Server.SessionLogging;
import net.avicus.hook.utils.Events;
import net.avicus.hook.utils.Messages;
import net.avicus.magma.database.model.impl.Server;
import net.avicus.magma.database.model.impl.Session;
import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.network.rtp.RTPHelpers;
import net.avicus.magma.network.user.Users;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.joda.time.Instant;
import org.joda.time.Seconds;

public class Sessions {

  private static final String ARROW = "➠";
  private static HashMap<User, Session> sessions = new HashMap<>();

  public static void init(CommandsManagerRegistration cmds) {
    if (SessionLogging.isEnabled()) {
      Events.register(new SessionListener());
      new SessionUpdateTask().start();
    }
    cmds.register(FindCommand.class);
  }

  private static Date nowWithPadding() {
    int seconds = SessionLogging.getPadding();
    return Instant.now().plus(Seconds.seconds(seconds).toStandardDuration()).toDate();
  }

  public static void create(User user, String ip, Server server) {
    Date now = new Date();

    Session session = new Session(user.getId(), ip, server.getId(), now, nowWithPadding());
    Hook.database().getSessions().insert(session).execute();

    sessions.put(user, session);
  }

  public static void updateAll() {
    Map<User, Session> copy = new HashMap<>();
    copy.putAll(sessions);

    Date expires = nowWithPadding();

    for (Map.Entry<User, Session> entry : copy.entrySet()) {
      update(entry.getValue(), expires, false);
    }
  }

  public static void update(Session session, Date expiredAt, boolean graceful) {
    session.updateExpiredAt(Hook.database(), expiredAt, graceful);
  }

  public static void end(User user) {
    Session session = sessions.remove(user);

    if (session != null) {
      update(session, new Date(), true);
    } else {
      throw new RuntimeException();
    }
  }

  /**
   * Get a last seen message for a user and lookup their last session.
   */
  public static Localizable formatLastSeen(CommandSender viewer, User user) {
    Optional<Session> latest = Hook.database().getSessions().findLatest(user.getId());
    return formatLastSeen(viewer, user, latest);
  }

  /**
   * Get a last seen message for a user and already found session model.
   */
  public static Localizable formatLastSeen(CommandSender viewer, User user,
      Optional<Session> lastSession) {
    LocalizableFormat format = new UnlocalizedFormat(" {0} {1}");

    Localizable symbol;
    Localizable status;

    if (!lastSession.isPresent()) {
      symbol = new UnlocalizedText(ARROW, ChatColor.GRAY);
      status = Messages.UI_NOT_ONLINE.with(Users.getLocalizedDisplay(user));
    } else if (lastSession.get().isActive()) {
      Optional<Server> server = Hook.database().getServers()
          .findById(lastSession.get().getServerId());
      if (!server.isPresent()) {
        server = Optional.of(new Server("N/A", null, 0, false));
      }
      symbol = new UnlocalizedText(ARROW, ChatColor.GREEN);
      Localizable serverName = new UnlocalizedText(server.get().getName(), ChatColor.GOLD);
      status = Messages.UI_CURRENTLY_ON.with(new UnlocalizedComponent(RTPHelpers
              .permissibleClickablePlayer(viewer, server.get(), user, viewer.getLocale(), true)),
          serverName);
    } else {
      symbol = new UnlocalizedText(ARROW, ChatColor.RED);
      Localizable lastSeen = new LocalizedTime(lastSession.get().getExpiredAt());
      status = Messages.UI_LAST_SEEN.with(Users.getLocalizedDisplay(user), lastSeen);
    }

    return format.with(ChatColor.WHITE, symbol, status);
  }
}
