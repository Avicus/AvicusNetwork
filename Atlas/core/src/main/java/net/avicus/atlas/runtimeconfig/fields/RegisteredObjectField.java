package net.avicus.atlas.runtimeconfig.fields;

import java.util.function.Consumer;
import java.util.function.Supplier;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.match.Match;
import net.avicus.compendium.commands.exception.TranslatableCommandErrorException;
import net.avicus.compendium.locale.text.UnlocalizedFormat;
import org.apache.commons.lang.StringUtils;

public class RegisteredObjectField<T> extends ConfigurableField<T> {

    private final Class<T> objectType;

    public RegisteredObjectField(String name, Supplier<T> valueGetter,
        Consumer<T> valueModifier, Class<T> objectType) {
        super(name, valueGetter, valueModifier);
        this.objectType = objectType;
    }

    public RegisteredObjectField(String name, Class<T> objectType) {
        super(name);
        this.objectType = objectType;
    }

    @Override
    public T parse(String... data) throws Exception {
        try {
            Match match = Atlas.getMatch();

            return match.getRegistry().get(this.objectType, StringUtils.join(data, '-'), true).get();
        } catch (Exception e) {
            throw new TranslatableCommandErrorException(new UnlocalizedFormat(e.getMessage()));
        }
    }

    @Override
    public String getValue(T value) throws Exception {
        return value.getClass().getSimpleName();
    }
}
