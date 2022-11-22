package de.claved.origin.bungee.api.manager;

import de.claved.origin.bungee.Origin;
import de.claved.origin.bungee.api.OriginPlayer;
import de.claved.origin.utils.enums.Rank;
import de.claved.origin.utils.session.Session;
import de.claved.origin.utils.session.query.QueryStatement;
import de.claved.origin.utils.session.query.UpdateStatement;
import lombok.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public class PermissionManager {

    @Getter
    private static PermissionManager instance;

    private final QueryStatement getRank;
    private final UpdateStatement setRank;

    public PermissionManager() {
        instance = this;
       // Session session = Origin.getInstance().getSession();
        Session session = Origin.getInstance().getSession();

        getRank = session.prepareQueryStatement("SELECT rank FROM players WHERE uuid = ?");
        setRank = session.prepareUpdateStatement("UPDATE players SET rank = ? WHERE uuid = ?");
    }

    public void disable() {
        instance = this;
    }

    public Rank getRank(UUID uuid) {
        try {
            ResultSet resultSet = getRank.execute(uuid);
            if (resultSet.next()) {
                String id = resultSet.getString("rank");
                return Arrays.stream(Rank.values()).filter(rank -> Objects.equals(rank.getId(), id)).findFirst().orElse(null);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setRank(UUID uuid, Rank rank) {
        try {
            setRank.execute(rank.getId(), uuid);

            OriginPlayer player = OriginManager.getInstance().getPlayer(uuid);
            if (player.isOnline()) {
                OriginManager.getInstance().sendPluginChannel(player, "updateRank");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}