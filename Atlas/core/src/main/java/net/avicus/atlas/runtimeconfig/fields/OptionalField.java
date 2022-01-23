package net.avicus.atlas.runtimeconfig.fields;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import lombok.Getter;

@Getter
public class OptionalField<T> extends ConfigurableField<Optional<T>> {

    private final ConfigurableField<T> baseField;

    public OptionalField(String name, Supplier<Optional<T>> valueGetter,
        Consumer<Optional<T>> valueModifier,
        ConfigurableField<T> baseField) {
        super(name, valueGetter, valueModifier);
        this.baseField = baseField;
    }


    @Override
    public Optional<T> parse(String... data) throws Exception {
        if (data[0].equalsIgnoreCase("$e"))
            return Optional.empty();
        return Optional.of(baseField.parse(data));
    }

    @Override
    public String getValue(Optional<T> value) throws Exception {
        if (value.isPresent()) {
            return baseField.getValue(value.get());
        }
        return "Not Configured";
    }
}
