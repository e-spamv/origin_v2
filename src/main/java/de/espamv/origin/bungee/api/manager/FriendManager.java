package de.claved.origin.bungee.api.manager;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.claved.origin.utils.objects.friends.FriendRequest;
import de.claved.origin.utils.session.query.QueryStatement;
import de.claved.origin.utils.session.query.UpdateStatement;
import de.claved.origin.bungee.Origin;
import de.claved.origin.bungee.api.OriginPlayer;
import de.claved.origin.utils.objects.friends.Friend;
import de.claved.origin.utils.objects.friends.FriendSettings;
import de.claved.origin.utils.session.Session;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class FriendManager implements Listener {

    @Getter
    private static FriendManager instance;

    @Getter
    private final String prefix = "§8[ §4Friends §8] §r";

    private final UpdateStatement createFriends;
    private final UpdateStatement createRequests;
    private final UpdateStatement createSettings;

    private final UpdateStatement insertFriends;
    private final UpdateStatement insertRequests;
    private final UpdateStatement insertSettings;

    private final QueryStatement existsFriends;
    private final QueryStatement existsRequests;
    private final QueryStatement existsSettings;

    private final QueryStatement getFriend;
    private final QueryStatement getFriends;
    private final UpdateStatement deleteFriend;

    private final QueryStatement getRequest;
    private final QueryStatement getRequests;
    private final UpdateStatement deleteRequest;

    private final QueryStatement getSettingsObject;
    private final UpdateStatement setSettingsObject;

    @Getter
    private final HashMap<OriginPlayer, OriginPlayer> replies = new HashMap<>();

    public FriendManager() {
        instance = this;
        ProxyServer.getInstance().getPluginManager().registerListener(Origin.getInstance(), this);
        Session session = Origin.getInstance().getSession();

        createFriends = session.prepareUpdateStatement("CREATE TABLE IF NOT EXISTS friends (uuid VARCHAR(36), target VARCHAR(36), since TIMESTAMP)");
        createRequests = session.prepareUpdateStatement("CREATE TABLE IF NOT EXISTS friends_requests (uuid VARCHAR(36), target VARCHAR(36), creation TIMESTAMP)");
        createSettings = session.prepareUpdateStatement("CREATE TABLE IF NOT EXISTS friends_settings (uuid VARCHAR(36), object TEXT)");

        insertFriends = session.prepareUpdateStatement("INSERT INTO friends (uuid, target, since) VALUES (?, ?, ?)");
        insertRequests = session.prepareUpdateStatement("INSERT INTO friends_requests (uuid, target, creation) VALUES (?, ?, ?)");
        insertSettings = session.prepareUpdateStatement("INSERT INTO friends_settings (uuid, object) VALUES (?, ?)");

        existsFriends = session.prepareQueryStatement("SELECT uuid FROM friends WHERE uuid = ? AND target = ?");
        existsRequests = session.prepareQueryStatement("SELECT uuid FROM friends_requests WHERE uuid = ? AND target = ?");
        existsSettings = session.prepareQueryStatement("SELECT uuid FROM friends_settings WHERE uuid = ?");

        getFriend = session.prepareQueryStatement("SELECT * FROM friends WHERE uuid = ? AND target = ?");
        getFriends = session.prepareQueryStatement("SELECT * FROM friends WHERE uuid = ?");
        deleteFriend = session.prepareUpdateStatement("DELETE FROM friends WHERE uuid = ? AND target = ?");

        getRequest = session.prepareQueryStatement("SELECT * FROM friends_requests WHERE uuid = ? AND target = ?");
        getRequests = session.prepareQueryStatement("SELECT * FROM friends_requests WHERE uuid = ?");
        deleteRequest = session.prepareUpdateStatement("DELETE FROM friends_requests WHERE uuid = ? AND target = ?");

        getSettingsObject = session.prepareQueryStatement("SELECT object FROM friends_settings WHERE uuid = ?");
        setSettingsObject = session.prepareUpdateStatement("UPDATE friends_settings SET object = ? WHERE uuid = ?");

        create();
    }

    public void disable() {
        instance = null;
    }

    private void create() {
        try {
            createFriends.execute();
            createRequests.execute();
            createSettings.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertFriends(UUID uuid, UUID target, long since) {
        try {
            insertFriends.execute(uuid, target, new Timestamp(since));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteFriend(UUID uuid, UUID target) {
        try {
            deleteFriend.execute(uuid, target);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertRequests(UUID uuid, UUID target, long creation) {
        try {
            insertRequests.execute(uuid, target, new Timestamp(creation));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteRequest(UUID uuid, UUID target) {
        try {
            deleteRequest.execute(uuid, target);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertSettings(UUID uuid) {
        try {
            JsonObject object = new JsonObject();
            object.addProperty("isReceivingMessage", true);
            object.addProperty("isReceivingRequests", true);
            object.addProperty("isDisplayedAsOnline", true);

            insertSettings.execute(uuid, object);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean existsFriends(UUID uuid, UUID target) {
        try {
            ResultSet resultSet = existsFriends.execute(uuid, target);
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean existsRequests(UUID uuid, UUID target) {
        try {
            ResultSet resultSet = existsRequests.execute(uuid, target);
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean existsSettings(UUID uuid) {
        try {
            ResultSet resultSet = existsSettings.execute(uuid);
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Friend> getFriends(UUID uuid) {
        List<Friend> friends = new ArrayList<>();
        try {
            ResultSet resultSet = getFriends.execute(uuid);
            while (resultSet.next()) {
                UUID target = UUID.fromString(resultSet.getString("target"));
                friends.add(new Friend(target, Timestamp.valueOf(resultSet.getString("since")), getSettings(target)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friends;
    }

    public Friend getFriend(UUID uuid, UUID target) {
        try {
            ResultSet resultSet = getFriend.execute(uuid, target);
            if (resultSet.next()) {
                return new Friend(target, Timestamp.valueOf(resultSet.getString("since")), getSettings(target));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<FriendRequest> getRequests(UUID uuid) {
        List<FriendRequest> requests = new ArrayList<>();
        try {
            ResultSet resultSet = getRequests.execute(uuid);
            while (resultSet.next()) {
                UUID target = UUID.fromString(resultSet.getString("target"));
                requests.add(new FriendRequest(target, Timestamp.valueOf(resultSet.getString("creation"))));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

    public FriendRequest getRequest(UUID uuid, UUID target) {
        try {
            ResultSet resultSet = getRequest.execute(uuid, target);
            if (resultSet.next()) {
                return new FriendRequest(target, Timestamp.valueOf(resultSet.getString("creation")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public FriendSettings getSettings(UUID uuid) {
        try {
            ResultSet resultSet = getSettingsObject.execute(uuid);
            if (resultSet.next()) {
                JsonObject object = (JsonObject) new JsonParser().parse(resultSet.getString("object"));
                return new FriendSettings(
                        object.get("isReceivingMessage").getAsBoolean(),
                        object.get("isReceivingRequests").getAsBoolean(),
                        object.get("isDisplayedAsOnline").getAsBoolean()
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setSettings(UUID uuid, FriendSettings friendSettings) {
        try {
            JsonObject object = new JsonObject();
            object.addProperty("isReceivingMessage", friendSettings.isReceivingMessage());
            object.addProperty("isReceivingRequests", friendSettings.isReceivingRequests());
            object.addProperty("isDisplayedAsOnline", friendSettings.isDisplayedAsOnline());

            setSettingsObject.execute(object, uuid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        OriginPlayer player = OriginManager.getInstance().getPlayer(event.getPlayer());

        ProxyServer.getInstance().getScheduler().runAsync(Origin.getInstance(), () -> {
            if (!existsSettings(player.getUniqueId())) {
                insertSettings(player.getUniqueId());
            }

            int count = getRequests(player.getUniqueId()).size();
            if (count != 0) {
                player.sendMessage(
                        prefix + "§7Du hast derzeit §e" + count + " §7offene " + (count == 1 ? "Freundschaftsanfrage" : "Freundschaftsanfragen"),
                        prefix + "§7You have currently §e" + count + " §7open " + (count == 1 ? "friend request" : "friend requests")
                );
            }
            count = (int) getFriends(player.getUniqueId()).stream().filter(friend -> OriginManager.getInstance().getPlayer(friend.getUuid()).isOnline()).count();
            if (count != 0) {
                player.sendMessage(
                        prefix + "§7Derzeit " + (count == 1 ? "ist" : "sind") + " §e" + count + " §7deiner Freunde online",
                        prefix + "§7Currently there " + (count == 1 ? "is" : "are") + " §e" + count + " §7of your friends online"
                );
            } else {
                player.sendMessage(
                        prefix + "§7Derzeit ist §ckeiner §7deiner Freunde online",
                        prefix + "§7Currently there are §cnone §7of your friens online"
                );
            }

            if (getSettings(player.getUniqueId()).isDisplayedAsOnline()) {
                OriginManager.getInstance().getPlayers().forEach(players -> {
                    if (getFriends(player.getUniqueId()).stream().map(Friend::getUuid).collect(Collectors.toList()).contains(players.getUniqueId())) {
                        players.sendMessage(
                                prefix + player.getDisplayName() + " §7ist nun §aonline",
                                prefix + player.getDisplayName() + " §7is now §aonline"
                        );
                    }
                });
            }
        });
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        OriginPlayer player = OriginManager.getInstance().getPlayer(event.getPlayer());

        replies.remove(player);

        ProxyServer.getInstance().getScheduler().runAsync(Origin.getInstance(), () -> {
            if (getSettings(player.getUniqueId()).isDisplayedAsOnline()) {
                OriginManager.getInstance().getPlayers().forEach(players -> {
                    if (getFriends(player.getUniqueId()).stream().map(Friend::getUuid).collect(Collectors.toList()).contains(players.getUniqueId())) {
                        players.sendMessage(
                                prefix + player.getDisplayName() + " §7ist nun §coffline",
                                prefix + player.getDisplayName() + " §7is now §coffline"
                        );
                    }
                });
            }
        });
    }
}
