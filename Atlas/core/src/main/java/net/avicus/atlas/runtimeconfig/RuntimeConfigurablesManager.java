package net.avicus.atlas.runtimeconfig;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import net.avicus.atlas.runtimeconfig.fields.ConfigurableField;
import net.avicus.compendium.commands.exception.TranslatableCommandErrorException;
import net.avicus.compendium.locale.text.UnlocalizedFormat;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;

public class RuntimeConfigurablesManager {

    private final Map<String, ConfigurableWrapper> registry;
    private final List<ConfigurableWrapper> roots = Lists.newArrayList();
    private final AtomicInteger idIncrementer = new AtomicInteger(0);

    public RuntimeConfigurablesManager() {
        this.registry = Maps.newHashMap();
    }

    public void registerConfigurable(RuntimeConfigurable configurable, ConfigurableWrapper parent) {
        String id = Integer.toString(this.idIncrementer.incrementAndGet());
        ConfigurableWrapper wrapper = new ConfigurableWrapper(configurable, id);
        this.registry.put(id, wrapper);
        if (parent != null) {
            parent.addChild(wrapper);
        } else
            roots.add(wrapper);
        for (RuntimeConfigurable child : configurable.getChildren()) {
            registerConfigurable(child, wrapper);
        }
    }

    public void reset() {
        this.roots.clear();
        this.registry.clear();
        idIncrementer.set(0);
    }

    public ConfigurableWrapper getWrapper(String id) throws TranslatableCommandErrorException {
        if (this.registry.containsKey(id)) {
            return this.registry.get(id);
        }
        throw new TranslatableCommandErrorException(
            new UnlocalizedFormat("No configurable with ID " + id));
    }

    public void describeAll(CommandSender viewer) {
        for (ConfigurableWrapper wrapper : this.roots) {
            describe(viewer, wrapper, false);
        }
    }

    public void describe(CommandSender viewer, ConfigurableWrapper wrapper, boolean showFields) {
        RuntimeConfigurable configurable = wrapper.getConfigurable();
        BaseComponent description = new TextComponent(ChatColor.BLUE + configurable.getDescription(viewer));
        description
            .setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/rt view " + wrapper.getId()));
        description.setHoverEvent(
            new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to view").color(
                ChatColor.GOLD).create()));
        viewer.sendMessage(description);
        if (showFields) {
            for (Map.Entry<String, ConfigurableField> entry : wrapper.getFields().entrySet()) {
                TextComponent fieldComponent = new TextComponent("  ");
                BaseComponent fieldDescription = new TextComponent(entry.getValue().getDescription());
                fieldDescription
                    .setClickEvent(new ClickEvent(Action.SUGGEST_COMMAND, "/rt config " + wrapper.getId() + " " + entry.getKey() + " "));
                fieldDescription.setHoverEvent(
                    new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to edit").color(
                        ChatColor.GOLD).create()));
                fieldComponent.addExtra(fieldDescription);
                viewer.sendMessage(fieldComponent);
            }
            for (ConfigurableWrapper child : wrapper.getChildren()) {
                describe(viewer, child, false);
            }
        }
    }
}
