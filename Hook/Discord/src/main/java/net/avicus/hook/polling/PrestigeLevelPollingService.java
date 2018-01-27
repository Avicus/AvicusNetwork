package net.avicus.hook.polling;

import java.awt.Color;
import java.util.ArrayList;
import java.util.logging.Logger;
import lombok.Getter;
import net.avicus.hook.Main;
import net.avicus.magma.database.Database;
import net.avicus.magma.database.model.impl.PrestigeLevel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.impl.MessageEmbedImpl;

public class PrestigeLevelPollingService extends PollingService<PrestigeLevel> {

  @Getter
  private final Logger logger;

  public PrestigeLevelPollingService(TextChannel destination) {
    super(destination);
    this.logger = Main.getLogger("Polling Service: Prestige Levels");
    this.lastResult = Main.getHook().getDatabase().getPrestigeLevels().getLatest();
  }

  @Override
  void poll(Database database) {
    PrestigeLevel latest = database.getPrestigeLevels().getLatest();
    if (latest == null) {
      return;
    }

    if (latest.getId() != this.lastResult.getId()) {
      this.lastResult = latest;
      sendAlert();
    }
  }

  @Override
  MessageEmbed getAlertMessage(PrestigeLevel level) {
    Database database = Main.getHook().getDatabase();
    return new MessageEmbedImpl()
        .setColor(Color.GREEN)
        .setTitle("Prestige Alert")
        .setDescription(
            level.getUser(database).getName() + " has leveled up to prestige " + level.getLevel()
                + "!")
        .setFields(new ArrayList<>());
  }
}
