package net.avicus.atlas.runtimeconfig.fields;

import java.util.function.Consumer;
import java.util.function.Supplier;
import net.avicus.compendium.points.AngleProvider;
import net.avicus.compendium.points.StaticAngleProvider;
import net.avicus.compendium.points.TargetPitchProvider;
import net.avicus.compendium.points.TargetYawProvider;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.util.Vector;

public class AngleProviderField extends ConfigurableField<AngleProvider> {

    public AngleProviderField(String name,
        Supplier<AngleProvider> valueGetter,
        Consumer<AngleProvider> valueModifier) {
        super(name, valueGetter, valueModifier);
    }

    public AngleProviderField(String name) {
        super(name);
    }

    @Override
    public AngleProvider parse(String... data) throws Exception {
        return new StaticAngleProvider(Float.parseFloat(data[0]));
    }

    @Override
    public String getValue(AngleProvider value) throws Exception {
        if (value instanceof StaticAngleProvider) {
            return Float.toString(((StaticAngleProvider) value).getAngle());
        }
        if (value instanceof TargetPitchProvider) {
            Vector vec = ((TargetPitchProvider) value).getTarget();
            return " Looking Toward" + ChatColor.GOLD + " X:" + ChatColor.LIGHT_PURPLE + vec.getX() +
                ChatColor.GOLD + " Y:" + ChatColor.LIGHT_PURPLE + vec.getY() +
                ChatColor.GOLD + " Z:" + ChatColor.LIGHT_PURPLE + vec.getZ();
        }
        if (value instanceof TargetYawProvider) {
            Vector vec = ((TargetYawProvider) value).getTarget();
            return " Looking Toward" + ChatColor.GOLD + " X:" + ChatColor.LIGHT_PURPLE + vec.getX() +
                ChatColor.GOLD + " Y:" + ChatColor.LIGHT_PURPLE + vec.getY() +
                ChatColor.GOLD + " Z:" + ChatColor.LIGHT_PURPLE + vec.getZ();
        }
        return "Unknown Angle Provider (" + value.getClass().getSimpleName() + ")";
    }
}
