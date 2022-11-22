package de.claved.origin.spigot.utils.registration;

import de.claved.origin.spigot.commands.*;
import de.claved.origin.utils.Reflections;
import org.bukkit.Bukkit;
import org.bukkit.command.SimpleCommandMap;

public class CommandRegistration {

    private final SimpleCommandMap simpleCommandMap = Reflections.getField(Bukkit.getServer(), "commandMap");

    public void registerAllCommands() {
        simpleCommandMap.register("origin", new FlyCommand());
        simpleCommandMap.register("origin", new LoadWorldCommand());
        simpleCommandMap.register("origin", new NickCommand());
        simpleCommandMap.register("origin", new NickListCommand());
        simpleCommandMap.register("origin", new ReportsCommand());
        simpleCommandMap.register("origin", new TeleportWorldCommand());
        simpleCommandMap.register("origin", new VanishCommand());
    }
}
