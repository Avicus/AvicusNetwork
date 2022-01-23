package net.avicus.atlas.runtimeconfig.fields;

import java.util.function.Consumer;
import java.util.function.Supplier;
import net.avicus.compendium.StringUtil;
import org.joda.time.Duration;

public class DurationField extends ConfigurableField<Duration> {

    public DurationField(String name, Supplier<Duration> valueGetter,
        Consumer<Duration> valueModifier) {
        super(name, valueGetter, valueModifier);
    }

    public DurationField(String name) {
        super(name);
    }

    @Override
    public Duration parse(String... data) throws Exception {
        return net.avicus.magma.util.StringUtil.parsePeriod(data[0]).toStandardDuration();
    }

    @Override
    public String getValue(Duration value) throws Exception {
        return StringUtil.secondsToClock((int) value.getStandardSeconds());
    }
}
