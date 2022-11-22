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

public class CoinsManager {

    @Getter
    private static CoinsManager instance;

    private final QueryStatement getCoins;
    private final UpdateStatement setCoins;

    public CoinsManager() {
        instance = this;
        Session session = Origin.getInstance().getSession();

        getCoins = session.prepareQueryStatement("SELECT coins FROM players WHERE uuid = ?");
        setCoins = session.prepareUpdateStatement("UPDATE players SET coins = ? WHERE uuid = ?");
    }

    public void disable() {
        instance = this;
    }

    public int getCoins(UUID uuid) {
        try {
            ResultSet resultSet = getCoins.execute(uuid);
            if (resultSet.next()) {
                return resultSet.getInt("coins");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void setCoins(UUID uuid, int coins) {
        try {
            setCoins.execute(coins, uuid);

            OriginPlayer player = OriginManager.getInstance().getPlayer(uuid);
            if (player.isOnline()) {
                OriginManager.getInstance().sendPluginChannel(player, "updateCoins");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
