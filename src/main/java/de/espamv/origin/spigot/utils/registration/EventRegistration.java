package de.claved.origin.spigot.utils.registration;

import de.claved.origin.spigot.Origin;
import de.claved.origin.spigot.listener.*;
import org.bukkit.Bukkit;

public class EventRegistration {

    public void registerAllEvents() {
        Bukkit.getPluginManager().registerEvents(new AsyncPlayerPreLoginListener(), Origin.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerChangedWorldListener(), Origin.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerCommandPreprocessListener(), Origin.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerDeathListener(), Origin.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), Origin.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerLoginListener(), Origin.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(), Origin.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerReceiveGameProfileListener(), Origin.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerTeleportListener(), Origin.getInstance());
    }
}
