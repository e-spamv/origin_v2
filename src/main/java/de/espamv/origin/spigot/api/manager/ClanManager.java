package de.claved.origin.spigot.api.manager;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.claved.origin.spigot.Origin;
import de.claved.origin.utils.enums.ClanRank;
import de.claved.origin.utils.objects.clans.Clan;
import de.claved.origin.utils.objects.clans.ClanRequest;
import de.claved.origin.utils.objects.clans.ClanSettings;
import de.claved.origin.utils.session.Session;
import de.claved.origin.utils.session.query.QueryStatement;
import de.claved.origin.utils.session.query.UpdateStatement;
import lombok.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ClanManager {

    @Getter
    private static ClanManager instance;

    @Getter
    private final String prefix = "§8┃ §6Clan §8× §r";

    private final QueryStatement getClan;
    private final QueryStatement getClans;

    private final QueryStatement getRequest;
    private final QueryStatement getRequests;

    private final QueryStatement getSettingsObjects;
    private final QueryStatement getSettingObject;
    private final UpdateStatement setSettingsObject;

    public ClanManager() {
        instance = this;
        //Session session = Origin.getInstance().getSession();
        Session session = Origin.getInstance().getSession();

        getClan = session.prepareQueryStatement("SELECT * FROM clans WHERE leader = ?");
        getClans = session.prepareQueryStatement("SELECT * FROM clans");

        getRequest = session.prepareQueryStatement("SELECT * FROM clans_requests WHERE target = ? AND clan = ?");
        getRequests = session.prepareQueryStatement("SELECT * FROM clans_requests WHERE target = ?");

        getSettingsObjects = session.prepareQueryStatement("SELECT * FROM clans_settings");
        getSettingObject = session.prepareQueryStatement("SELECT * FROM clans_settings WHERE uuid = ?");
        setSettingsObject = session.prepareUpdateStatement("UPDATE clans_settings SET object = ? WHERE uuid = ?");
    }

    public void disable() {
        instance = null;
    }

    public List<ClanRequest> getRequests(UUID uuid) {
        List<ClanRequest> requests = new ArrayList<>();
        try {
            ResultSet resultSet = getRequests.execute(uuid);
            while (resultSet.next()) {
                Clan clan = getClans().stream().filter(clans -> {
                    try {
                        return clans.getName().equals(resultSet.getString("clan"));
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    return false;
                }).findFirst().orElse(null);
                if (clan != null) {
                    requests.add(new ClanRequest(uuid, clan, Timestamp.valueOf(resultSet.getString("creation"))));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

    public ClanRequest getRequest(UUID target, Clan clan) {
        try {
            ResultSet resultSet = getRequest.execute(target, clan.getName());
            if (resultSet.next()) {
                return new ClanRequest(target, clan, Timestamp.valueOf(resultSet.getString("creation")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ClanSettings getSettings(UUID uuid) {
        try {
            ResultSet resultSet = getSettingObject.execute(uuid);
            if (resultSet.next()) {
                JsonObject object = (JsonObject) new JsonParser().parse(resultSet.getString("object"));
                return new ClanSettings(
                        UUID.fromString(resultSet.getString("uuid")),
                        object.get("clan").getAsString(),
                        ClanRank.valueOf(object.get("clanRank").getAsString()),
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

    public List<ClanSettings> getSettings() {
        List<ClanSettings> settings = new ArrayList<>();
        try {
            ResultSet resultSet = getSettingsObjects.execute();
            while (resultSet.next()) {
                JsonObject object = (JsonObject) new JsonParser().parse(resultSet.getString("object"));
                settings.add(new ClanSettings(
                        UUID.fromString(resultSet.getString("uuid")),
                        object.get("clan").getAsString(),
                        ClanRank.valueOf(object.get("clanRank").getAsString()),
                        object.get("isReceivingMessage").getAsBoolean(),
                        object.get("isReceivingRequests").getAsBoolean(),
                        object.get("isDisplayedAsOnline").getAsBoolean()
                ));
            }
            return settings;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return settings;
    }

    public void setSettings(UUID uuid, ClanSettings clanSettings) {
        try {
            JsonObject object = new JsonObject();
            object.addProperty("clan", clanSettings.getClanName());
            object.addProperty("clanRank", clanSettings.getClanRank().name());
            object.addProperty("isReceivingMessage", clanSettings.isReceivingMessage());
            object.addProperty("isReceivingRequests", clanSettings.isReceivingRequests());
            object.addProperty("isDisplayedAsOnline", clanSettings.isDisplayedAsOnline());

            setSettingsObject.execute(object, uuid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Clan getClanByLeader(UUID leader) {
        try {
            ResultSet resultSet = getClan.execute(leader);
            if (resultSet.next()) {
                JsonObject object = (JsonObject) new JsonParser().parse(resultSet.getString("object"));
                return new Clan(
                        UUID.fromString(resultSet.getString("leader")),
                        object.get("name").getAsString(),
                        object.get("tag").getAsString(),
                        object.get("description").getAsString(),
                        object.get("color").getAsString(),
                        object.get("elo").getAsInt(),
                        new Timestamp(object.get("creation").getAsLong())
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Clan> getClans() {
        List<Clan> clans = new ArrayList<>();
        try {
            ResultSet resultSet = getClans.execute();
            while (resultSet.next()) {
                JsonObject object = (JsonObject) new JsonParser().parse(resultSet.getString("object"));
                clans.add(new Clan(
                        UUID.fromString(resultSet.getString("leader")),
                        object.get("name").getAsString(),
                        object.get("tag").getAsString(),
                        object.get("description").getAsString(),
                        object.get("color").getAsString(),
                        object.get("elo").getAsInt(),
                        new Timestamp(object.get("creation").getAsLong())
                ));
            }
            return clans;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clans;
    }

    public Clan getClanByUuid(UUID uuid) {
        return getClans().stream().filter(clan -> Objects.equals(clan.getName(), getSettings(uuid).getClanName())).findFirst().orElse(null);
    }
}
