package de.claved.origin.bungee;

import de.claved.origin.bungee.api.manager.*;
import de.claved.origin.utils.session.Session;
import de.claved.origin.bungee.utils.registration.EventRegistration;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

public class Origin extends Plugin {

    @Getter
    private static Origin instance;

    @Getter
    private final String prefix = "§8[ §6Claved §8] §r";

    @Getter
    private Session session;

    private EventRegistration eventRegistration;

    @Override
    public void onLoad() {
        instance = this;

        eventRegistration = new EventRegistration();
    }

    @Override
    public void onEnable() {
        session = new Session("localhost", 3306, "venedus", "root", "uuJuH8Rq=#sxQ9)}Sj6E>E5{?Dr4y6az", true);
        session.connect();

        eventRegistration.registerAllEvents();

        new ClanManager();
        new CoinsManager();
        new CountryManager();
        new FriendManager();
        new LanguageManager();
        new LoginManager();
        new NotifyManager();
        new OriginManager();
        new OnlineTimeManager();
        new PartyManager();
        new PermissionManager();
        new PingManager();
        new PointsManager();
        new PunishmentManager();
        new ReportManager();
        new TablistManager();

        ProxyServer.getInstance().registerChannel("origin");
    }

    @Override
    public void onDisable() {
        session.disable();

        de.claved.origin.spigot.api.manager.ClanManager.getInstance().disable();
        de.pxscxl.origin.bungee.api.manager.CoinsManager.getInstance().disable();
        de.pxscxl.origin.bungee.api.manager.CountryManager.getInstance().disable();
        de.pxscxl.origin.bungee.api.manager.FriendManager.getInstance().disable();
        de.pxscxl.origin.bungee.api.manager.LanguageManager.getInstance().disable();
        de.pxscxl.origin.bungee.api.manager.LoginManager.getInstance().disable();
        de.pxscxl.origin.bungee.api.manager.NotifyManager.getInstance().disable();
        de.pxscxl.origin.bungee.api.manager.OriginManager.getInstance().disable();
        de.pxscxl.origin.bungee.api.manager.OnlineTimeManager.getInstance().disable();
        de.pxscxl.origin.bungee.api.manager.PartyManager.getInstance().disable();
        de.pxscxl.origin.bungee.api.manager.PermissionManager.getInstance().disable();
        de.pxscxl.origin.bungee.api.manager.PingManager.getInstance().disable();
        de.pxscxl.origin.bungee.api.manager.PointsManager.getInstance().disable();
        de.pxscxl.origin.bungee.api.manager.PunishmentManager.getInstance().disable();
        de.pxscxl.origin.bungee.api.manager.ReportManager.getInstance().disable();
        de.pxscxl.origin.bungee.api.manager.TablistManager.getInstance().disable();
    }
}
