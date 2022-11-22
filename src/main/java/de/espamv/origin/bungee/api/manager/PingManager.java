package de.claved.origin.bungee.api.manager;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.claved.origin.bungee.Origin;
import de.claved.origin.utils.objects.origin.OriginServerPing;
import de.claved.origin.utils.session.Session;
import de.claved.origin.utils.session.query.QueryStatement;
import de.claved.origin.utils.session.query.UpdateStatement;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PingManager {

    @Getter
    private static PingManager instance;

    private final UpdateStatement create;
    private final UpdateStatement insert;
    private final QueryStatement exists;

    private final QueryStatement getJsonObject;
    private final UpdateStatement setJsonObject;

    @Getter
    private OriginServerPing originServerPing;

    public PingManager() {
        instance = this;
        //Session session = de.claved.origin.spigot.Origin.getInstance().getSession();
        Session session = Origin.getInstance().getSession();

        create = session.prepareUpdateStatement("CREATE TABLE IF NOT EXISTS ping_information (object TEXT)");
        insert = session.prepareUpdateStatement("INSERT INTO ping_information (object) VALUES (?)");
        exists = session.prepareQueryStatement("SELECT object FROM ping_information");

        getJsonObject = session.prepareQueryStatement("SELECT object FROM ping_information");
        setJsonObject = session.prepareUpdateStatement("UPDATE ping_information SET object = ?");

        create();

        ProxyServer.getInstance().getScheduler().runAsync(Origin.getInstance(), () -> {
            if (!exists()) {
                insert();
            }
            originServerPing = getObject();
        });
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

    private void insert() {
        try {
            JsonObject object = new JsonObject();
            object.addProperty("maintenance", true);
            object.addProperty("fakePlayers", false);
            object.addProperty("slots", 250);
            object.addProperty("fakePlayersCount", 0);
            object.addProperty("headerMotd", "§7Default Motd");
            object.addProperty("footerMotd", "§7Default Motd");

            insert.execute(object);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean exists() {
        try {
            ResultSet resultSet = exists.execute();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private OriginServerPing getObject() {
        try {
            ResultSet resultSet = getJsonObject.execute();
            if (resultSet.next()) {
                JsonObject object = (JsonObject) new JsonParser().parse(resultSet.getString("object"));
                return new OriginServerPing(
                        object.get("headerMotd").getAsString(),
                        object.get("footerMotd").getAsString(),
                        object.get("slots").getAsInt(),
                        object.get("fakePlayersCount").getAsInt(),
                        object.get("maintenance").getAsBoolean(),
                        object.get("fakePlayers").getAsBoolean()
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setOriginServerPing(OriginServerPing serverPing) {
        try {
            JsonObject object = new JsonObject();
            object.addProperty("maintenance", serverPing.isMaintenance());
            object.addProperty("fakePlayers", serverPing.isFakePlayers());
            object.addProperty("slots", serverPing.getSlots());
            object.addProperty("fakePlayersCount", serverPing.getFakePlayersCount());
            object.addProperty("headerMotd", serverPing.getHeaderMotd());
            object.addProperty("footerMotd", serverPing.getFooterMotd());

            setJsonObject.execute(object);

            originServerPing = serverPing;

            OriginManager.getInstance().sendPluginChannel("updateNetworkData", "ping", object);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
