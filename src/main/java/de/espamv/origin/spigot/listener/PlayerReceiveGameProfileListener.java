package de.claved.origin.spigot.listener;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.mojang.authlib.properties.Property;
import de.claved.cloud.CloudAPI;
import de.claved.origin.spigot.api.OriginPlayer;
import de.claved.origin.spigot.api.manager.NickManager;
import de.claved.origin.spigot.api.manager.OriginManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.kitteh.tag.PlayerReceiveGameProfileEvent;

public class PlayerReceiveGameProfileListener implements Listener {

    @EventHandler
    public void onPlayerReceiveGameProfile(PlayerReceiveGameProfileEvent event) {
        if (!CloudAPI.getInstance().getLocalServer().getName().toLowerCase().contains("lobby")) {
            OriginPlayer player = OriginManager.getInstance().getPlayer(event.getNamedPlayer());
            if (NickManager.getInstance().getNickName(player.getUniqueId()) != null) {
                String name = NickManager.getInstance().getNickName(player.getUniqueId());
                if (name.length() > 16) {
                    name = name.substring(0, 16);
                }
                event.setName(name);
                if (NickManager.getInstance().getNickGameProfile(NickManager.getInstance().getNickName(player.getUniqueId())) != null) {
                    event.setGameProfile(WrappedGameProfile.fromHandle(NickManager.getInstance().getNickGameProfile(NickManager.getInstance().getNickName(player.getUniqueId()))));
                    Property property = NickManager.getInstance().getNickGameProfile(NickManager.getInstance().getNickName(player.getUniqueId())).getProperties().get("textures").iterator().next();
                    String texture = property.getValue();
                    String signature = property.getSignature();
                    event.setTexture(texture, signature);
                }
            }
        }
    }
}

