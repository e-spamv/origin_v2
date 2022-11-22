package de.claved.origin.spigot.api.manager;

import de.claved.origin.spigot.Origin;
import de.claved.origin.spigot.api.OriginPlayer;
import de.claved.origin.spigot.api.hologram.Hologram;
import de.claved.origin.spigot.api.events.LanguageUpdateEvent;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
public class HologramManager implements Listener {

    @Getter
    private static HologramManager instance;

    private final HashMap<UUID, Set<Hologram>> holograms = new HashMap<>();
    private final HashMap<UUID, Set<Hologram>> outsideDistance = new HashMap<>();

    public HologramManager() {
        instance = this;
        Bukkit.getPluginManager().registerEvents(this, Origin.getInstance());
    }

    public void disable() {
        instance = null;
    }

    @EventHandler
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        HologramManager.getInstance().getHolograms().put(event.getUniqueId(), new HashSet<>());
        HologramManager.getInstance().getOutsideDistance().put(event.getUniqueId(), new HashSet<>());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        OriginPlayer player = OriginManager.getInstance().getPlayer(event.getPlayer());
        holograms.remove(player.getUniqueId()).forEach(Hologram::hide);
        outsideDistance.remove(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        OriginPlayer player = OriginManager.getInstance().getPlayer(event.getPlayer());
        handleMove(player, event.getTo());
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        OriginPlayer player = OriginManager.getInstance().getPlayer(event.getPlayer());
        handleMove(player, event.getTo());
    }

    @EventHandler
    public void onLanguageUpdate(LanguageUpdateEvent event) {
        holograms.get(event.getPlayer().getUniqueId()).forEach(Hologram::updateText);
    }

    private void handleMove(OriginPlayer player, Location location) {
        Set<Hologram> holograms = this.holograms.get(player.getUniqueId());
        if (holograms == null) return;
        holograms.forEach(hologram -> {
            double distance = hologram.getLocation().distance(location);
            if (distance > 100 && !outsideDistance.get(player.getUniqueId()).contains(hologram)) {
                outsideDistance.get(player.getUniqueId()).add(hologram);
            } else if (distance < 50 && outsideDistance.get(player.getUniqueId()).contains(hologram)) {
                outsideDistance.get(player.getUniqueId()).remove(hologram);
                hologram.hide();
                Bukkit.getScheduler().runTaskLaterAsynchronously(Origin.getInstance(), hologram::show, 2);
            }
        });
    }

    public void registerHologram(OriginPlayer player, Hologram hologram) {
        if (holograms.get(player.getUniqueId()) == null) return;
        holograms.get(player.getUniqueId()).add(hologram);
    }

    public void unregisterHologram(OriginPlayer player, Hologram hologram) {
        if (holograms.get(player.getUniqueId()) == null) return;
        holograms.get(player.getUniqueId()).remove(hologram);
    }

    public Hologram createHologram(OriginPlayer player, Location location, Hologram.HologramText hologramText) {
        return new Hologram(player, location, hologramText);
    }
}
