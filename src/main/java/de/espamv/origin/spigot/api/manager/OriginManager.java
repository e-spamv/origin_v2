package de.claved.origin.spigot.api.manager;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.JsonObject;
import de.claved.origin.spigot.Origin;
import de.claved.origin.spigot.api.OriginPlayer;
import de.claved.origin.utils.session.Session;
import de.claved.origin.utils.session.query.QueryStatement;
import de.claved.origin.utils.session.query.UpdateStatement;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class OriginManager {

    @Getter
    private static OriginManager instance;

    private final QueryStatement getName;
    private final UpdateStatement setName;

    private final QueryStatement getUuid;
    private final UpdateStatement setUuid;

    private final QueryStatement getDatabasePlayers;

    private final Map<UUID, JsonObject> cache = new WeakHashMap<>();
    private final ReadWriteLock cacheLock = new ReentrantReadWriteLock();

    private final Map<Player, OriginPlayer> players = new WeakHashMap<>();
    private final ReadWriteLock playerLock = new ReentrantReadWriteLock();

    public OriginManager() {
        instance = this;
        Session session = Origin.getInstance().getSession();

        getName = session.prepareQueryStatement("SELECT name FROM players WHERE uuid = ?");
        setName = session.prepareUpdateStatement("UPDATE players SET name = ? WHERE uuid = ?");

        getUuid = session.prepareQueryStatement("SELECT uuid FROM players WHERE LOWER(name) = LOWER(?)");
        setUuid = session.prepareUpdateStatement("UPDATE players SET uuid = ? WHERE LOWER(name) = LOWER(?)");

        getDatabasePlayers = session.prepareQueryStatement("SELECT * FROM players");
    }

    public void disable() {
        instance = null;
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

    public JsonObject loadOriginPlayer(String name, UUID uuid) {
        if (getName(uuid) != null) {
            JsonObject object = new JsonObject();

            object.addProperty("uuid", uuid.toString());
            object.addProperty("name", name);

            object.addProperty("language", LanguageManager.getInstance().getLanguage(uuid).name());
            object.addProperty("rank", PermissionManager.getInstance().getRealRank(uuid).name());

            object.addProperty("socketAddress", LoginManager.getInstance().getAddress(uuid));
            object.addProperty("firstLogin", LoginManager.getInstance().getFirstLogin(uuid));
            object.addProperty("lastLogin", LoginManager.getInstance().getLastLogin(uuid));
            object.addProperty("server", LoginManager.getInstance().getServer(uuid));

            object.addProperty("coins", CoinsManager.getInstance().getCoins(uuid));
            object.addProperty("points", PointsManager.getInstance().getPoints(uuid));
            object.addProperty("onlineTime", OnlineTimeManager.getInstance().getOnlineTime(uuid));

            object.addProperty("nickState", NickManager.getInstance().getNickState(uuid));
            return object;
        }
        return null;
    }

    public void cache(UUID uuid, JsonObject object) {
        cacheLock.writeLock().lock();
        try {
            cache.put(uuid, object);
        } finally {
            cacheLock.writeLock().unlock();
        }
    }

    public JsonObject getCache(UUID uuid) {
        cacheLock.writeLock().lock();
        JsonObject originPlayer;
        try {
            originPlayer = cache.remove(uuid);
        } finally {
            cacheLock.writeLock().unlock();
        }
        return originPlayer;
    }

    public void addPlayer(OriginPlayer player) {
        playerLock.writeLock().lock();
        try {
            players.put(player.getBukkitPlayer(), player);
        } finally {
            playerLock.writeLock().unlock();
        }
    }

    public void removePlayer(OriginPlayer player) {
        players.remove(player.getBukkitPlayer());
    }

    public OriginPlayer getPlayer(UUID uuid) {
        OriginPlayer player = getPlayer(Bukkit.getPlayer(uuid));
        if (player != null) {
            return player;
        } else {
            if (getName(uuid) != null) {
                return new OriginPlayer(loadOriginPlayer(getName(uuid), uuid));
            } else {
                return null;
            }
        }
    }

    public OriginPlayer getPlayer(String name) {
        OriginPlayer player = getPlayer(Bukkit.getPlayer(name));
        if (player != null) {
            return player;
        } else {
            if (getUuid(name) != null) {
                return new OriginPlayer(loadOriginPlayer(name, getUuid(name)));
            } else {
                return null;
            }
        }
    }

    public OriginPlayer getPlayer(Player player) {
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
            players.forEach((bukkitPlayer, player) -> {
                if (player.isLocalOnline()) set.add(player);
            });
            return Collections.unmodifiableCollection(set);
        } finally {
            playerLock.readLock().unlock();
        }
    }

    public List<OriginPlayer> getDatabasePlayers() {
        List<OriginPlayer> players = new ArrayList<>();
        try {
            ResultSet resultSet = getDatabasePlayers.execute();
            while (resultSet.next()) {
                players.add(new OriginPlayer(loadOriginPlayer(resultSet.getString("name"), UUID.fromString(resultSet.getString("uuid")))));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return players;
    }

    public void sendPluginChannel(OriginPlayer player, String subChannel) {
        ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
        byteArrayDataOutput.writeUTF(subChannel);
        byteArrayDataOutput.writeUTF(player.getUniqueId().toString());
        player.sendPluginMessage(Origin.getInstance(), "origin", byteArrayDataOutput.toByteArray());
    }

    public Class<?> getVersionSpecificClass(String name) {
        try {
            return Class.forName(name.replace("version", Bukkit.getServer().getClass().getName().split("\\.")[3]));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
