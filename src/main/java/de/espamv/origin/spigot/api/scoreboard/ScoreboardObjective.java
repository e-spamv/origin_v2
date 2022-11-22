package de.claved.origin.spigot.api.scoreboard;

import de.claved.origin.spigot.api.OriginPlayer;
import lombok.Getter;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.stream.IntStream;

@Getter
public class ScoreboardObjective {

    private final OriginPlayer player;
    private final String title;

    private final Scoreboard scoreboard;
    private final Objective bukkitObjective;

    private final HashMap<Integer, String> scores = new HashMap<>();

    public ScoreboardObjective(Scoreboard scoreboard, OriginPlayer player, String title) {
        this.player = player;
        this.title = title;

        this.scoreboard = scoreboard;
        this.bukkitObjective = scoreboard.registerNewObjective("aaa", "dummy");
    }

    public void setScore(int score, String prefix, String suffix) {
        scores.put(score, prefix + ";" + suffix);
    }

    public void resetObjective() {
        scores.forEach((integer, string) -> {
            scoreboard.resetScores(getScoreEntry(integer));
            scoreboard.getEntryTeam(getScoreEntry(integer)).unregister();
        });
        scores.clear();
    }

    public void setObjective() {
        bukkitObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
        bukkitObjective.setDisplayName(title);

        IntStream.range(0, 16).forEach(i -> {
            if (scores.get(i) != null) {
                Team team = scoreboard.registerNewTeam("#" + i);
                String[] strings = scores.get(i).split(";");

                team.setPrefix(strings[0]);
                team.setSuffix(strings[1]);
                team.addEntry(getScoreEntry(i));

                bukkitObjective.getScore(getScoreEntry(i)).setScore(i);
                player.getBukkitPlayer().setScoreboard(scoreboard);
            }
        });
    }

    private String getScoreEntry(int i) {
        String entry = null;
        if (i <= 9) {
            entry = "§" + i;
        } else {
            switch (i) {
                case 10:
                    entry = "§a";
                    break;
                case 11:
                    entry = "§b";
                    break;
                case 12:
                    entry = "§c";
                    break;
                case 13:
                    entry = "§d";
                    break;
                case 14:
                    entry = "§e";
                    break;
                case 15:
                    entry = "§f";
                    break;
            }
        }
        return entry;
    }

    public void updateScore(int score, String prefix, String suffix) {
        if (player.getBukkitPlayer().getScoreboard() != null && player.getBukkitPlayer().getScoreboard().getObjective(DisplaySlot.SIDEBAR) != null) {
            player.getBukkitPlayer().getScoreboard().getTeam("#" + score).setSuffix(suffix);
            player.getBukkitPlayer().getScoreboard().getTeam("#" + score).setPrefix(prefix);
        }
    }
}