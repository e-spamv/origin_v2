package de.claved.origin.utils.objects.origin;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Getter
public class OriginPunishment {

    private final String id;
    private final String address;

    private final UUID target;
    private final UUID executor;

    private final Timestamp since;
    @Setter
    private Timestamp until;

    private final PunishmentType type;
    private final PunishmentReason reason;

    public OriginPunishment(String id, String address, UUID target, UUID executor, Timestamp since, Timestamp until, PunishmentType type, PunishmentReason reason) {
        this.id = id;
        this.address = address;
        this.target = target;
        this.executor = executor;
        this.since = since;
        this.until = until;
        this.type = type;
        this.reason = reason;
    }

    public String getSinceAsDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return simpleDateFormat.format(new Date(since.getTime()));
    }

    public String getUntilAsDate() {
        if (until == null) {
            return "§4PERMANENT";
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return simpleDateFormat.format(new Date(until.getTime()));
    }

    public boolean isActive() {
        return until == null || System.currentTimeMillis() < until.getTime();
    }

    public enum PunishmentType {
        BAN, MUTE
    }

    @Getter
    public enum PunishmentReason {

        HACKING(30, true, PunishmentType.BAN),
        TEAMING(3, false, PunishmentType.BAN),
        TROLLING(3, false, PunishmentType.BAN),
        BUGUSING(3, false, PunishmentType.BAN),
        COMBATLOG(3, false, PunishmentType.BAN),
        SECURITY_BAN(null, false, PunishmentType.BAN),
        BEHAVIOUR(3, true, PunishmentType.MUTE);

        Integer days;
        Boolean dynamic;
        PunishmentType type;

        PunishmentReason(Integer days, Boolean dynamic, PunishmentType type) {
            this.days = days;
            this.dynamic = dynamic;
            this.type = type;
        }
    }
}