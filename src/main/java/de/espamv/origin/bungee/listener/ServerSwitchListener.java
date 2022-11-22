package de.claved.origin.bungee.listener;

import de.claved.origin.bungee.api.manager.OriginManager;
import de.claved.origin.bungee.api.manager.TablistManager;
import de.claved.origin.bungee.api.OriginPlayer;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ServerSwitchListener implements Listener {

    @EventHandler
    public void onServerSwitch(ServerSwitchEvent event) {
        OriginPlayer player = OriginManager.getInstance().getPlayer(event.getPlayer());
        player.setServer(event.getPlayer().getServer().getInfo().getName());
        player.setTabHeader(TablistManager.getInstance().getHeader(player), TablistManager.getInstance().getFooter(player));
    }
}
