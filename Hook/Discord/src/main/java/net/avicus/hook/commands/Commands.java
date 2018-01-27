package net.avicus.hook.commands;

import com.google.common.collect.Lists;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import lombok.Getter;
import net.avicus.hook.Hook;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class Commands extends ListenerAdapter {

  @Getter
  private final Hook hook;
  @Getter
  private HashMap<String, DiscordCommand> commands = new HashMap<>();

  public Commands(Hook hook) {
    this.hook = hook;
    commands.put("help", (context, args) -> {
      List<MessageEmbed.Field> entries = Lists.newArrayList();
      entries.add(new MessageEmbed.Field("!counts", "View player counts for servers.", true));
      entries.add(new MessageEmbed.Field("!poll", "Create a poll.", true));
      entries.add(
          new MessageEmbed.Field("!unregister", "Unlink your account with your Avicus user.",
              true));
      entries.add(
          new MessageEmbed.Field("!register", "Link your account with your Avicus user.", true));
      MessageEmbed help = hook
          .generateRichMessage("HELP", "Various commands that the bot can use", Color.CYAN,
              entries);
      context.getLocation().sendMessage(help).complete();
    });
  }

  @Override
  public void onMessageReceived(MessageReceivedEvent event) {
    Guild guild = event.getGuild();
    if (!(hook.getMainGuild().equals(guild) || hook.getTmGuild().equals(guild))) {
      return;
    }

    if (!event.getMessage().getContent().startsWith("!")
        || event.getMessage().getContent().length() <= 1) {
      return;
    }

    java.util.List<String> args = new ArrayList<String>(
        Arrays.asList(event.getMessage().getContent().replace("!", "").split(" ")));

    String commandIdent = args.get(0);

    DiscordCommand command = commands.get(args.get(0));

    if (command != null) {
      hook.getLog().info("Command: " + event.getMessage().getContent());
      try {
        if (args.size() > 1) {
          args.remove(0);
        } else {
          args = new ArrayList<>();
        }
        command.execute(
            new DiscordCommand.CommandContext(event.getAuthor(), event.getMessage(),
                event.getChannel(), this.hook,
                event.getGuild()), args);
      } catch (Exception ex) {
        ex.printStackTrace();
        hook.sendStatusException("Error executing command " + commandIdent, ex);
      }
    } else {
      event.getChannel().sendMessage("Command not found!\nUse !help for a list of commands.")
          .complete();
    }
  }
}
