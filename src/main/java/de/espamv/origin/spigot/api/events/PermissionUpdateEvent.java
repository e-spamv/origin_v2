package de.claved.origin.spigot.api.events;

import de.claved.origin.spigot.api.OriginPlayer;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class PermissionUpdateEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();

    private final OriginPlayer player;

    public PermissionUpdateEvent(OriginPlayer player) {
        this.player = player;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
