package net.avicus.hook.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import net.avicus.hook.Hook;
import net.avicus.hook.utils.HookTask;
import net.avicus.hook.utils.Messages;
import net.avicus.magma.Magma;
import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.module.gadgets.Gadgets;
import net.avicus.magma.module.gadgets.crates.KeyGadget;
import net.avicus.magma.module.gadgets.crates.TypeManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Date;
import java.util.List;

public class GadgetCommands {

    @Command(aliases = {"key", "givekey"}, desc = "Give a player a crate key.", usage = "<player> <type>")
    @CommandPermissions("atlas.gadget.givekey")
    public static void giveKey(CommandContext cmd, CommandSender sender) {
        String query = cmd.getString(0);
        String type = cmd.getString(1);

        if (!List.of("alpha", "betta", "gamma").contains(type)) {
            sender.sendMessage(ChatColor.RED + "Invalid gadget type: " + type);
            return;
        }

        HookTask.of(() -> {
            User user = Hook.database().getUsers().findByName(query).orElse(null);
            if (user == null) {
                sender.sendMessage(Messages.ERROR_NO_PLAYERS.with(ChatColor.RED));
                return;
            }

            Magma.get().getMm().get(Gadgets.class)
                    .createBackpackGadget(user,
                            new KeyGadget(TypeManager.getType(type)).defaultContext(),
                            false, new Date());
        }).nowAsync();
    }
}
