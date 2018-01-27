package net.avicus.atlas.sets.generator;

import com.google.common.collect.Maps;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.documentation.FeatureDocumentation;
import net.avicus.atlas.documentation.ModuleDocumentation;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.located.LocatedJDOMFactory;

public class GenerateCommand {

  @SuppressWarnings("all") // I KNOW IM IGNOREING FILE CALLS, I DON'T CARE
  @Command(aliases = "generatedocs", desc = "Generate documentation files.")
  public static void genDocs(CommandContext cmd, final CommandSender sender) {
    if (!(sender instanceof ConsoleCommandSender)) {
      sender.sendMessage(ChatColor.RED + "Nice try peasant!");
    }

    try {
      List<ModuleDocumentation> documentations = Atlas.get().getMatchFactory().getDocumentation();

      sender.sendMessage(ChatColor.GOLD + "Beginning documentation creation.");

      File location = new File(Atlas.get().getDataFolder(), "docs");
      location.mkdirs();
      location.createNewFile();
      File modules = new File(location, "modules");
      modules.mkdirs();
      modules.createNewFile();

      File examples = new File(location, "examples.xml");
      if (!examples.exists()) {
        GenerationUtils.populateExamples(examples, documentations);
      }

      sender.sendMessage(ChatColor.GOLD + "Generating change log...");
      GenerationUtils.writeHistory(location,
          documentations.stream().flatMap(d -> d.getSpecInfo().stream())
              .collect(Collectors.toList()));
      sender.sendMessage(ChatColor.GREEN + "Finished generating change log!");

      final SAXBuilder sax = new SAXBuilder();
      sax.setJDOMFactory(new LocatedJDOMFactory());
      Document document = sax.build(examples);

      Element parent = document.getRootElement();

      HashMap<FeatureDocumentation, Element> ex = Maps.newHashMap();
      for (ModuleDocumentation d : documentations) {
        Element documentation = parent.getChild(d.getSafeName());
        if (documentation == null) {
          throw new RuntimeException("No documentation for " + d.getSafeName());
        }
        for (FeatureDocumentation f : d.getFeatures()) {
          ex.putAll(GenerationUtils.mapExamples(f, documentation.getChild(f.getSafeName())));
        }
      }

      sender.sendMessage(ChatColor.GOLD + "Writing module config.");
      File config = new File(location, "modules.yml");
      config.createNewFile();
      GenerationUtils.generateConfig(config);
      sender.sendMessage(ChatColor.GREEN + "Finished writing module config.");

      sender.sendMessage(ChatColor.GOLD + "Beginning module pages generation...");
      for (ModuleDocumentation documentation : documentations) {
        GenerationUtils.writeFile(modules, documentation, sender, ex);
      }
      sender.sendMessage(ChatColor.GREEN + "Finished module pages generation!");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
