package de.claved.origin.bungee.listener;

import de.claved.origin.bungee.api.manager.OriginManager;
import de.claved.origin.bungee.Origin;
import de.claved.origin.bungee.api.OriginPlayer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.concurrent.TimeUnit;

public class PlayerDisconnectListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        OriginPlayer player = OriginManager.getInstance().getPlayer(event.getPlayer());
        OriginManager.getInstance().removePlayer(player);
        player.setServer("-");
        ProxyServer.getInstance().getScheduler().schedule(Origin.getInstance(), () -> OriginManager.getInstance().getPlayers().forEach(players -> OriginManager.getInstance().sendPluginChannel("updatePlayerCount")), 1, TimeUnit.SECONDS);
    }
}
