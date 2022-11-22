package de.claved.origin.spigot.commands;

import de.claved.origin.spigot.Origin;
import de.claved.origin.spigot.api.OriginPlayer;
import de.claved.origin.spigot.api.manager.NickManager;
import de.claved.origin.utils.enums.Rank;
import de.claved.origin.spigot.api.manager.OriginManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportWorldCommand extends Command {

    public TeleportWorldCommand() {
        super("teleportworld");
    }

    @Override
    public boolean execute(CommandSender commandSender, String arg, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("This command is only executable as a player!");
            return true;
        }
        OriginPlayer player = OriginManager.getInstance().getPlayer((Player) commandSender);
        if (player.hasPriorityAccess(Rank.ADMINISTRATOR.getPriority())) {
            if (args.length == 1) {
                World world = Bukkit.getWorld(args[0]);
                if (world == null) {
                    player.sendMessage(
                            Origin.getInstance().getPrefix() + "§7Es ist keine Welt mit dem Namen §c" + args[0] + " §7geladen",
                            Origin.getInstance().getPrefix() + "§7No world §cloaded §7with the name§8: §c" + args[0]
                    );
                    return true;
                }
                player.teleport(world.getSpawnLocation());
                player.sendMessage(
                        Origin.getInstance().getPrefix() + "§7Du wurdest §ateleportiert",
                        Origin.getInstance().getPrefix() + "§7You've been §ateleported"
                );
            } else {
                player.sendMessage(
                        Origin.getInstance().getPrefix() + "§7Bitte nutze: §f/teleportworld [Name]",
                        Origin.getInstance().getPrefix() + "§7Please use: §f/teleportworld [Name]"
                );
            }
        } else {
            player.sendMessage(
                    NickManager.getInstance().getPrefix() + "§7Du hast §ckeine §7Rechte diesen Befehl auszuführen!",
                    NickManager.getInstance().getPrefix() + "§7You §cdon't §7have permission to perform this command!"
            );
        }
        return false;
    }
}
