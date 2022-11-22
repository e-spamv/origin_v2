package de.claved.origin.bungee.api.manager;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.claved.origin.bungee.Origin;
import de.claved.origin.bungee.api.OriginPlayer;
import de.claved.origin.utils.enums.ClanRank;
import de.claved.origin.utils.objects.clans.Clan;
import de.claved.origin.utils.objects.clans.ClanRequest;
import de.claved.origin.utils.objects.clans.ClanSettings;
import de.claved.origin.utils.session.Session;
import de.claved.origin.utils.session.query.QueryStatement;
import de.claved.origin.utils.session.query.UpdateStatement;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ClanManager implements Listener {

    @Getter
    private static ClanManager instance;

    @Getter
    private final String prefix = "§8[ §6Clan §8] §r";

    private final UpdateStatement createClans;
    private final UpdateStatement createRequests;
    private final UpdateStatement createSettings;

    private final UpdateStatement insertClans;
    private final UpdateStatement insertRequests;
    private final UpdateStatement insertSettings;

    private final QueryStatement existsSettings;

    private final QueryStatement getClan;
    private final QueryStatement getClans;
    private final UpdateStatement setLeader;
    private final UpdateStatement updateClan;
    private final UpdateStatement deleteClan;

    private final QueryStatement getRequest;
    private final QueryStatement getRequests;
    private final UpdateStatement updateRequests;
    private final UpdateStatement deleteRequest;

    private final QueryStatement getSettingsObjects;
    private final QueryStatement getSettingObject;
    private final UpdateStatement setSettingsObject;

    public ClanManager() {
        instance = this;
        ProxyServer.getInstance().getPluginManager().registerListener(Origin.getInstance(), this);
        Session session = Origin.getInstance().getSession();

        createClans = session.prepareUpdateStatement("CREATE TABLE IF NOT EXISTS clans (leader VARCHAR(36), object TEXT)");
        createRequests = session.prepareUpdateStatement("CREATE TABLE IF NOT EXISTS clans_requests (target VARCHAR(36), clan VARCHAR(100), creation TIMESTAMP)");
        createSettings = session.prepareUpdateStatement("CREATE TABLE IF NOT EXISTS clans_settings (uuid VARCHAR(36), object TEXT)");

        insertClans = session.prepareUpdateStatement("INSERT INTO clans (leader, object) VALUES (?, ?)");
        insertRequests = session.prepareUpdateStatement("INSERT INTO clans_requests (target, clan, creation) VALUES (?, ?, ?)");
        insertSettings = session.prepareUpdateStatement("INSERT INTO clans_settings (uuid, object) VALUES (?, ?)");

        existsSettings = session.prepareQueryStatement("SELECT uuid FROM clans_settings WHERE uuid = ?");

        getClan = session.prepareQueryStatement("SELECT * FROM clans WHERE leader = ?");
        getClans = session.prepareQueryStatement("SELECT * FROM clans");
        setLeader = session.prepareUpdateStatement("UPDATE clans SET leader = ? WHERE leader = ?");
        updateClan = session.prepareUpdateStatement("UPDATE clans SET object = ? WHERE leader = ?");
        deleteClan = session.prepareUpdateStatement("DELETE FROM clans WHERE leader = ?");

        getRequest = session.prepareQueryStatement("SELECT * FROM clans_requests WHERE target = ? AND clan = ?");
        getRequests = session.prepareQueryStatement("SELECT * FROM clans_requests WHERE target = ?");
        deleteRequest = session.prepareUpdateStatement("DELETE FROM clans_requests WHERE target = ? AND clan = ?");
        updateRequests = session.prepareUpdateStatement("UPDATE clans_requests SET clan = ? WHERE clan = ? ");

        getSettingsObjects = session.prepareQueryStatement("SELECT * FROM clans_settings");
        getSettingObject = session.prepareQueryStatement("SELECT * FROM clans_settings WHERE uuid = ?");
        setSettingsObject = session.prepareUpdateStatement("UPDATE clans_settings SET object = ? WHERE uuid = ?");

        create();
    }

    public void disable() {
        instance = null;
    }

    private void create() {
        try {
            createClans.execute();
            createRequests.execute();
            createSettings.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertClan(UUID leader, String name, String tag, String description, int elo, long creation) {
        try {
            JsonObject object = new JsonObject();
            object.addProperty("name", name);
            object.addProperty("tag", tag);
            object.addProperty("description", description);
            object.addProperty("color", ChatColor.YELLOW.name());
            object.addProperty("elo", elo);
            object.addProperty("creation", creation);

            insertClans.execute(leader, object);

            ClanSettings settings = getSettings(leader);
            settings.setClanName(name);
            settings.setClanRank(ClanRank.LEADER);
            setSettings(leader, settings);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertRequests(UUID target, Clan clan, long creation) {
        try {
            insertRequests.execute(target, clan.getName(), new Timestamp(creation));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertSettings(UUID uuid) {
        try {
            JsonObject object = new JsonObject();
            object.addProperty("clan", "-");
            object.addProperty("clanRank", ClanRank.NONE.name());
            object.addProperty("isReceivingMessage", true);
            object.addProperty("isReceivingRequests", true);
            object.addProperty("isDisplayedAsOnline", true);

            insertSettings.execute(uuid, object);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteRequest(UUID target, Clan clan) {
        try {
            deleteRequest.execute(target, clan.getName());
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    public void setClanLeader(UUID oldLeader, UUID newLeader) {
        try {
            setLeader.execute(newLeader, oldLeader);

            ClanSettings settings = ClanManager.getInstance().getSettings(oldLeader);
            settings.setClanRank(ClanRank.MODERATOR);
            ClanManager.getInstance().setSettings(oldLeader, settings);

            settings = ClanManager.getInstance().getSettings(newLeader);
            settings.setClanRank(ClanRank.LEADER);
            ClanManager.getInstance().setSettings(newLeader, settings);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateClan(UUID leader, Clan clan) {
        try {
            JsonObject object = new JsonObject();
            object.addProperty("name", clan.getName());
            object.addProperty("tag", clan.getTag());
            object.addProperty("description", clan.getDescription());
            object.addProperty("color", clan.getColor().name());
            object.addProperty("elo", clan.getElo());
            object.addProperty("creation", clan.getCreation().getTime());

            if (!Objects.equals(clan.getName(), getClanByLeader(leader).getName())) {
                updateRequests.execute(clan.getName(), getClanByLeader(leader).getName());

                clan.getAllMembers().forEach(uuid -> {
                    ClanSettings settings = getSettings(uuid);
                    settings.setClanName(clan.getName());
                    setSettings(uuid, settings);
                });
            }

            updateClan.execute(object, leader);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteClan(UUID leader, Clan clan) {
        try {
            deleteClan.execute(leader);

            clan.getAllMembers().forEach(uuid -> {
                ClanSettings settings = getSettings(uuid);
                settings.setClanName("-");
                settings.setClanRank(ClanRank.NONE);
                setSettings(uuid, settings);
            });
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
                        prefix + "§7Du hast derzeit §e" + count + " §7offene " + (count == 1 ? "Clan Einladung" : "Clan Einladungen"),
                        prefix + "§7You have currently §e" + count + " §7open " + (count == 1 ? "clan invite" : "clan invites")
                );
            }
        });
    }
}
