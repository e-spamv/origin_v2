package de.claved.origin.utils.objects.clans;

import de.claved.origin.bungee.api.manager.ClanManager;
import de.claved.origin.utils.enums.ClanRank;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

@Getter
public class Clan {

    @Setter
    private UUID leader;

    @Setter
    private String name;
    @Setter
    private String tag;
    @Setter
    private String description;
    @Setter
    private ChatColor color;

    @Setter
    private int elo;

    private final Timestamp creation;

    public Clan(UUID leader, String name, String tag, String description, String color, int elo, Timestamp creation) {
        this.leader = leader;
        this.name = name;
        this.tag = tag;
        this.description = description;
        this.color = ChatColor.valueOf(color);
        this.elo = elo;
        this.creation = creation;
    }

    public String getCreationAsDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return simpleDateFormat.format(new Date(creation.getTime()));
    }

    public HashSet<UUID> getModerators() {
        HashSet<UUID> set = new HashSet<>();
        for (ClanSettings setting : ClanManager.getInstance().getSettings()) {
            if (Objects.equals(setting.getClanName(), name)) {
                if (setting.getClanRank().equals(ClanRank.MODERATOR)) {
                    set.add(setting.getUuid());
                }
            }
        }
        return set;
    }

    public HashSet<UUID> getMembers() {
        HashSet<UUID> set = new HashSet<>();
        for (ClanSettings setting : ClanManager.getInstance().getSettings()) {
            if (Objects.equals(setting.getClanName(), name)) {
                if (setting.getClanRank().equals(ClanRank.MEMBER)) {
                    set.add(setting.getUuid());
                }
            }
        }
        return set;
    }

    public HashSet<UUID> getAllMembers() {
        HashSet<UUID> set = new HashSet<>();
        set.add(leader);
        set.addAll(getMembers());
        set.addAll(getModerators());
        return set;
    }
}
