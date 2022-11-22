package de.claved.origin.utils.objects.friends;

import lombok.Getter;
import lombok.Setter;

@Getter
public class FriendSettings {

    @Setter
    private boolean isReceivingMessage;
    @Setter
    private boolean isReceivingRequests;
    @Setter
    private boolean isDisplayedAsOnline;

    public FriendSettings(boolean isReceivingMessage, boolean isReceivingRequests, boolean isDisplayedAsOnline) {
        this.isReceivingMessage = isReceivingMessage;
        this.isReceivingRequests = isReceivingRequests;
        this.isDisplayedAsOnline = isDisplayedAsOnline;
    }
}