package de.claved.origin.spigot.listener;

import de.claved.origin.spigot.api.OriginPlayer;
import de.claved.origin.spigot.api.manager.NickManager;
import de.claved.origin.spigot.api.manager.OriginManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import net.md_5.itag.iTag;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerTeleportListener implements Listener {


    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        OriginPlayer player = OriginManager.getInstance().getPlayer(event.getPlayer());
        if (NickManager.getInstance().getNickName(player.getUniqueId()) != null && event.getTo().getBlockX() != event.getFrom().getBlockX() && event.getTo().getBlockY() != event.getFrom().getBlockY() && event.getTo().getBlockZ() != event.getFrom().getBlockZ()) {
            iTag.getInstance().refreshPlayer(player.getBukkitPlayer());
        }
    }
}
