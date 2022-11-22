package de.claved.origin.spigot.commands;

import de.claved.origin.spigot.Origin;
import de.claved.origin.spigot.api.OriginPlayer;
import de.claved.origin.spigot.api.manager.OriginManager;
import de.claved.origin.spigot.api.manager.ReportManager;
import de.claved.origin.utils.enums.Rank;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReportsCommand extends Command {

    public ReportsCommand() {
        super("reports");
    }

    @Override
    public boolean execute(CommandSender commandSender, String arg, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("This command is only executable as a player!");
            return true;
        }
        OriginPlayer player = OriginManager.getInstance().getPlayer((Player) commandSender);
        if (player.hasPriorityAccess(Rank.SUPPORTER.getPriority())) {
            if (args.length == 0) {
                ReportManager.getInstance().openGUI(player);
            } else {
                player.sendMessage(
                        Origin.getInstance().getPrefix() + "§7Bitte nutze: §f/reports",
                        Origin.getInstance().getPrefix() + "§7Please use: §f/reports"
                );
            }
        } else {
            player.sendMessage(
                    Origin.getInstance().getPrefix() + "§7Du hast §ckeine §7Rechte diesen Befehl auszuführen!",
                    Origin.getInstance().getPrefix() + "§7You §cdon't §7have permission to perform this command!"
            );
        }
        return false;
    }
}
