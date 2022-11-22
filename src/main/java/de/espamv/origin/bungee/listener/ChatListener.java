package de.claved.origin.bungee.listener;

import de.claved.origin.bungee.api.manager.OriginManager;
import de.claved.origin.bungee.api.manager.PunishmentManager;
import de.claved.origin.utils.objects.origin.OriginPunishment;
import de.claved.origin.bungee.Origin;
import de.claved.origin.bungee.api.OriginPlayer;
import de.claved.origin.utils.enums.Rank;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Arrays;
import java.util.Objects;

public class ChatListener implements Listener {

    private final String[] blockedCommands;
    private final String[] blockedWords;

    public ChatListener() {
        blockedCommands = new String[]{"plugins", "pl", "bukkit:plugins", "bukkit:pl", "bukkit:?", "bukkit:ver", "bukkit:help", "about", "minecraft:me", "help", "version", "ver", "bungee", "icanhasbukkit"};
        blockedWords = new String[]{"hurensohn", "hrnshn", "huso", "huan", "bastard", "bstrd", "spast", "spasti", "schwuchtel", "nigga", "nigger", "n1gger", "nigg3r", "n1gg3r", "nega", "n1gga", "nigg4", "n1gg4", "fotze", "fxtze", "fotzx", "fxtzx", "f0tze", "ficken", "f1cken", "f1ckxn", "fckn", "fickxn", "fxcken"};
    }

    @EventHandler
    public void onChat(ChatEvent event) {
        OriginPlayer player = OriginManager.getInstance().getPlayer((ProxiedPlayer) event.getSender());

        if (!event.getMessage().startsWith("/")) {
            OriginPunishment punishment = player.getActivePunishments().stream().filter(originPunishment -> originPunishment.getType().equals(OriginPunishment.PunishmentType.MUTE)).findFirst().orElse(null);
            if (punishment != null) {
                if (punishment.isActive()) {
                    player.sendMessage(
                            Origin.getInstance().getPrefix() + "§7Du bist derzeit aus dem §eChat §7gebannt",
                            Origin.getInstance().getPrefix() + "§7You are currently banned from the §echat"
                    );
                    player.sendMessage(
                            Origin.getInstance().getPrefix() + "§7Grund§8: §c" + punishment.getReason(),
                            Origin.getInstance().getPrefix() + "§7Reason§8: §c" + punishment.getReason()
                    );
                    player.sendMessage(
                            Origin.getInstance().getPrefix() + "§7Dauer§8: §e" + punishment.getUntilAsDate(),
                            Origin.getInstance().getPrefix() + "§7Duration§8: §e" + punishment.getUntilAsDate()
                    );
                    event.setMessage(null);
                    event.setCancelled(true);
                    return;
                }
            }

            if (PunishmentManager.getInstance().getPunishments().stream().filter(originPunishment -> originPunishment.isActive() && originPunishment.getType().equals(OriginPunishment.PunishmentType.MUTE)).anyMatch(originPunishment -> Objects.equals(originPunishment.getAddress(), player.getSocketAddress()))) {
                player.sendMessage(
                        Origin.getInstance().getPrefix() + "§7Du bist auf einem anderen Account aus dem §fChat §7gebannt",
                        Origin.getInstance().getPrefix() + "§7You are banned from the §fchat §7on another account"
                );
                event.setMessage(null);
                event.setCancelled(true);
                return;
            }
        }

        if (Arrays.stream(blockedCommands).anyMatch(command -> ("/" + command).equals(event.getMessage().toLowerCase())) && !player.hasPriorityAccess(Rank.SRMODERATOR.getPriority())) {
            player.sendMessage(
                    Origin.getInstance().getPrefix() + "§7Du darfst diesen Befehl §cnicht §7ausführen",
                    Origin.getInstance().getPrefix() + "§7You §caren't §7allowed to execute this command"
            );
            event.setCancelled(true);
        }

        if (Arrays.stream(blockedWords).anyMatch(word -> event.getMessage().contains(word)) && !player.hasPriorityAccess(Rank.SRMODERATOR.getPriority())) {
            player.sendMessage(
                    Origin.getInstance().getPrefix() + "§7Bitte achte auf deine Wortwahl",
                    Origin.getInstance().getPrefix() + "§7Please pay attention to your choice of words"
            );
            event.setCancelled(true);
        }
    }
}
