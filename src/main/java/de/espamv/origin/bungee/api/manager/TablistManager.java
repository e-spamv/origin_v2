package de.claved.origin.bungee.api.manager;

import de.claved.origin.bungee.api.OriginPlayer;
import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class TablistManager {

    @Getter
    private static TablistManager instance;

    public TablistManager() {
        instance = this;
    }

    public void disable() {
        instance = null;
    }

    public BaseComponent[] getHeader(OriginPlayer player) {
        if (player == null) return TextComponent.fromLegacyText("");
        return TextComponent.fromLegacyText("\n§6Claved §7§o" + player.language("Netzwerk", "Network") + "\n");
    }

    public BaseComponent[] getFooter(OriginPlayer player) {
        if (player == null) return TextComponent.fromLegacyText("");
        String server = player.getServer() == null ? "Unknown" : player.getServer().getName();
        return TextComponent.fromLegacyText("\n        " + player.language("§7Du befindest dich auf §6", "§7You are currently on §6") + server + "        \n§7TeamSpeak §8× §6Claved.de\n");
    }
}
