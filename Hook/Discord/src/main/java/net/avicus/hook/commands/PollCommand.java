package net.avicus.hook.commands;

import java.awt.Color;
import java.util.List;
import net.avicus.hook.Main;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.impl.EmoteImpl;

public class PollCommand implements DiscordCommand {

  @Override
  public void execute(CommandContext context, List<String> args) throws Exception {
    if (args.isEmpty()) {
      context.getLocation().sendMessage("**Usage:** !poll <question>").complete();
      return;
    }

    if (context.getGuild().getIdLong() != context.getHook().getMainGuild().getIdLong()) {
      context.getLocation().sendMessage(
          "**OH NO!** This command can only be used in the main Avicus guild (custom emojis)")
          .complete();
      return;
    }

    String question = String.join(" ", args);
    Message sent = context.getLocation()
        .sendMessage(context.getHook()
            .generateRichMessage("Poll by " + context.getSender().getName(), question, Color.PINK))
        .complete();
    sent.addReaction(new EmoteImpl(326428521666838528L, Main.getHook().getMainGuild())).complete();
    sent.addReaction(new EmoteImpl(326428638603902977L, Main.getHook().getMainGuild())).complete();
    sent.addReaction(new EmoteImpl(326428260588191745L, Main.getHook().getMainGuild())).complete();

    context.getMessage().delete().complete();
  }
}
