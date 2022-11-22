package de.claved.origin.spigot.listener;

import de.claved.origin.spigot.Origin;
import de.claved.origin.spigot.api.OriginPlayer;
import de.claved.origin.spigot.api.manager.OriginManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        OriginPlayer player = OriginManager.getInstance().getPlayer(event.getPlayer());

        event.setQuitMessage(null);

        Origin.getInstance().getFlyingPlayers().remove(player);
        Origin.getInstance().getVanishPlayers().remove(player);
    }
}
