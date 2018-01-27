package net.avicus.hook.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.avicus.compendium.StringUtil;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.TextChannel;

public class ChannelUtils {

  public static void logHistory(TextChannel channel) {
    System.out.println("History for #" + channel.getName());
    List<Message> hist = new ArrayList<>(channel.getHistory().retrievePast(100).complete());
    Collections.reverse(hist);
    hist.forEach(m -> {
      System.out.println(getConsoleMessage(m, false));
    });
  }

  public static String getConsoleMessage(Message message, boolean showChan) {
    StringBuilder res = new StringBuilder();
    if (showChan) {
      res.append("#" + message.getChannel().getName() + " ");
    }
    res.append(message.getAuthor().getName());
    res.append(": ");
    res.append(message.getContent());
    if (!message.getReactions().isEmpty()) {
      res.append(" REACTIONS: " + StringUtil
          .join(message.getReactions(), " | ", new StringUtil.Stringify<MessageReaction>() {
            @Override
            public String on(MessageReaction object) {
              return object.getEmote().getName() + " (" + object.getCount() + ")";
            }
          }));
    }
    return res.toString();
  }
}
