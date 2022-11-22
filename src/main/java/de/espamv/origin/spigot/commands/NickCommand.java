package de.claved.origin.spigot.commands;

import de.claved.cloud.CloudAPI;
import de.claved.origin.spigot.Origin;
import de.claved.origin.spigot.api.OriginPlayer;
import de.claved.origin.spigot.api.manager.NickManager;
import de.claved.origin.spigot.api.manager.OriginManager;
import de.claved.origin.utils.enums.Rank;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NickCommand extends Command {

    public NickCommand() {
        super("nick");
    }

    @Override
    public boolean execute(CommandSender commandSender, String arg, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("This command is only executable as a player!");
            return true;
        }
        OriginPlayer player = OriginManager.getInstance().getPlayer((Player) commandSender);
        if (player.hasPriorityAccess(Rank.PREMIUMPLUS.getPriority())) {
            if (args.length == 0) {
                if (!CloudAPI.getInstance().getLocalServer().getName().toLowerCase().contains("lobby")) {
                    if (NickManager.getInstance().getNickName(player.getUniqueId()) == null) {
                        Bukkit.getScheduler().runTaskAsynchronously(Origin.getInstance(), () -> NickManager.getInstance().nickPlayer(player));
                    } else {
                        Bukkit.getScheduler().runTaskAsynchronously(Origin.getInstance(), () -> NickManager.getInstance().unnickPlayer(player));
                    }
                } else {
                    player.sendMessage(
                            NickManager.getInstance().getPrefix() + "§7Du kannst dich auf der Lobby §cnicht §7nicken!",
                            NickManager.getInstance().getPrefix() + "§7You §ccan't §7change your nickname on a lobby!"
                    );
                }
            } else {
                player.sendMessage(
                        Origin.getInstance().getPrefix() + "§7Bitte nutze: §f/nick",
                        Origin.getInstance().getPrefix() + "§7Please use: §f/nick"
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
