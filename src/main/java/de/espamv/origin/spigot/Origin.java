package de.claved.origin.spigot;

import de.claved.origin.spigot.api.OriginMessages;
import de.claved.origin.spigot.api.OriginPlayer;
import de.claved.origin.spigot.api.manager.*;
import de.claved.origin.utils.session.Session;
import de.claved.origin.spigot.utils.registration.CommandRegistration;
import de.claved.origin.spigot.utils.registration.EventRegistration;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public class Origin extends JavaPlugin {

    @Getter
    private static Origin instance;

    @Getter
    private final String prefix = "§8┃ §6§lClaved §8× §r";

    @Getter
    private Session session;

    @Setter
    @Getter
    private boolean running = false;

    private CommandRegistration commandRegistration;
    private EventRegistration eventRegistration;

    @Getter
    private final ArrayList<OriginPlayer> flyingPlayers = new ArrayList<>();
    @Getter
    private final ArrayList<OriginPlayer> vanishPlayers = new ArrayList<>();

    @Override
    public void onLoad() {
        instance = this;

        commandRegistration = new CommandRegistration();
        eventRegistration = new EventRegistration();
    }

    @Override
    public void onEnable() {
        session = new Session("localhost", 3306, "venedus", "root", "uuJuH8Rq=#sxQ9)}Sj6E>E5{?Dr4y6az", true);
        session.connect();

        commandRegistration.registerAllCommands();
        eventRegistration.registerAllEvents();

        new ClanManager();
        new CoinsManager();
        new FriendManager();
        new LanguageManager();
        new LoginManager();
        new NickManager();
        new HologramManager();
        new NotifyManager();
        new OriginManager();
        new OnlineTimeManager();
        new PermissionManager();
        new PointsManager();
        new ReportManager();

        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "origin");
        Bukkit.getMessenger().registerIncomingPluginChannel(this, "origin", new OriginMessages.PluginMessageReceivedListener());

        running = true;
    }

    @Override
    public void onDisable() {
        session.disable();

        de.pxscxl.origin.bungee.api.manager.ClanManager.getInstance().disable();
        de.pxscxl.origin.bungee.api.manager.CoinsManager.getInstance().disable();
        de.pxscxl.origin.bungee.api.manager.FriendManager.getInstance().disable();
        de.pxscxl.origin.bungee.api.manager.LanguageManager.getInstance().disable();
        de.pxscxl.origin.bungee.api.manager.LoginManager.getInstance().disable();
        de.pxscxl.origin.spigot.api.manager.NickManager.getInstance().disable();
        de.pxscxl.origin.bungee.api.manager.NotifyManager.getInstance().disable();
        de.pxscxl.origin.bungee.api.manager.OnlineTimeManager.getInstance().disable();
        de.pxscxl.origin.bungee.api.manager.OriginManager.getInstance().disable();
        de.pxscxl.origin.bungee.api.manager.PermissionManager.getInstance().disable();
        de.pxscxl.origin.bungee.api.manager.PointsManager.getInstance().disable();
        de.pxscxl.origin.bungee.api.manager.ReportManager.getInstance().disable();
    }


}
