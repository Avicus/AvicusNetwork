package net.avicus.atlas.runtimeconfig.fields;

import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.apache.commons.lang.StringUtils;

public class EnumField<T extends Enum<T>> extends ConfigurableField<T> {

    private final Class<T> enumCLazz;

    public EnumField(String name, Supplier<T> valueGetter,
        Consumer<T> valueModifier, Class<T> enumCLazz) {
        super(name, valueGetter, valueModifier);
        this.enumCLazz = enumCLazz;
    }

    public EnumField(String name, Class<T> enumCLazz) {
        super(name);
        this.enumCLazz = enumCLazz;
    }

    @Override
    public T parse(String... data) throws Exception {
        return Enum.valueOf(enumCLazz, StringUtils.join(data, '_').toUpperCase(Locale.ROOT));
    }

    @Override
    public String getValue(T value) throws Exception {
        return value.name();
    }
}
