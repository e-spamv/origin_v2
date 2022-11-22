package de.claved.origin.spigot.listener;

import de.claved.origin.spigot.Origin;
import de.claved.origin.spigot.api.OriginPlayer;
import de.claved.origin.spigot.api.manager.OriginManager;
import de.claved.origin.utils.enums.Rank;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        OriginPlayer player = OriginManager.getInstance().getPlayer(event.getPlayer());

        event.setJoinMessage(null);

        OriginManager.getInstance().getPlayers().forEach(players -> {
            if (Origin.getInstance().getVanishPlayers().contains(players)) {
                if (!player.hasPriorityAccess(Rank.SRMODERATOR.getPriority())) {
                    player.hidePlayer(players);
                }
            }
        });
    }
}
