package net.avicus.atlas.runtimeconfig.fields;

import java.util.function.Consumer;
import java.util.function.Supplier;
import net.avicus.compendium.commands.exception.TranslatableCommandErrorException;
import net.avicus.compendium.locale.text.UnlocalizedFormat;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.util.Vector;

public class VectorField extends ConfigurableField<Vector> {

    public VectorField(String name, Supplier<Vector> valueGetter,
        Consumer<Vector> valueModifier) {
        super(name, valueGetter, valueModifier);
    }

    public VectorField(String name) {
        super(name);
    }

    @Override
    public Vector parse(String... data) throws Exception {
        if (data.length != 3) throw new TranslatableCommandErrorException(new UnlocalizedFormat("Vectors must be written as X Y Z"));
        double x = Double.parseDouble(data[0]);
        double y = Double.parseDouble(data[1]);
        double z = Double.parseDouble(data[2]);
        return new Vector(x, y, z);
    }

    @Override
    public String getValue(Vector value) {
        return ChatColor.GOLD + " X:" + ChatColor.LIGHT_PURPLE + value.getX() +
            ChatColor.GOLD + " Y:" + ChatColor.LIGHT_PURPLE + value.getY() +
            ChatColor.GOLD + " Z:" + ChatColor.LIGHT_PURPLE + value.getZ();
    }
}
