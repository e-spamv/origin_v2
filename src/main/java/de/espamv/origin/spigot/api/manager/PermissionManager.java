package de.claved.origin.spigot.api.manager;

import de.claved.origin.spigot.Origin;
import de.claved.origin.spigot.api.OriginPlayer;
import de.claved.origin.utils.enums.Rank;
import de.claved.origin.utils.session.Session;
import de.claved.origin.utils.session.query.QueryStatement;
import de.claved.origin.utils.session.query.UpdateStatement;
import de.claved.origin.spigot.api.events.PermissionUpdateEvent;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.ServerOperator;

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
        Session session = Origin.getInstance().getSession();

        getRank = session.prepareQueryStatement("SELECT rank FROM players WHERE uuid = ?");
        setRank = session.prepareUpdateStatement("UPDATE players SET rank = ? WHERE uuid = ?");
    }

    public void disable() {
        instance = this;
    }

    public Rank getRank(UUID uuid) {
        if (NickManager.getInstance().getNickName(uuid) != null) {
            return Rank.PLAYER;
        }
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

    public Rank getRealRank(UUID uuid) {
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
                PermissionUpdateEvent permissionUpdateEvent = new PermissionUpdateEvent(player);
                Bukkit.getPluginManager().callEvent(permissionUpdateEvent);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static class PermissibleOverride extends PermissibleBase {

        private final OriginPlayer player;

        public PermissibleOverride(ServerOperator operator, OriginPlayer player) {
            super(operator);
            this.player = player;
        }

        @Override
        public boolean hasPermission(String inName) {
            return player.getBukkitPlayer().isOp() || player.hasPriorityAccess(Rank.ADMINISTRATOR.getPriority());
        }

        @Override
        public boolean hasPermission(Permission perm) {
            return super.hasPermission(perm);
        }

    }
}