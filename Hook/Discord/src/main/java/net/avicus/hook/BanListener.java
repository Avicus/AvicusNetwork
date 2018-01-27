package net.avicus.hook;

import java.util.Date;
import java.util.Optional;
import net.avicus.magma.database.model.impl.User;
import net.avicus.quest.query.Operator;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class BanListener extends ListenerAdapter {

  public static boolean isBanned(User user) {
    Hook hook = Main.getHook();
    boolean banned = false;
    banned = !hook.getDatabase().getPunishments().select()
        .where("user_id", user.getId())
        .where("type", "discord_tempban").where("expires", new Date(), Operator.GREATER)
        .where("appealed", false).execute().isEmpty();
    banned = banned || !hook.getDatabase().getPunishments().select()
        .where("user_id", user.getId())
        .where("type", "discord_ban").where("appealed", false).execute().isEmpty();
    return banned;
  }

  @Override
  public void onGuildMemberJoin(GuildMemberJoinEvent event) {
    Hook hook = Main.getHook();
    Optional<User> found = hook.getDatabase().getUsers().findByDiscord(event.getUser().getIdLong());
    found.ifPresent(f -> {
      if (isBanned(f)) {
        event.getGuild().getController().kick(event.getMember(), "Joined while banned!").complete();
      }
    });
  }
}
