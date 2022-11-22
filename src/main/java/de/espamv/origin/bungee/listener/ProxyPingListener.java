package de.claved.origin.bungee.listener;

import de.claved.origin.bungee.api.manager.LanguageManager;
import de.claved.origin.bungee.api.manager.PingManager;
import de.claved.origin.utils.enums.Language;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ProxyPingListener implements Listener {

    @EventHandler
    public void onProxyPing(ProxyPingEvent event) {
        ServerPing response = event.getResponse();
        ServerPing.Players players = response.getPlayers();

        try {
            response.setDescriptionComponent(new TextComponent(PingManager.getInstance().getOriginServerPing().getHeaderMotd() + "\n" + PingManager.getInstance().getOriginServerPing().getFooterMotd()));
            if (PingManager.getInstance().getOriginServerPing().isMaintenance()) {
                Language language = LanguageManager.getInstance().fromIp(event.getConnection().getAddress().getAddress().getHostAddress());
                response.setVersion(new ServerPing.Protocol(language.language("§cWartungsarbeiten", "§cMaintenance"), Short.MAX_VALUE));
            } else {
                players.setOnline(response.getPlayers().getOnline() + (PingManager.getInstance().getOriginServerPing().isFakePlayers() ? PingManager.getInstance().getOriginServerPing().getFakePlayersCount() : 0));
                players.setMax(PingManager.getInstance().getOriginServerPing().getSlots());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        event.setResponse(response);
    }
}
