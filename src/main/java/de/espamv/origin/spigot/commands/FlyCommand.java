package de.claved.origin.spigot.commands;

import de.claved.origin.spigot.Origin;
import de.claved.origin.spigot.api.OriginPlayer;
import de.claved.origin.spigot.api.manager.OriginManager;
import de.claved.origin.utils.enums.Rank;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FlyCommand extends Command {

    public FlyCommand() {
        super("fly");
    }

    @Override
    public boolean execute(CommandSender commandSender, String arg, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("This command is only executable as a player!");
            return true;
        }
        OriginPlayer player = OriginManager.getInstance().getPlayer((Player) commandSender);
        if (player.hasPriorityAccess(Rank.SRMODERATOR.getPriority())) {
            if (!Origin.getInstance().getVanishPlayers().contains(player)) {
                if (args.length == 0) {
                    if (Origin.getInstance().getFlyingPlayers().contains(player)) {
                        Origin.getInstance().getFlyingPlayers().remove(player);
                        player.setAllowFlight(false);
                        player.setFlying(false);
                        player.sendMessage(
                                Origin.getInstance().getPrefix() + "§7Du kannst nun §cnicht §7mehr §efliegen",
                                Origin.getInstance().getPrefix() + "§7You can §cno §7longer §efly"
                        );
                    } else {
                        Origin.getInstance().getFlyingPlayers().add(player);
                        player.setAllowFlight(true);
                        player.setFlying(true);
                        player.sendMessage(
                                Origin.getInstance().getPrefix() + "§7Du kannst nun §efliegen",
                                Origin.getInstance().getPrefix() + "§7You can now §efly"
                        );
                    }
                } else if (args.length == 1) {
                    OriginPlayer target = OriginManager.getInstance().getPlayer(args[0]);
                    if (target != null) {
                        if (target != player) {
                            if (Origin.getInstance().getFlyingPlayers().contains(target)) {
                                Origin.getInstance().getFlyingPlayers().remove(target);
                                target.setAllowFlight(false);
                                target.setFlying(false);
                                target.sendMessage(
                                        Origin.getInstance().getPrefix() + "§7Du kannst nun §cnicht §7mehr §efliegen",
                                        Origin.getInstance().getPrefix() + "§7You can §cno §7longer §efly"
                                );
                                player.sendMessage(
                                        Origin.getInstance().getPrefix() + target.getDisplayName() + " §7kann nun §cnicht §7mehr §efliegen",
                                        Origin.getInstance().getPrefix() + target.getDisplayName() + " §7can §cno §7longer §efly"
                                );
                            } else {
                                Origin.getInstance().getFlyingPlayers().add(target);
                                target.setAllowFlight(true);
                                target.setFlying(true);
                                target.sendMessage(
                                        Origin.getInstance().getPrefix() + "§7Du kannst nun §efliegen",
                                        Origin.getInstance().getPrefix() + "§7You can now §efly"
                                );
                                player.sendMessage(
                                        Origin.getInstance().getPrefix() + target.getDisplayName() + " §7kann nun §efliegen",
                                        Origin.getInstance().getPrefix() + target.getDisplayName() + " §7can now §efly"
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
                            Origin.getInstance().getPrefix() + "§7Bitte nutze: §f/fly [Name]",
                            Origin.getInstance().getPrefix() + "§7Please use: §f/fly [Name]"
                    );
                }
            } else {
                player.sendMessage(
                        Origin.getInstance().getPrefix() + "§7Du bist derzeit im Vanish, daher kannst du diesen Befehl §cnicht §7nutzen!",
                        Origin.getInstance().getPrefix() + "§7You are currently in vanish, so you §ccan't §7use this command!"
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
