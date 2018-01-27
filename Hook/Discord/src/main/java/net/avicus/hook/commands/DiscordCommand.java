package net.avicus.hook.commands;


import java.util.List;
import lombok.Data;
import net.avicus.hook.Hook;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

@FunctionalInterface
public interface DiscordCommand {

  void execute(CommandContext context, List<String> args) throws Exception;

  @Data
  public class CommandContext {

    private final User sender;
    private final Message message;
    private final MessageChannel location;
    private final Hook hook;
    private final Guild guild;
  }
}
