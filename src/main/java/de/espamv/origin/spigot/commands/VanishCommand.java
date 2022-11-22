package de.claved.origin.spigot.commands;

import de.claved.origin.spigot.Origin;
import de.claved.origin.spigot.api.OriginPlayer;
import de.claved.origin.spigot.api.manager.OriginManager;
import de.claved.origin.utils.enums.Rank;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class VanishCommand extends Command {

    public VanishCommand() {
        super("vanish");

        List<String> aliases = new ArrayList<>();
        aliases.add("spectate");
        aliases.add("spec");
        aliases.add("v");

        setAliases(aliases);
    }

    @Override
    public boolean execute(CommandSender commandSender, String arg, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("This command is only executable as a player!");
            return true;
        }
        OriginPlayer player = OriginManager.getInstance().getPlayer((Player) commandSender);
        if (player.hasPriorityAccess(Rank.SRMODERATOR.getPriority())) {
            if (args.length == 0) {
                if (Origin.getInstance().getVanishPlayers().contains(player)) {
                    Origin.getInstance().getVanishPlayers().remove(player);
                    OriginManager.getInstance().getPlayers().forEach(players -> players.showPlayer(player));
                    player.setAllowFlight(false);
                    player.setFlying(false);
                    player.sendMessage(
                            Origin.getInstance().getPrefix() + "§7Du bist nun §cnicht §7mehr im §eVanish",
                            Origin.getInstance().getPrefix() + "§7You are §cno §7longer §evanished"
                    );
                } else {
                    Origin.getInstance().getVanishPlayers().add(player);
                    OriginManager.getInstance().getPlayers().forEach(players -> players.hidePlayer(player));
                    player.setAllowFlight(true);
                    player.setFlying(true);
                    player.sendMessage(
                            Origin.getInstance().getPrefix() + "§7Du bist nun im §eVanish",
                            Origin.getInstance().getPrefix() + "§7You are now §evanished"
                    );
                }
            } else if (args.length == 1) {
                OriginPlayer target = OriginManager.getInstance().getPlayer(args[0]);
                if (target != null) {
                    if (target != player) {
                        if (Origin.getInstance().getVanishPlayers().contains(target)) {
                            Origin.getInstance().getVanishPlayers().remove(target);
                            OriginManager.getInstance().getPlayers().forEach(players -> players.showPlayer(target));
                            target.setAllowFlight(false);
                            target.setFlying(false);
                            target.sendMessage(
                                    Origin.getInstance().getPrefix() + "§7Du bist nun §cnicht §7mehr im §eVanish",
                                    Origin.getInstance().getPrefix() + "§7You are §cno §7longer §evanished"
                            );
                            player.sendMessage(
                                    Origin.getInstance().getPrefix() + target.getDisplayName() + " §7ist §cnicht §7mehr im §eVanish",
                                    Origin.getInstance().getPrefix() + target.getDisplayName() + " §7is §cno §7longer §evanished"
                            );
                        } else {
                            Origin.getInstance().getVanishPlayers().add(target);
                            OriginManager.getInstance().getPlayers().forEach(players -> players.hidePlayer(target));
                            target.setAllowFlight(true);
                            target.setFlying(true);
                            target.sendMessage(
                                    Origin.getInstance().getPrefix() + "§7Du bist nun im §eVanish",
                                    Origin.getInstance().getPrefix() + "§7You are now §evanished"
                            );
                            player.sendMessage(
                                    Origin.getInstance().getPrefix() + target.getDisplayName() + " §7ist nun im §eVanish",
                                    Origin.getInstance().getPrefix() + target.getDisplayName() + " §7is now §evanished"
                            );
                        }
                    } else {
                        player.sendMessage(
                                Origin.getInstance().getPrefix() + "§7Du darfst §cnicht §7mit dir selbst interagieren!",
                                Origin.getInstance().getPrefix() + "§7You §ccan't §7interact with yourself!"
                        );
                    }
                } else {
                    player.sendMessage(
                            Origin.getInstance().getPrefix() + "§7Dieser Spieler wurde §cnicht §7gefunden!",
                            Origin.getInstance().getPrefix() + "§7This player was §cnot §7found!"
                    );
                }
            } else {
                player.sendMessage(
                        Origin.getInstance().getPrefix() + "§7Bitte nutze: §f/vanish [Name]",
                        Origin.getInstance().getPrefix() + "§7Please use: §f/vanish [Name]"
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
