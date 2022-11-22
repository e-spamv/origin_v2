package de.claved.origin.utils.objects.clans;

import de.claved.origin.bungee.api.manager.OriginManager;
import lombok.Getter;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Getter
public class ClanRequest {

    private final UUID uuid;

    private final Clan clan;

    private final Timestamp since;

    public ClanRequest(UUID uuid, Clan clan, Timestamp since) {
        this.uuid = uuid;
        this.clan = clan;
        this.since = since;
    }

    public String getSinceAsDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return simpleDateFormat.format(new Date(since.getTime()));
    }

    public boolean isOnline() {
        return OriginManager.getInstance().getPlayer(uuid) != null;
    }
}
