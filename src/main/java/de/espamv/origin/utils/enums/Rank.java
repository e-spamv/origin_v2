package de.claved.origin.utils.enums;

import lombok.Getter;
import net.md_5.bungee.api.ChatColor;

@Getter
public enum Rank {

    ADMINISTRATOR("a0_admin", 120, "Admin", "§4Admin §8┃ §7", ChatColor.DARK_RED),
    YOUTUBERPLUS("b1_ytplus", 110, "YouTuber+", "§4YT+ §8┃ §7", ChatColor.DARK_RED),
    DEVELOPER("c2_developer", 100, "Developer", "§cDev §8┃ §7", ChatColor.RED),
    SRMODERATOR("d3_srmoderator", 90, "SrModerator", "§cSrMod §8┃ §7", ChatColor.RED),
    MODERATOR("e4_moderator", 80, "Moderator", "§cMod §8┃ §7", ChatColor.RED),
    SUPPORTER("f5_supporter", 70, "Supporter", "§cSup §8┃ §7", ChatColor.RED),
    BUILDER("g6_builder", 60, "Builder", "§cBuilder §8┃ §7", ChatColor.RED),
    CHAMP("h7_champ", 50, "Champ", "§bChampion §8┃ §7", ChatColor.AQUA),
    CLAVED("", 50, "Claved", "§6Claved §8┃ §7", ChatColor.GOLD),
    YOUTUBER("h6_youtuber", 40, "YouTuber", "§5YT §8┃ §7", ChatColor.DARK_PURPLE),
    PREMIUMPLUS("i7_premiumplus", 30, "PrimePlus", "§dPrime+ §8┃ §7", ChatColor.LIGHT_PURPLE),
    PREMIUM("j8_premium", 20, "Prime", "§6Prime §8┃ §7", ChatColor.GOLD),
    PLAYER("k9_player", 10, "Player", "§7", ChatColor.GRAY);

    final String id;
    final String name;
    final String prefix;

    final int priority;

    final ChatColor color;

    Rank(String id, int priority, String name, String prefix, ChatColor color) {
        this.id = id;
        this.priority = priority;
        this.name = name;
        this.prefix = prefix;
        this.color = color;
    }
}