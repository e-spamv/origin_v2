package de.claved.origin.spigot.listener;

import de.claved.cloud.CloudAPI;
import de.claved.origin.spigot.Origin;
import de.claved.origin.spigot.api.OriginPlayer;
import de.claved.origin.spigot.api.manager.OriginManager;
import de.claved.origin.utils.enums.Rank;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class PlayerLoginListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        OriginPlayer player;
        try {
            player = new OriginPlayer(OriginManager.getInstance().getCache(event.getPlayer().getUniqueId()));
        } catch (NullPointerException e) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Error while loading your data!");
            return;
        }
        player.setBukkitPlayer(event.getPlayer());
        OriginManager.getInstance().addPlayer(player);
        try {
            player.editPermissibleBase();
        } catch (Exception e1) {
            e1.printStackTrace();
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "An internal error occurred during login, try again later");
        }

        switch (CloudAPI.getInstance().getLocalServer().getState()) {
            case LOBBY:
                if (!CloudAPI.getInstance().getLocalServer().getName().contains("lobby")) {
                    if (Bukkit.getOnlinePlayers().size() < CloudAPI.getInstance().getLocalServer().getServerInfo().getMaxPlayers()) {
                        event.allow();
                        return;
                    }
                    if (!player.hasPriorityAccess(Rank.PREMIUM.getPriority())) {
                        event.disallow(
                                PlayerLoginEvent.Result.KICK_FULL, player.language(
                                        Origin.getInstance().getPrefix() + "§7You've got kicked for a higher rank. For more information visit: §ehttps://claved.de",
                                        Origin.getInstance().getPrefix() + "§7Du wurdest für einen höherrangigen Spieler gekickt. Weitere Informationen: §ehttps://claved.de"
                                )
                        );
                        return;
                    }
                    for (OriginPlayer players : OriginManager.getInstance().getPlayers()) {
                        if (player == players) continue;
                        if (!player.hasPriorityAccess(Rank.PREMIUM.getPriority())) {
                            players.kick(
                                    player.language(
                                            Origin.getInstance().getPrefix() + "§7The server is §cfull§7. You need a higher rank to join nevertheless. For more information visit: §ehttps://claved.de",
                                            Origin.getInstance().getPrefix() + "§7Der Server ist §cvoll§7. Du benötigst einen höheren Rang, um ihn dennoch betreten zu können. Weitere Informationen: §ehttps://claved.de"
                                    )
                            );
                            event.allow();
                            return;
                        }
                    }
                    event.disallow(
                            PlayerLoginEvent.Result.KICK_FULL, player.language(
                                    Origin.getInstance().getPrefix() + "§7The server is §cfull§7. Nobody is able to join this server",
                                    Origin.getInstance().getPrefix() + "§7Der Server ist §cvoll§7. Niemand kann diesen Server mehr betreten"
                            )
                    );
                }
                break;
            case INGAME:
                event.allow();
                break;
            default:
                break;
        }
    }
}
