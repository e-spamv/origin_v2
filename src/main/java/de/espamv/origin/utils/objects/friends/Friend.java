package de.claved.origin.utils.objects.friends;

import lombok.Getter;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Getter
public class Friend {

    private final UUID uuid;

    private final Timestamp since;

    private final FriendSettings settings;

    public Friend(UUID uuid, Timestamp since, FriendSettings settings) {
        this.uuid = uuid;
        this.since = since;
        this.settings = settings;
    }

    public String getSinceAsDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return simpleDateFormat.format(new Date(since.getTime()));
    }
}
