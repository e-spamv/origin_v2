package de.claved.origin.spigot.api.manager;

import de.claved.origin.spigot.Origin;
import de.claved.origin.spigot.api.OriginPlayer;
import de.claved.origin.utils.session.Session;
import de.claved.origin.utils.session.query.QueryStatement;
import de.claved.origin.utils.session.query.UpdateStatement;
import de.claved.origin.spigot.api.events.CoinsUpdateEvent;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

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

            OriginPlayer player = OriginManager.getInstance().getPlayer(uuid);
            if (player.isOnline()) {
                OriginManager.getInstance().sendPluginChannel(player, "updateOnlineTime");
                CoinsUpdateEvent coinsUpdateEvent = new CoinsUpdateEvent(player);
                Bukkit.getPluginManager().callEvent(coinsUpdateEvent);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
