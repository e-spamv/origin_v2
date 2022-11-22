package de.claved.origin.spigot.api.scoreboard;

import de.claved.cloud.CloudAPI;
import de.claved.origin.spigot.api.OriginPlayer;
import de.claved.origin.spigot.api.manager.ClanManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Set;

@Getter
public class Scoreboard {

    private final OriginPlayer player;
    private final org.bukkit.scoreboard.Scoreboard bukkitScoreboard;

    private final ScoreboardObjective objective;

    private final HashMap<String, Team> teams = new HashMap<>();

    public Scoreboard(OriginPlayer player) {
        this.player = player;
        this.bukkitScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.objective = new ScoreboardObjective(bukkitScoreboard, player, "§6Claved §8┃ §7" + CloudAPI.getInstance().getLocalServer().getName());
        player.getBukkitPlayer().setScoreboard(bukkitScoreboard);
    }

    public Team getScoreboardTeam(String registration) {
        return teams.get(registration);
    }

    public void registerScoreboardTeam(String registration, String prefix, String suffix, Set<String> entries) {
        if (registration.length() > 16) registration = registration.substring(0, 16);
        if (prefix != null && prefix.length() > 16) prefix = prefix.substring(0, 16);
        if (suffix != null && suffix.length() > 16) suffix = suffix.substring(0, 16);

        Team team = teams.get(registration);
        if (team == null) {
            team = bukkitScoreboard.registerNewTeam(registration);
            team.setCanSeeFriendlyInvisibles(true);
            teams.put(registration, team);
        }
        team.setPrefix(prefix);
        team.setSuffix(suffix);
        entries.forEach(team::addEntry);
    }

    public void registerPlayer(OriginPlayer player) {
        String registration = player.getNick() != null && getPlayer() != player ? player.getRank().getId() + player.getNick() : player.getRealRank().getId() + player.getName();
        String prefix = player.getNick() != null && getPlayer() != player ? player.getRank().getPrefix() : player.getRealRank().getPrefix();
        String suffix = player.getNick() != null && getPlayer() != player ? "" : (ClanManager.getInstance().getClanByUuid(player.getUniqueId()) == null ? "" : " §8[" + ClanManager.getInstance().getClanByUuid(player.getUniqueId()).getColor() + ClanManager.getInstance().getClanByUuid(player.getUniqueId()).getTag() + "§8]");
        String name = player.getNick() != null && getPlayer() != player ? player.getNick() : player.getName();

        if (registration.length() > 16) registration = registration.substring(0, 16);
        if (prefix != null && prefix.length() > 16) prefix = prefix.substring(0, 16);
        if (suffix != null && suffix.length() > 16) suffix = suffix.substring(0, 16);

        Team team = teams.get(registration);
        if (team == null) {
            team = bukkitScoreboard.registerNewTeam(registration);
            team.setCanSeeFriendlyInvisibles(true);
            teams.put(registration, team);
        }
        team.setPrefix(prefix);
        team.setSuffix(suffix);
        team.addEntry(name);
    }

    public void unregisterPlayer(OriginPlayer player) {
        String registration = player.getNick() != null && getPlayer() != player ? player.getRank().getId() + player.getNick() : player.getRealRank().getId() + player.getName();
        Team team = teams.get(registration);
        if (team != null) {
            team.unregister();
            teams.remove(registration);
        }
    }
}
