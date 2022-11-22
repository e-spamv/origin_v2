package de.claved.origin.spigot.listener;

import com.google.gson.JsonObject;
import de.claved.origin.spigot.Origin;
import de.claved.origin.spigot.api.manager.OriginManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class AsyncPlayerPreLoginListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        if (!Origin.getInstance().isRunning()) {
            event.setKickMessage("Server is still starting...");
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            return;
        }
        JsonObject playerObject = OriginManager.getInstance().loadOriginPlayer(event.getName(), event.getUniqueId());
        if (playerObject == null) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, NullPointerException.class.getName());
        } else {
            OriginManager.getInstance().cache(event.getUniqueId(), playerObject);
        }
    }
}
