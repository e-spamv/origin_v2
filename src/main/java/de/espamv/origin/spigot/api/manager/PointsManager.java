package de.claved.origin.spigot.api.manager;

import de.claved.origin.spigot.Origin;
import de.claved.origin.spigot.api.OriginPlayer;
import de.claved.origin.utils.session.Session;
import de.claved.origin.utils.session.query.QueryStatement;
import de.claved.origin.utils.session.query.UpdateStatement;
import de.claved.origin.spigot.api.events.PointsUpdateEvent;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PointsManager {

    @Getter
    private static PointsManager instance;

    private final QueryStatement getPoints;
    private final UpdateStatement setPoints;

    public PointsManager() {
        instance = this;
        Session session = Origin.getInstance().getSession();

        getPoints = session.prepareQueryStatement("SELECT points FROM players WHERE uuid = ?");
        setPoints = session.prepareUpdateStatement("UPDATE players SET points = ? WHERE uuid = ?");
    }

    public void disable() {
        instance = this;
    }

    public int getPoints(UUID uuid) {
        try {
            ResultSet resultSet = getPoints.execute(uuid);
            if (resultSet.next()) {
                return resultSet.getInt("points");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void setPoints(UUID uuid, int points) {
        try {
            setPoints.execute(points, uuid);

            OriginPlayer player = OriginManager.getInstance().getPlayer(uuid);
            if (player.isOnline()) {
                OriginManager.getInstance().sendPluginChannel(player, "updatePoints");
                PointsUpdateEvent pointsUpdateEvent = new PointsUpdateEvent(player);
                Bukkit.getPluginManager().callEvent(pointsUpdateEvent);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
