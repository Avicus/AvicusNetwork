package net.avicus.atlas.runtimeconfig;

import com.google.common.collect.Lists;
import java.util.List;
import net.avicus.atlas.runtimeconfig.fields.ConfigurableField;
import org.bukkit.command.CommandSender;

public interface RuntimeConfigurable {

    default ConfigurableField[] getFields() {
        return new ConfigurableField[0];
    }

    String getDescription(CommandSender viewer);

    default List<RuntimeConfigurable> getChildren() {
        return Lists.newArrayList();
    }

    default void onFieldChange(String name) {

    }
}
