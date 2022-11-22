package de.claved.origin.utils.objects.origin;

import lombok.Getter;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Getter
public class OriginReport {

    private final String id;
    private final ReportType type;

    private final UUID target;
    private final UUID executor;

    private final String server;

    private final Timestamp date;

    private final ReportState state;

    public OriginReport(String id, ReportType type, UUID target, UUID executor, String server, Timestamp date, ReportState state) {
        this.id = id;
        this.type = type;

        this.target = target;
        this.executor = executor;

        this.server = server;

        this.date = date;

        this.state = state;
    }

    public String getFormattedDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return simpleDateFormat.format(new Date(date.getTime()));
    }

    public enum ReportType {
        HACKING, TEAMING, TROLLING, BUGUSING, COMBATLOG
    }

    public enum ReportState {
        OPENED, ACCEPTED, CLOSED
    }
}
