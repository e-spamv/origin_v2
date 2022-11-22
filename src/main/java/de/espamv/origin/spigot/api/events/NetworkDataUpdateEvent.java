package de.claved.origin.spigot.api.events;

import com.google.gson.JsonObject;
import de.claved.origin.spigot.api.OriginPlayer;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class NetworkDataUpdateEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();

    private final OriginPlayer player;
    private final String identifier;
    private final JsonObject object;

    public NetworkDataUpdateEvent(OriginPlayer player, String identifier, JsonObject object) {
        this.player = player;
        this.identifier = identifier;
        this.object = object;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
