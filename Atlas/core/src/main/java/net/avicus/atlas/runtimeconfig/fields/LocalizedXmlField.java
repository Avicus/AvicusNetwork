package net.avicus.atlas.runtimeconfig.fields;

import java.util.function.Consumer;
import java.util.function.Supplier;
import net.avicus.atlas.module.locales.LocalizedXmlString;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang3.StringUtils;

public class LocalizedXmlField extends ConfigurableField<LocalizedXmlString> {

    public LocalizedXmlField(String name,
        Supplier<LocalizedXmlString> valueGetter,
        Consumer<LocalizedXmlString> valueModifier) {
        super(name, valueGetter, valueModifier);
    }

    @Override
    public LocalizedXmlString parse(String... data) throws Exception {
        return new LocalizedXmlString(ChatColor.translateAlternateColorCodes('&', StringUtils.join(data, ' ')));
    }

    @Override
    public String getValue(LocalizedXmlString value) throws Exception {
        return value.translateDefault();
    }
}
