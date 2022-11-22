package de.claved.origin.bungee.api.manager;

import de.claved.origin.bungee.Origin;
import de.claved.origin.bungee.api.OriginPlayer;
import de.claved.origin.utils.session.Session;
import de.claved.origin.utils.session.query.QueryStatement;
import de.claved.origin.utils.session.query.UpdateStatement;
import lombok.Getter;

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
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
