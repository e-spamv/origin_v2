package de.claved.origin.bungee.api.manager;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import de.claved.origin.bungee.Origin;
import de.claved.origin.bungee.api.OriginPlayer;
import de.claved.origin.utils.session.Session;
import de.claved.origin.utils.session.query.QueryStatement;
import de.claved.origin.utils.session.query.UpdateStatement;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class NotifyManager implements Listener {

    @Getter
    private static NotifyManager instance;

    @Getter
    private final String prefix = "§8[ §9Notify §8] §r";

    private final UpdateStatement create;
    private final UpdateStatement insert;
    private final QueryStatement exists;

    private final QueryStatement getJsonObject;
    private final UpdateStatement setJsonObject;

    public NotifyManager() {
        instance = this;
        ProxyServer.getInstance().getPluginManager().registerListener(Origin.getInstance(), this);
        Session session = Origin.getInstance().getSession();

        create = session.prepareUpdateStatement("CREATE TABLE IF NOT EXISTS notifies (uuid VARCHAR(36), object TEXT)");
        insert = session.prepareUpdateStatement("INSERT INTO notifies (uuid, object) VALUES (?, ?)");
        exists = session.prepareQueryStatement("SELECT uuid FROM notifies WHERE uuid = ?");

        getJsonObject = session.prepareQueryStatement("SELECT object FROM notifies WHERE uuid = ?");
        setJsonObject = session.prepareUpdateStatement("UPDATE notifies SET object = ? WHERE uuid = ?");

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

    public void insert(UUID uuid) {
        try {
            JsonObject object = new JsonObject();
            object.addProperty("ban", true);
            object.addProperty("mute", true);
            object.addProperty("kick", true);
            object.addProperty("report", true);
            object.addProperty("appeals", true);

            insert.execute(uuid, object);
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

    public JsonObject getObject(UUID uuid) {
        try {
            ResultSet resultSet = getJsonObject.execute(uuid);
            if (resultSet.next()) {
                try {
                    return (JsonObject) new JsonParser().parse(resultSet.getString("object"));
                } catch (JsonParseException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new JsonObject();
    }

    public void setObject(UUID uuid, JsonObject object) {
        try {
            setJsonObject.execute(object, uuid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        OriginPlayer player = OriginManager.getInstance().getPlayer(event.getPlayer());

        ProxyServer.getInstance().getScheduler().runAsync(Origin.getInstance(), () -> {
            if (!exists(player.getUniqueId())) {
                insert(player.getUniqueId());
            }
        });
    }
}
