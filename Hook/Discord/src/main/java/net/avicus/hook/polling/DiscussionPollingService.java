package net.avicus.hook.polling;

import java.awt.Color;
import java.util.Arrays;
import java.util.logging.Logger;
import lombok.Getter;
import net.avicus.hook.Main;
import net.avicus.magma.NetworkIdentification;
import net.avicus.magma.database.Database;
import net.avicus.magma.database.model.impl.Discussion;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.impl.MessageEmbedImpl;

public class DiscussionPollingService extends PollingService<Discussion> {

  private final int catId;
  @Getter
  private final Logger logger;

  public DiscussionPollingService(TextChannel destination, int categoryId) {
    super(destination);
    this.catId = categoryId;
    this.logger = Main
        .getLogger("Polling Service: Cat-" + this.catId + " Chan-" + this.destination.getName());
    this.lastResult = Main.getHook().getDatabase().getDiscussions().getLatest(this.catId);
  }

  @Override
  void poll(Database database) {
    Discussion latest = database.getDiscussions().getLatest(this.catId);
    if (latest == null) {
      return;
    }

    if (latest.getId() != this.lastResult.getId()) {
      this.lastResult = latest;
      sendAlert();
    }
  }

  @Override
  MessageEmbed getAlertMessage(Discussion discussion) {
    Database database = Main.getHook().getDatabase();
    return new MessageEmbedImpl()
        .setColor(Color.blue)
        .setTitle("Discussion Alert")
        .setDescription("A new discussion by " + discussion.getAuthor(database).getName()
            + " has been created in in " + discussion.getCatName(database))
        .setFields(
            Arrays.asList(new MessageEmbed.Field("Title", discussion.getTitle(database), true)))
        .setUrl(NetworkIdentification.URL + "/forums/discussions/" + discussion.getUuid());
  }
}
