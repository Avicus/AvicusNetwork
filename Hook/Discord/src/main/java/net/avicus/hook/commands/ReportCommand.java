package net.avicus.hook.commands;

import java.awt.Color;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import net.avicus.compendium.Time;
import net.avicus.hook.utils.UserUtils;
import net.avicus.magma.database.model.impl.Report;

public class ReportCommand implements DiscordCommand {

  @Override
  public void execute(CommandContext context, List<String> args) throws Exception {
    if (!UserUtils.hasRoleOrHigher(context.getSender(),
        context.getGuild().getRolesByName("Moderator", false).get(0))) {
      context.getLocation()
          .sendMessage("**Error:** You do not have permission to use this command!").complete();
      return;
    }

    List<Report> reports = context.getHook().getDatabase().getReports()
        .getRecentReports(Optional.empty(), Optional.empty());
    reports = reports.subList(0, Math.min(10, reports.size()));
    StringBuilder reportDisplay = new StringBuilder();
    reports.forEach(report -> {
      reportDisplay.append("**");
      reportDisplay.append(report.getServer());
      reportDisplay.append("**: `");
      reportDisplay.append(
          context.getHook().getDatabase().getUsers().findById(report.getCreatorId()).get()
              .getName());
      reportDisplay.append("` reported `");
      reportDisplay.append(
          context.getHook().getDatabase().getUsers().findById(report.getReportedId()).get()
              .getName());
      reportDisplay.append("` for `");
      reportDisplay.append(report.getReason());
      reportDisplay.append(" `");
      reportDisplay.append(Time.prettyTime(Locale.ENGLISH).format(report.getCreatedAt()));
      reportDisplay.append("\n");
    });
    context.getLocation().sendMessage(context.getHook()
        .generateRichMessage("Recent Reports", reportDisplay.toString(), Color.LIGHT_GRAY))
        .complete();
  }
}
