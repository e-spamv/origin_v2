package de.claved.origin.spigot.commands;

import de.claved.origin.spigot.Origin;
import de.claved.origin.spigot.api.OriginPlayer;
import de.claved.origin.spigot.api.manager.NickManager;
import de.claved.origin.utils.enums.Rank;
import de.claved.origin.spigot.api.manager.OriginManager;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;

public class LoadWorldCommand extends Command {

    public LoadWorldCommand() {
        super("loadworld");
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
                if (Bukkit.getWorld(args[0]) != null) {
                    player.sendMessage(
                            Origin.getInstance().getPrefix() + "§7Die Welt ist §ebereits §7geladen",
                            Origin.getInstance().getPrefix() + "§7This world is §ealready §7loaded"
                    );
                    return true;
                }
                if (!new File(args[0]).exists()) {
                    player.sendMessage(
                            Origin.getInstance().getPrefix() + "§7Es ist keine Welt mit dem Namen §c" + args[0] + " §7verfügbar",
                            Origin.getInstance().getPrefix() + "§7No world is §cavailable §7with the name§8: §c" + args[0]
                    );
                    return true;
                }
                loadWorld(args[0]);
                player.sendMessage(
                        Origin.getInstance().getPrefix() + "§7Welt §aerfolgreich §7geladen",
                        Origin.getInstance().getPrefix() + "§7World §asuccessfully §7loaded"
                );
            } else {
                player.sendMessage(
                        Origin.getInstance().getPrefix() + "§7Bitte nutze: §f/loadworld [Name]",
                        Origin.getInstance().getPrefix() + "§7Please use: §f/loadworld [Name]"
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

    private void loadWorld(String name) {
        if (!new File(name).exists()) {
            return;
        }
        World world = Bukkit.getWorld(name);
        if (world != null) {
            return;
        }
        WorldCreator creator = new WorldCreator(name);
        creator.environment(World.Environment.NORMAL);
        creator.generateStructures(false);
        world = creator.createWorld();
        world.setDifficulty(Difficulty.EASY);
        world.setSpawnFlags(false, false);
        world.setPVP(true);
        world.setStorm(false);
        world.setThundering(false);
        world.setKeepSpawnInMemory(false);
        world.setTicksPerAnimalSpawns(0);
        world.setTicksPerMonsterSpawns(0);
        world.setWeatherDuration(0);

        world.setAutoSave(false);
        world.setGameRuleValue("doFireTick", "false");
        world.setGameRuleValue("doMobSpawning", "false");
        world.setTime(6000L);
    }
}
