package de.claved.origin.spigot.listener;

import de.claved.origin.spigot.Origin;
import de.claved.origin.spigot.api.OriginPlayer;
import de.claved.origin.spigot.api.manager.OriginManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.help.HelpTopic;

public class PlayerCommandPreprocessListener implements Listener {

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        OriginPlayer player = OriginManager.getInstance().getPlayer(event.getPlayer());
        HelpTopic helpTopic = Bukkit.getHelpMap().getHelpTopic(event.getMessage().split(" ")[0]);

        if (!event.isCancelled() && helpTopic == null) {
            event.setCancelled(true);
            player.sendMessage(
                    Origin.getInstance().getPrefix() + "§7Dieser Befehl §cexistiert §7nicht",
                    Origin.getInstance().getPrefix() + "§7This command could not be §cfound");
        }
    }
}
