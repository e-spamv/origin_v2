package de.claved.origin.bungee.api.manager;

import de.claved.origin.bungee.Origin;
import de.claved.origin.utils.session.Session;
import de.claved.origin.utils.session.query.QueryStatement;
import de.claved.origin.utils.session.query.UpdateStatement;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class OnlineTimeManager {

    @Getter
    private static OnlineTimeManager instance;

    private final QueryStatement getOnlineTime;
    private final UpdateStatement setOnlineTime;

    public OnlineTimeManager() {
        instance = this;
        Session session = Origin.getInstance().getSession();

        getOnlineTime = session.prepareQueryStatement("SELECT onlineTime FROM players WHERE uuid = ?");
        setOnlineTime = session.prepareUpdateStatement("UPDATE players SET onlineTime = ? WHERE uuid = ?");

        ProxyServer.getInstance().getScheduler().schedule(Origin.getInstance(), () -> {
            if (!OriginManager.getInstance().getPlayers().isEmpty()) {
                OriginManager.getInstance().getPlayers().forEach(players -> {
                    setOnlineTime(players.getUniqueId(), players.getOnlineTime() + 1);
                    players.updateOnlineTime();
                });
                OriginManager.getInstance().sendPluginChannel("updateOnlineTime");
            }
        }, 0, 1, TimeUnit.MINUTES);
    }

    public void disable() {
        instance = this;
    }

    public int getOnlineTime(UUID uuid) {
        try {
            ResultSet resultSet = getOnlineTime.execute(uuid);
            if (resultSet.next()) {
                return resultSet.getInt("onlineTime");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void setOnlineTime(UUID uuid, int time) {
        try {
            setOnlineTime.execute(time, uuid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
