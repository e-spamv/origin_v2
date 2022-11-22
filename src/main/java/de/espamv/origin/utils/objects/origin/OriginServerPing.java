package de.claved.origin.utils.objects.origin;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OriginServerPing {

    private String headerMotd;
    private String footerMotd;

    private int slots;
    private int fakePlayersCount;

    private boolean maintenance;
    private boolean fakePlayers;

    public OriginServerPing(String headerMotd, String footerMotd, int slots, int fakePlayersCount, boolean maintenance, boolean fakePlayers) {
        this.headerMotd = headerMotd;
        this.footerMotd = footerMotd;
        this.slots = slots;
        this.fakePlayersCount = fakePlayersCount;
        this.maintenance = maintenance;
        this.fakePlayers = fakePlayers;
    }
}
