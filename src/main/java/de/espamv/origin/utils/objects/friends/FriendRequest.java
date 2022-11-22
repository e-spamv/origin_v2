package de.claved.origin.utils.objects.friends;

import de.claved.origin.bungee.api.manager.OriginManager;
import lombok.Getter;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Getter
public class FriendRequest {

    private final UUID uuid;

    private final Timestamp creation;

    public FriendRequest(UUID uuid, Timestamp creation) {
        this.uuid = uuid;
        this.creation = creation;
    }

    public String getCreationAsDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return simpleDateFormat.format(new Date(creation.getTime()));
    }

    public boolean isOnline() {
        return OriginManager.getInstance().getPlayer(uuid) != null;
    }
}
