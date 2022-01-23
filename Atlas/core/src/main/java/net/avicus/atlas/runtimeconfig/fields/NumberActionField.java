package net.avicus.atlas.runtimeconfig.fields;

import java.util.function.Consumer;
import java.util.function.Supplier;
import net.avicus.compendium.number.NumberAction;
import org.apache.commons.lang.StringUtils;

public class NumberActionField extends ConfigurableField<NumberAction> {

    public NumberActionField(String name,
        Supplier<NumberAction> valueGetter,
        Consumer<NumberAction> valueModifier) {
        super(name, valueGetter, valueModifier);
    }

    public NumberActionField(String name) {
        super(name);
    }

    @Override
    public NumberAction parse(String... data) throws Exception {
        String text = StringUtils.join(data).toLowerCase();

        switch (text) {
            case "none":
                return NumberAction.NONE;
            case "set":
                return NumberAction.SET;
            case "add":
                return NumberAction.ADD;
            case "subtract":
                return NumberAction.SUBTRACT;
            case "multiply":
                return NumberAction.MULTIPLY;
            case "divide":
                return NumberAction.DIVIDE;
            case "power":
                return NumberAction.POWER;
        }
        throw new IllegalArgumentException("Unknown action type");
    }

    @Override
    public String getValue(NumberAction value) throws Exception {
        return "1 -> " + value.perform(1, 1);
    }
}
