package de.claved.origin.bungee.api;

import de.claved.origin.bungee.api.manager.*;
import de.claved.origin.utils.enums.Language;
import de.claved.origin.utils.objects.origin.OriginPunishment;
import de.claved.origin.utils.enums.Rank;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class OriginPlayer {

    private ProxiedPlayer player;

    private final UUID uuid;
    private String name;

    private Language language;
    private Rank rank;

    private String socketAddress;
    private String firstLogin;
    private String lastLogin;
    private String server;

    private int coins;
    private int points;
    private int onlineTime;

    public OriginPlayer(UUID uuid) {
        this.uuid = uuid;
        this.name = OriginManager.getInstance().getName(uuid);

        language = LanguageManager.getInstance().getLanguage(uuid);
        rank = PermissionManager.getInstance().getRank(uuid);

        socketAddress = LoginManager.getInstance().getAddress(uuid);
        firstLogin = LoginManager.getInstance().getFirstLogin(uuid);
        lastLogin = LoginManager.getInstance().getLastLogin(uuid);
        server = LoginManager.getInstance().getServer(uuid);

        coins = CoinsManager.getInstance().getCoins(uuid);
        points = PointsManager.getInstance().getPoints(uuid);
        onlineTime = OnlineTimeManager.getInstance().getOnlineTime(uuid);
    }

    public ProxiedPlayer getProxiedPlayer() {
        return player;
    }

    public void setProxiedPlayer(ProxiedPlayer player) {
        this.player = player;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        OriginManager.getInstance().setName(uuid, name);
    }

    public Language getLanguage() {
        return language;
    }

    public <T> T language(T german, T english) {
        return (language.equals(Language.GERMAN) ? german : english);
    }

    public void setLanguage(Language language) {
        this.language = language;
        LanguageManager.getInstance().setLanguage(uuid, language);
    }

    public void updateLanguage() {
        language = LanguageManager.getInstance().getLanguage(uuid);
    }

    public String getSocketAddress() {
        return socketAddress;
    }

    public void setSocketAddress(InetSocketAddress address) {
        this.socketAddress = address.getAddress().getHostAddress();
        LoginManager.getInstance().setAddress(uuid, address);
    }

    public String getFirstLogin() {
        return firstLogin;
    }

    public void setFirstLogin(Date date) {
        this.firstLogin = date.toString();
        LoginManager.getInstance().setFirstLogin(uuid, date);
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date date) {
        this.lastLogin = date.toString();
        LoginManager.getInstance().setLastLogin(uuid, date);
    }

    public Rank getRank() {
        return rank;
    }

    public void setRank(Rank rank) {
        this.rank = rank;
        PermissionManager.getInstance().setRank(uuid, rank);
    }

    public boolean hasPriorityAccess(int needed) {
        return needed <= getRank().getPriority();
    }

    public String getDisplayName() {
        return getRank().getColor() + name;
    }

    public void sendMessage(String message) {
        player.sendMessage(message);
    }

    public void sendMessage(String german, String english) {
        sendMessage(language(german, english));
    }

    public void sendMessage(TextComponent... textComponent) {
        player.sendMessage(new TextComponent(textComponent));
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
        CoinsManager.getInstance().setCoins(uuid, this.coins);
    }

    public void addCoins(int coins) {
        this.coins = (this.coins + coins);
        CoinsManager.getInstance().setCoins(uuid, this.coins);
    }

    public void removeCoins(int coins) {
        this.coins = (this.coins - coins);
        CoinsManager.getInstance().setCoins(uuid, this.coins);
    }

    public void updateCoins() {
        coins = CoinsManager.getInstance().getCoins(uuid);
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
        PointsManager.getInstance().setPoints(uuid, this.points);
    }

    public void addPoints(int points) {
        this.points = (this.points + points);
        PointsManager.getInstance().setPoints(uuid, this.points);
    }

    public void removePoints(int points) {
        this.points = (this.points - points);
        PointsManager.getInstance().setPoints(uuid, this.points);
    }

    public void updatePoints() {
        points = PointsManager.getInstance().getPoints(uuid);
    }

    public int getOnlineTime() {
        return onlineTime;
    }

    public void updateOnlineTime() {
        onlineTime = OnlineTimeManager.getInstance().getOnlineTime(uuid);
    }

    public List<OriginPunishment> getActivePunishments() {
        return PunishmentManager.getInstance().getPunishments(uuid).stream().filter(OriginPunishment::isActive).collect(Collectors.toList());
    }

    public List<OriginPunishment> getPunishments() {
        return PunishmentManager.getInstance().getPunishments(uuid);
    }

    public boolean isOnline() {
        if (player == null) return false;
        return player.isConnected();
    }

    public int getPing() {
        return player.getPing();
    }

    public ServerInfo getServer() {
        if (server.equals("-")) return null;
        return ProxyServer.getInstance().getServerInfo(server);
    }

    public void setServer(String server) {
        this.server = server;
        LoginManager.getInstance().setServer(uuid, server);
    }

    public void connect(ServerInfo serverInfo) {
        player.connect(serverInfo);
    }

    public void disconnect() {
        player.disconnect();
    }

    public void disconnect(String reason) {
        player.disconnect(reason);
    }

    public PendingConnection getPendingConnection() {
        return player.getPendingConnection();
    }

    public void setTabHeader(BaseComponent[] header, BaseComponent[] footer) {
        player.setTabHeader(header, footer);
    }
}
