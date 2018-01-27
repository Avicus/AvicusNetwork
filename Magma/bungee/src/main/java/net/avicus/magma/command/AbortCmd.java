package net.avicus.magma.command;

import com.google.common.base.Joiner;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;

public class AbortCmd extends Command {

  public AbortCmd() {
    super("abort", "no.one.has.this.permission");
  }

  @Override
  public void execute(CommandSender sender, String[] args) {
    if (args.length == 0) {
      ProxyServer.getInstance().stop();
    } else {
      ProxyServer.getInstance().stop(Joiner.on(' ').join(args));
    }
  }
}
