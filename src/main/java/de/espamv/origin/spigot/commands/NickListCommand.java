package de.claved.origin.spigot.commands;

import de.claved.origin.spigot.Origin;
import de.claved.origin.spigot.api.OriginPlayer;
import de.claved.origin.spigot.api.manager.NickManager;
import de.claved.origin.spigot.api.manager.OriginManager;
import de.claved.origin.utils.enums.Rank;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class NickListCommand extends Command {

    public NickListCommand() {
        super("nicklist");
    }

    @Override
    public boolean execute(CommandSender commandSender, String arg, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("This command is only executable as a player!");
            return true;
        }
        OriginPlayer player = OriginManager.getInstance().getPlayer((Player) commandSender);
        if (player.hasPriorityAccess(Rank.BUILDER.getPriority())) {
            if (args.length == 0) {
                HashMap<UUID, String> currentNicks = NickManager.getInstance().getCurrentNicks();

                if (currentNicks.size() == 0) {
                    player.sendMessage(
                            NickManager.getInstance().getPrefix() + "§7Derzeit ist §cniemand §7genickt",
                            NickManager.getInstance().getPrefix() + "§7Currently there is §cnobody §7nicked"
                    );
                    return true;
                }
                player.sendMessage(NickManager.getInstance().getPrefix());
                player.sendMessage(
                        NickManager.getInstance().getPrefix() + "§7Liste aller genickten Spieler auf dem Server§8:",
                        NickManager.getInstance().getPrefix() + "§7List of all nicked players on this server§8:"
                );
                currentNicks.forEach((uuid, name) -> {
                    Player players = Bukkit.getPlayer(uuid);
                    player.sendMessage(NickManager.getInstance().getPrefix() + "§7" + name + " §8➟ §e" + players.getName());
                });
                player.sendMessage(NickManager.getInstance().getPrefix());

            } else {
                player.sendMessage(
                        Origin.getInstance().getPrefix() + "§7Bitte nutze: §f/nicklist",
                        Origin.getInstance().getPrefix() + "§7Please use: §f/nicklist"
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
