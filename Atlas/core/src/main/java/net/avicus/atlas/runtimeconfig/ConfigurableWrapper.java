package net.avicus.atlas.runtimeconfig;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sk89q.minecraft.util.commands.CommandException;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import net.avicus.atlas.runtimeconfig.fields.ConfigurableField;
import net.avicus.compendium.commands.exception.TranslatableCommandErrorException;
import net.avicus.compendium.locale.text.UnlocalizedFormat;

@Getter
public class ConfigurableWrapper {

    private final RuntimeConfigurable configurable;
    private final String id;
    private final Map<String, ConfigurableField> fields = Maps.newHashMap();
    private final List<ConfigurableWrapper> children = Lists.newArrayList();

    public ConfigurableWrapper(RuntimeConfigurable configurable, String id) {
        this.configurable = configurable;
        this.id = id;
        for (ConfigurableField field : configurable.getFields()) {
            this.fields.put(field.getId(), field);
        }
    }

    public void addChild(ConfigurableWrapper wrapper) {
        this.children.add(wrapper);
    }

    public String configure(String field, String... data) throws CommandException {
        if (!fields.containsKey(field)) throw new TranslatableCommandErrorException(new UnlocalizedFormat("This configurable does not contain a field with ID " + id));

        ConfigurableField found = fields.get(field);
        try {
            found.configure(found.parse(data));
            return found.getDescription();
        } catch (Exception e) {
            e.printStackTrace();
            throw new TranslatableCommandErrorException(new UnlocalizedFormat(e.getMessage()));
        }
    }
}
