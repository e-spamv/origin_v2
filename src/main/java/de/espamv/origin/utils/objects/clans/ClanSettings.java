package de.claved.origin.utils.objects.clans;

import de.claved.origin.utils.enums.ClanRank;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class ClanSettings {

    private final UUID uuid;

    private String clanName;
    private ClanRank clanRank;

    private boolean isReceivingMessage;
    private boolean isReceivingRequests;
    private boolean isDisplayedAsOnline;

    public ClanSettings(UUID uuid, String clanName, ClanRank clanRank, boolean isReceivingMessage, boolean isReceivingRequests, boolean isDisplayedAsOnline) {
        this.uuid = uuid;
        this.clanName = clanName;
        this.clanRank = clanRank;
        this.isReceivingMessage = isReceivingMessage;
        this.isReceivingRequests = isReceivingRequests;
        this.isDisplayedAsOnline = isDisplayedAsOnline;
    }
}