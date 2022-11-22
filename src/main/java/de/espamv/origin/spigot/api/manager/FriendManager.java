package de.claved.origin.spigot.api.manager;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.claved.origin.spigot.Origin;
import de.claved.origin.utils.objects.friends.Friend;
import de.claved.origin.utils.objects.friends.FriendRequest;
import de.claved.origin.utils.objects.friends.FriendSettings;
import de.claved.origin.utils.session.Session;
import de.claved.origin.utils.session.query.QueryStatement;
import de.claved.origin.utils.session.query.UpdateStatement;
import lombok.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FriendManager {

    @Getter
    private static FriendManager instance;

    @Getter
    private final String prefix = "§8┃ §4Friends §8× §r";

    private final UpdateStatement insertFriends;

    private final QueryStatement getFriend;
    private final QueryStatement getFriends;
    private final UpdateStatement deleteFriend;

    private final QueryStatement getRequest;
    private final QueryStatement getRequests;
    private final UpdateStatement deleteRequest;

    private final QueryStatement getSettingsObject;
    private final UpdateStatement setSettingsObject;

    public FriendManager() {
        instance = this;
        Session session = Origin.getInstance().getSession();

        insertFriends = session.prepareUpdateStatement("INSERT INTO friends (uuid, target, since) VALUES (?, ?, ?)");

        getFriend = session.prepareQueryStatement("SELECT * FROM friends WHERE uuid = ? AND target = ?");
        getFriends = session.prepareQueryStatement("SELECT * FROM friends WHERE uuid = ?");
        deleteFriend = session.prepareUpdateStatement("DELETE FROM friends WHERE uuid = ? AND target = ?");

        getRequest = session.prepareQueryStatement("SELECT * FROM friends_requests WHERE uuid = ? AND target = ?");
        getRequests = session.prepareQueryStatement("SELECT * FROM friends_requests WHERE uuid = ?");
        deleteRequest = session.prepareUpdateStatement("DELETE FROM friends_requests WHERE uuid = ? AND target = ?");

        getSettingsObject = session.prepareQueryStatement("SELECT object FROM friends_settings WHERE uuid = ?");
        setSettingsObject = session.prepareUpdateStatement("UPDATE friends_settings SET object = ? WHERE uuid = ?");
    }

    public void disable() {
        instance = null;
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

    public void deleteRequest(UUID uuid, UUID target) {
        try {
            deleteRequest.execute(uuid, target);
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
}
