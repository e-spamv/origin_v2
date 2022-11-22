package de.claved.origin.bungee.api.manager;

import de.claved.origin.bungee.Origin;
import de.claved.origin.utils.objects.origin.OriginReport;
import de.claved.origin.utils.session.Session;
import de.claved.origin.utils.session.query.QueryStatement;
import de.claved.origin.utils.session.query.UpdateStatement;
import lombok.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class ReportManager {

    @Getter
    private static ReportManager instance;

    private final UpdateStatement create;
    private final UpdateStatement insert;
    private final QueryStatement exists;

    private final QueryStatement getReports;
    private final QueryStatement getAllReports;

    public ReportManager() {
        instance = this;
        Session session = Origin.getInstance().getSession();

        create = session.prepareUpdateStatement("CREATE TABLE IF NOT EXISTS reports (id VARCHAR(7), reason VARCHAR(100), target VARCHAR(36), executor VARCHAR(36), server VARCHAR(100), date LONG, state VARCHAR(20))");
        insert = session.prepareUpdateStatement("INSERT INTO reports (id, reason, target, executor, server, date, state) VALUES (?, ?, ?, ?, ?, ?, ?)");
        exists = session.prepareQueryStatement("SELECT target FROM reports WHERE target = ?");

        getReports = session.prepareQueryStatement("SELECT * FROM reports WHERE target = ? AND state = ?");
        getAllReports = session.prepareQueryStatement("SELECT * FROM reports WHERE state = ?");

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

    public boolean exists(UUID uuid) {
        try {
            ResultSet resultSet = exists.execute(uuid);
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public OriginReport executeReport(OriginReport.ReportType type, UUID target, UUID executor, String server, OriginReport.ReportState status) {
        try {
            String id = "#" + (new Random().nextInt(900000) + 100000);
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            insert.execute(id, type.name(), target, executor, server, timestamp, status.name());
            return new OriginReport(id, type, target, executor, server, timestamp, status);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<OriginReport> getReports(UUID uuid, OriginReport.ReportState state) {
        List<OriginReport> reports = new ArrayList<>();
        try {
            ResultSet resultSet = getReports.execute(uuid, state.name());
            while (resultSet.next()) {
                reports.add(new OriginReport(
                        resultSet.getString("id"),
                        OriginReport.ReportType.valueOf(resultSet.getString("reason")),
                        UUID.fromString(resultSet.getString("target")),
                        UUID.fromString(resultSet.getString("executor")),
                        resultSet.getString("server"),
                        Timestamp.valueOf(resultSet.getString("date")),
                        OriginReport.ReportState.valueOf(resultSet.getString("state"))
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reports;
    }

    public List<OriginReport> getReports() {
        List<OriginReport> reports = new ArrayList<>();
        try {
            ResultSet resultSet = getAllReports.execute(OriginReport.ReportState.OPENED.name());
            while (resultSet.next()) {
                reports.add(new OriginReport(
                        resultSet.getString("id"),
                        OriginReport.ReportType.valueOf(resultSet.getString("reason")),
                        UUID.fromString(resultSet.getString("target")),
                        UUID.fromString(resultSet.getString("executor")),
                        resultSet.getString("server"),
                        Timestamp.valueOf(resultSet.getString("date")),
                        OriginReport.ReportState.valueOf(resultSet.getString("state"))
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reports;
    }
}
