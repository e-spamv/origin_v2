package de.claved.origin.bungee.utils.registration;

import de.claved.origin.bungee.Origin;
import de.claved.origin.bungee.listener.*;
import net.md_5.bungee.api.ProxyServer;

public class EventRegistration {

    public void registerAllEvents() {
        ProxyServer.getInstance().getPluginManager().registerListener(Origin.getInstance(), new ChatListener());
        ProxyServer.getInstance().getPluginManager().registerListener(Origin.getInstance(), new PlayerDisconnectListener());
        ProxyServer.getInstance().getPluginManager().registerListener(Origin.getInstance(), new PluginMessageListener());
        ProxyServer.getInstance().getPluginManager().registerListener(Origin.getInstance(), new PostLoginListener());
        ProxyServer.getInstance().getPluginManager().registerListener(Origin.getInstance(), new ProxyPingListener());
        ProxyServer.getInstance().getPluginManager().registerListener(Origin.getInstance(), new ServerConnectListener());
        ProxyServer.getInstance().getPluginManager().registerListener(Origin.getInstance(), new ServerSwitchListener());
    }
}
