package de.claved.origin.bungee.api.manager;

import de.claved.origin.bungee.Origin;
import de.claved.origin.utils.session.query.QueryStatement;
import de.claved.origin.utils.session.query.UpdateStatement;
import de.claved.origin.utils.objects.origin.OriginPunishment;
import de.claved.origin.utils.session.Session;
import lombok.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class PunishmentManager {

    @Getter
    private static PunishmentManager instance;

    private final UpdateStatement create;
    private final UpdateStatement insert;

    private final QueryStatement getPunishments;
    private final QueryStatement getAllPunishments;
    private final UpdateStatement deletePunishment;

    public PunishmentManager() {
        instance = this;
        //Session session = Origin.getInstance().getSession();
        Session session = Origin.getInstance().getSession();

        create = session.prepareUpdateStatement("CREATE TABLE IF NOT EXISTS punishments (id VARCHAR(100), reason VARCHAR(100), address VARCHAR(100), target VARCHAR(36), executor VARCHAR(36), since LONG, until LONG, type VARCHAR(4))");
        insert = session.prepareUpdateStatement("INSERT INTO punishments (id, reason, address, target, executor, since, until, type) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");

        getPunishments = session.prepareQueryStatement("SELECT * FROM punishments WHERE target = ?");
        getAllPunishments = session.prepareQueryStatement("SELECT * FROM punishments");
        deletePunishment = session.prepareUpdateStatement("UPDATE punishments SET until = ? WHERE id = ? AND target = ? AND type = ?");

        create();
    }

    public void disable() {
        instance = this;
    }

    private void create() {
        try {
            create.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public OriginPunishment executePunishment(OriginPunishment.PunishmentReason reason, String address, UUID target, UUID executor, Timestamp until, OriginPunishment.PunishmentType type) {
        try {
            String id = "#" + (new Random().nextInt(900000) + 100000);
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            insert.execute(id, reason.name(), address, target, executor, timestamp, until == null ? "null" : until, type.name());
            return new OriginPunishment(id, address, target, executor, timestamp, until, type, reason);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void executeUnpunish(String id, UUID target, OriginPunishment.PunishmentType type, Timestamp timestamp) {
        try {
            deletePunishment.execute(timestamp, id, target, type.name());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<OriginPunishment> getPunishments(UUID uuid) {
        List<OriginPunishment> punishments = new ArrayList<>();
        try {
            ResultSet resultSet = getPunishments.execute(uuid);
            while (resultSet.next()) {
                punishments.add(
                        new OriginPunishment(
                                resultSet.getString("id"),
                                resultSet.getString("address"),
                                UUID.fromString(resultSet.getString("target")),
                                UUID.fromString(resultSet.getString("executor")),
                                Timestamp.valueOf(resultSet.getString("since")),
                                resultSet.getString("until").equals("null") ? null : Timestamp.valueOf(resultSet.getString("until")),
                                OriginPunishment.PunishmentType.valueOf(resultSet.getString("type")),
                                OriginPunishment.PunishmentReason.valueOf(resultSet.getString("reason"))
                        )
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return punishments;
    }

    public List<OriginPunishment> getPunishments() {
        List<OriginPunishment> punishments = new ArrayList<>();
        try {
            ResultSet resultSet = getAllPunishments.execute();
            while (resultSet.next()) {
                punishments.add(
                        new OriginPunishment(
                                resultSet.getString("id"),
                                resultSet.getString("address"),
                                UUID.fromString(resultSet.getString("target")),
                                UUID.fromString(resultSet.getString("executor")),
                                Timestamp.valueOf(resultSet.getString("since")),
                                resultSet.getString("until").equals("null") ? null : Timestamp.valueOf(resultSet.getString("until")),
                                OriginPunishment.PunishmentType.valueOf(resultSet.getString("type")),
                                OriginPunishment.PunishmentReason.valueOf(resultSet.getString("reason"))
                        )
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return punishments;
    }
}
