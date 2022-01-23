package net.avicus.atlas.runtimeconfig.fields;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.avicus.compendium.StringUtil;
import net.avicus.compendium.inventory.MaterialMatcher;
import net.avicus.compendium.inventory.MultiMaterialMatcher;
import net.avicus.compendium.inventory.SingleMaterialMatcher;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;

public abstract class MaterialMatcherField<M extends MaterialMatcher> extends ConfigurableField<M> {

    public static class MatcherField extends MaterialMatcherField<MaterialMatcher> {

        public MatcherField(String name,
            Supplier<MaterialMatcher> valueGetter,
            Consumer<MaterialMatcher> valueModifier) {
            super(name, valueGetter, valueModifier);
        }

        public MatcherField(String name) {
            super(name);
        }

        @Override
        public MaterialMatcher parse(String... data) throws Exception {
            String[] mats = StringUtils.join(data, ' ').split(",");
            if (mats.length == 1) {
                return parseMatcher(mats[0]);
            } else {
                List<SingleMaterialMatcher> matchers = Lists.newArrayList();
                for (String mat : mats) {
                    matchers.add(parseMatcher(mat));
                }
                return new MultiMaterialMatcher(matchers);
            }
        }
    }

    public static class SingleMatcherField extends MaterialMatcherField<SingleMaterialMatcher> {

        public SingleMatcherField(String name,
            Supplier<SingleMaterialMatcher> valueGetter,
            Consumer<SingleMaterialMatcher> valueModifier) {
            super(name, valueGetter, valueModifier);
        }

        public SingleMatcherField(String name) {
            super(name);
        }

        @Override
        public SingleMaterialMatcher parse(String... data) throws Exception {
            return MaterialMatcherField.parseMatcher(StringUtils.join(data, ' '));
        }
    }

    public static class MultiMatcherField extends MaterialMatcherField<MultiMaterialMatcher> {

        public MultiMatcherField(String name,
            Supplier<MultiMaterialMatcher> valueGetter,
            Consumer<MultiMaterialMatcher> valueModifier) {
            super(name, valueGetter, valueModifier);
        }

        public MultiMatcherField(String name) {
            super(name);
        }

        @Override
        public MultiMaterialMatcher parse(String... data) throws Exception {
            String[] mats = StringUtils.join(data, ' ').split(",");
            List<SingleMaterialMatcher> matchers = Lists.newArrayList();
            for (String mat : mats) {
                matchers.add(parseMatcher(mat));
            }
            return new MultiMaterialMatcher(matchers);
        }
    }

    public MaterialMatcherField(String name, Supplier<M> valueGetter,
        Consumer<M> valueModifier) {
        super(name, valueGetter, valueModifier);
    }

    public MaterialMatcherField(String name) {
        super(name);
    }

    private static SingleMaterialMatcher parseMatcher(String mat) {
        if (mat.contains(":")) {
            Material material = Material.valueOf(mat.split(":")[0].replaceAll(" ", "_").toUpperCase());
            byte matData = Byte.parseByte(mat.split(":")[1]);
            return new SingleMaterialMatcher(material, matData);
        } else {
            Material material = Material.valueOf(mat.split(":")[0].replaceAll(" ", "_").toUpperCase());
            return new SingleMaterialMatcher(material);
        }
    }

    @Override
    public String getValue(M value) throws Exception {
        if (value instanceof SingleMaterialMatcher) {
            return ((SingleMaterialMatcher) value).describe();
        } else if (value instanceof MultiMaterialMatcher) {
            return StringUtil.listToEnglishCompound(
                ((MultiMaterialMatcher) value).getMatchers().stream()
                    .map(SingleMaterialMatcher::describe)
                    .collect(Collectors.toList())
            );
        }
        return "Unknown Matcher";
    }
}
