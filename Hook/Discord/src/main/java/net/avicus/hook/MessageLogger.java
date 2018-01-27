package net.avicus.hook;

import net.avicus.hook.utils.ChannelUtils;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class MessageLogger extends ListenerAdapter {

  @Override
  public void onMessageReceived(MessageReceivedEvent event) {
    if (event.getChannelType() != ChannelType.TEXT || HookConfig.isDebug()) {
      System.out.println(ChannelUtils.getConsoleMessage(event.getMessage(), true));
    }
  }
}
