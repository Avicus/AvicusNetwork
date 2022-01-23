package net.avicus.atlas.runtimeconfig.fields;

import com.sk89q.minecraft.util.commands.CommandException;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Supplier;
import lombok.Getter;
import net.avicus.compendium.commands.exception.TranslatableCommandErrorException;
import net.avicus.compendium.locale.text.UnlocalizedFormat;
import net.md_5.bungee.api.ChatColor;

@Getter
public abstract class ConfigurableField<T> {

    private final String id;
    private final String name;
    private final Supplier<T> valueGetter;
    private final Consumer<T> valueModifier;

    public ConfigurableField(String name, Supplier<T> valueGetter,
        Consumer<T> valueModifier) {
        this.id = name.toLowerCase(Locale.ROOT).replaceAll(" ", "-");
        this.name = name;
        this.valueGetter = valueGetter;
        this.valueModifier = valueModifier;
    }

    public ConfigurableField(String name) {
        this(name, null, null);
    }

    public void configure(T data) throws CommandException {
        if (this.valueModifier == null) throw new IllegalStateException("not supported");
        try {
            this.valueModifier.accept(data);
        } catch (Exception e) {
            throw new TranslatableCommandErrorException(new UnlocalizedFormat(e.getMessage()));
        }
    }

    public String getDescription() {
        if (this.valueGetter == null) throw new IllegalStateException("not supported");
        try {
            return ChatColor.AQUA + name + ChatColor.WHITE + ": " + ChatColor.LIGHT_PURPLE + getValue(valueGetter.get());
        } catch (Exception e) {
            e.printStackTrace();
            return ChatColor.RED + "Error";
        }
    }

    public abstract T parse(String... data) throws Exception;

    public abstract String getValue(T value) throws Exception;
}
