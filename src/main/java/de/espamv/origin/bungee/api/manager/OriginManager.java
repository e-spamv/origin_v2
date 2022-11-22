package de.claved.origin.bungee.api.manager;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.claved.origin.bungee.Origin;
import de.claved.origin.bungee.api.OriginPlayer;
import de.claved.origin.utils.enums.Language;
import de.claved.origin.utils.enums.Rank;
import de.claved.origin.utils.session.Session;
import de.claved.origin.utils.session.query.QueryStatement;
import de.claved.origin.utils.session.query.UpdateStatement;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.net.InetSocketAddress;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class OriginManager {

    @Getter
    private static OriginManager instance;

    private final UpdateStatement create;
    private final UpdateStatement insert;
    private final QueryStatement exists;

    private final QueryStatement getName;
    private final UpdateStatement setName;

    private final QueryStatement getUuid;
    private final UpdateStatement setUuid;

    private final QueryStatement getAllPlayers;

    private final Map<ProxiedPlayer, OriginPlayer> players = new WeakHashMap<>();
    private final ReadWriteLock playerLock = new ReentrantReadWriteLock();

    public OriginManager() {
        instance = this;
        Session session = Origin.getInstance().getSession();

        create = session.prepareUpdateStatement("CREATE TABLE IF NOT EXISTS players (name VARCHAR(16), uuid VARCHAR(36), rank VARCHAR(100), address VARCHAR(20), language VARCHAR(20), firstLogin VARCHAR(100), lastLogin VARCHAR(100), server VARCHAR(100), coins INT(100), points INT(100), onlineTime INT(100), nickState BOOLEAN)");
        insert = session.prepareUpdateStatement("INSERT INTO players (name, uuid, rank, address, language, firstLogin, lastLogin, server, coins, points, onlineTime, nickState) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        exists = session.prepareQueryStatement("SELECT uuid FROM players WHERE uuid = ?");

        getName = session.prepareQueryStatement("SELECT name FROM players WHERE uuid = ?");
        setName = session.prepareUpdateStatement("UPDATE players SET name = ? WHERE uuid = ?");

        getUuid = session.prepareQueryStatement("SELECT uuid FROM players WHERE LOWER(name) = LOWER(?)");
        setUuid = session.prepareUpdateStatement("UPDATE players SET uuid = ? WHERE LOWER(name) = LOWER(?)");

        getAllPlayers = session.prepareQueryStatement("SELECT * FROM players");

        create();
    }

    public void disable() {
        instance = null;
    }

    private void create() {
        try {
            create.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insert(String name, UUID uuid, InetSocketAddress address) {
        try {
            Language language = LanguageManager.getInstance().fromIp(address.getAddress().getHostAddress());
            insert.execute(name, uuid, Rank.PLAYER.getId(), address.getAddress().getHostAddress(), language.name(), new Date(), new Date(), "-", 0, 0, 0, 0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean exists(UUID uuid) {
        try {
            ResultSet resultSet = exists.execute(uuid);
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getName(UUID uuid) {
        try {
            ResultSet resultSet = getName.execute(uuid);
            if (resultSet.next()) {
                return resultSet.getString("name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setName(UUID uuid, String name) {
        try {
            setName.execute(name, uuid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public UUID getUuid(String name) {
        try {
            ResultSet resultSet = getUuid.execute(name);
            if (resultSet.next()) {
                return UUID.fromString(resultSet.getString("uuid"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setUuid(String name, UUID uuid) {
        try {
            setUuid.execute(uuid, name);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addPlayer(OriginPlayer player) {
        playerLock.writeLock().lock();
        try {
            players.put(player.getProxiedPlayer(), player);
        } finally {
            playerLock.writeLock().unlock();
        }
    }

    public void removePlayer(OriginPlayer player) {
        players.remove(player.getProxiedPlayer());
    }

    public OriginPlayer getPlayer(UUID uuid) {
        OriginPlayer player = getPlayer(ProxyServer.getInstance().getPlayer(uuid));
        if (player != null) {
            return player;
        } else {
            if (getName(uuid) != null) {
                return new OriginPlayer(uuid);
            } else {
                return null;
            }
        }
    }

    public OriginPlayer getPlayer(String name) {
        OriginPlayer player = getPlayer(ProxyServer.getInstance().getPlayer(name));
        if (player != null) {
            return player;
        } else {
            if (getUuid(name) != null) {
                return new OriginPlayer(getUuid(name));
            } else {
                return null;
            }
        }
    }

    public OriginPlayer getPlayer(ProxiedPlayer player) {
        playerLock.readLock().lock();
        OriginPlayer originPlayer;
        try {
            originPlayer = players.get(player);
        } finally {
            playerLock.readLock().unlock();
        }
        return originPlayer;
    }

    public Collection<OriginPlayer> getPlayers() {
        playerLock.readLock().lock();
        try {
            Set<OriginPlayer> set = new HashSet<>();
            players.forEach((proxiedPlayer, player) -> set.add(player));
            return Collections.unmodifiableCollection(set);
        } finally {
            playerLock.readLock().unlock();
        }
    }

    public List<OriginPlayer> getDatabasePlayers() {
        List<OriginPlayer> players = new ArrayList<>();
        try {
            ResultSet resultSet = getAllPlayers.execute();
            while (resultSet.next()) {
                players.add(new OriginPlayer(UUID.fromString(resultSet.getString("uuid"))));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return players;
    }

    public void sendPluginChannel(String subChannel) {
        ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
        byteArrayDataOutput.writeUTF(subChannel);
        ProxyServer.getInstance().getServers().values().stream().filter(serverInfo -> !serverInfo.getPlayers().isEmpty()).collect(Collectors.toList()).forEach(serverInfo -> serverInfo.sendData("origin", byteArrayDataOutput.toByteArray()));
    }

    public <T> void sendPluginChannel(String subChannel, String identifier, T value) {
        ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
        byteArrayDataOutput.writeUTF(subChannel);
        byteArrayDataOutput.writeUTF(identifier);
        byteArrayDataOutput.writeUTF(value.toString());
        ProxyServer.getInstance().getServers().values().stream().filter(serverInfo -> !serverInfo.getPlayers().isEmpty()).collect(Collectors.toList()).forEach(serverInfo -> serverInfo.sendData("origin", byteArrayDataOutput.toByteArray()));
    }

    public void sendPluginChannel(OriginPlayer player, String subChannel) {
        ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
        byteArrayDataOutput.writeUTF(subChannel);
        byteArrayDataOutput.writeUTF(player.getUniqueId().toString());
        if (player.getServer() != null) {
            player.getServer().sendData("origin", byteArrayDataOutput.toByteArray());
        }
    }
}
