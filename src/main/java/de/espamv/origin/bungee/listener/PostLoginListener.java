package de.claved.origin.bungee.listener;

import de.claved.origin.bungee.api.manager.OriginManager;
import de.claved.origin.bungee.api.manager.PunishmentManager;
import de.claved.origin.utils.objects.origin.OriginPunishment;
import de.claved.origin.bungee.Origin;
import de.claved.origin.bungee.api.OriginPlayer;
import de.claved.origin.bungee.api.manager.PingManager;
import de.claved.origin.utils.enums.Rank;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class PostLoginListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPostLogin(PostLoginEvent event) {
        ProxiedPlayer proxiedPlayer = event.getPlayer();

        if (!OriginManager.getInstance().exists(proxiedPlayer.getUniqueId())) {
            OriginManager.getInstance().insert(proxiedPlayer.getName(), proxiedPlayer.getUniqueId(), proxiedPlayer.getAddress());
        }

        OriginPlayer player = new OriginPlayer(proxiedPlayer.getUniqueId());
        player.setProxiedPlayer(proxiedPlayer);
        OriginManager.getInstance().addPlayer(player);

        if (!Objects.equals(player.getName(), proxiedPlayer.getName())) {
            player.setName(proxiedPlayer.getName());
        }

        if (!Objects.equals(player.getSocketAddress(), proxiedPlayer.getAddress().getAddress().getHostAddress())) {
            player.setSocketAddress(proxiedPlayer.getAddress());
        }

        player.setLastLogin(new Date());

        if (PingManager.getInstance().getOriginServerPing().isMaintenance() && !player.hasPriorityAccess(Rank.BUILDER.getPriority())) {
            player.disconnect(player.language(
                    "§cDas Netzwerk befindet sich derzeit in Wartungsarbeiten!",
                    "§cThe network is currently under maintenance!"
            ));
            return;
        }

        ProxyServer.getInstance().getScheduler().runAsync(Origin.getInstance(), () -> {
            OriginPunishment punishment = player.getActivePunishments().stream().filter(originPunishment -> originPunishment.getType().equals(OriginPunishment.PunishmentType.BAN)).findFirst().orElse(null);
            if (punishment != null) {
                if (punishment.isActive()) {
                    player.disconnect(player.language(
                            "§fDu wurdest von §6Claved §fgebannt!\n\n§fGrund§8: §c" + punishment.getReason() + "\n§fDauer§8: §e" + punishment.getUntilAsDate() + "\n\n§fWeitere Informationen können unter §chttps://Claved.de §fangefordert werden.",
                            "§fYou have been banned by §6Claved§f!\n\n§fReason§8: §c" + punishment.getReason() + "\n§fDuration§8: §e" + punishment.getUntilAsDate() + "\n\n§fFurther information you can receive at §chttps://Claved.de§f."
                    ));
                    return;
                }
            }

            if (PunishmentManager.getInstance().getPunishments().stream().filter(originPunishment -> originPunishment.isActive() && originPunishment.getType().equals(OriginPunishment.PunishmentType.BAN)).anyMatch(originPunishment -> Objects.equals(originPunishment.getAddress(), player.getSocketAddress()))) {
                player.disconnect(player.language(
                        "§fDie IP-Adresse mit welcher du dich verbunden hast, ist derzeit von §6Claved §fgebannt!§f!\n§fWeitere Informationen können unter §chttps://Claved.de §fangefordert werden.",
                        "§fThe used IP-Address is currently banned by §bVenedus§f!\n§fFurther information you can receive at §chttps://Claved.de§f."
                ));
                return;
            }
        });

        ProxyServer.getInstance().getScheduler().schedule(Origin.getInstance(), () -> OriginManager.getInstance().getPlayers().forEach(players -> OriginManager.getInstance().sendPluginChannel("updatePlayerCount")), 1, TimeUnit.SECONDS);
    }
}
