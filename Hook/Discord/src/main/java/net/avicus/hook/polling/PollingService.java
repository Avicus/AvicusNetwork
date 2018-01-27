package net.avicus.hook.polling;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import net.avicus.hook.Main;
import net.avicus.magma.database.Database;
import net.avicus.quest.model.Model;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;

public abstract class PollingService<T extends Model> {

  final TextChannel destination;
  T lastResult;

  public PollingService(TextChannel destination) {
    this.destination = destination;
  }

  abstract Logger getLogger();

  abstract void poll(Database database);

  public void start() {
    getLogger().info("Starting polling service for " + destination.getName() + " at an interval of "
        + getPollingInterval());
    Main.getExecutor().scheduleAtFixedRate(new Runnable() {
      @Override
      public void run() {
        try {
          poll(Main.getHook().getDatabase());
        } catch (Exception e) {
          Main.getHook().sendStatusException("Polling Failed!", e);
          e.printStackTrace();
        }
      }
    }, 10, getPollingInterval(), TimeUnit.SECONDS);
  }

  abstract MessageEmbed getAlertMessage(T t);

  void sendAlert() {
    MessageEmbed embed = getAlertMessage(this.lastResult);
    this.destination.sendMessage(embed).complete();
    getLogger()
        .info("Sent alert to #" + this.destination.getName() + ": " + embed.getDescription());
  }

  int getPollingInterval() {
    return 30;
  }

  T getLastResult() {
    return lastResult;
  }
}
