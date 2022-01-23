package net.avicus.atlas.runtimeconfig.fields;

import java.util.function.Consumer;
import java.util.function.Supplier;
import org.apache.commons.lang3.StringUtils;

public class SimpleFields {

    public static class BooleanField extends ConfigurableField<Boolean> {

        public BooleanField(String name, Supplier<Boolean> valueGetter,
            Consumer<Boolean> valueModifier) {
            super(name, valueGetter, valueModifier);
        }

        @Override
        public Boolean parse(String... data) {
            return Boolean.valueOf(data[0]);
        }

        @Override
        public String getValue(Boolean value) {
            return Boolean.toString(value);
        }
    }

    public static class StringField extends ConfigurableField<String> {

        public StringField(String name, Supplier<String> valueGetter,
            Consumer<String> valueModifier) {
            super(name, valueGetter, valueModifier);
        }

        @Override
        public String parse(String... data) {
            return StringUtils.join(data, ' ');
        }

        @Override
        public String getValue(String value) {
            return value;
        }
    }

    public static class IntField extends ConfigurableField<Integer> {

        public IntField(String name, Supplier<Integer> valueGetter,
            Consumer<Integer> valueModifier) {
            super(name, valueGetter, valueModifier);
        }

        public IntField(String name) {
            super(name);
        }

        @Override
        public Integer parse(String... data) {
            return Integer.valueOf(data[0]);
        }

        @Override
        public String getValue(Integer value) {
            return Integer.toString(value);
        }
    }

    public static class DoubleField extends ConfigurableField<Double> {

        public DoubleField(String name, Supplier<Double> valueGetter,
            Consumer<Double> valueModifier) {
            super(name, valueGetter, valueModifier);
        }

        public DoubleField(String name) {
            super(name);
        }

        @Override
        public Double parse(String... data) {
            return Double.parseDouble(data[0]);
        }

        @Override
        public String getValue(Double value) {
            return Double.toString(value);
        }
    }

    public static class FloatField extends ConfigurableField<Float> {

        public FloatField(String name, Supplier<Float> valueGetter,
            Consumer<Float> valueModifier) {
            super(name, valueGetter, valueModifier);
        }

        public FloatField(String name) {
            super(name);
        }

        @Override
        public Float parse(String... data) {
            return Float.parseFloat(data[0]);
        }

        @Override
        public String getValue(Float value) {
            return Float.toString(value);
        }
    }
}
