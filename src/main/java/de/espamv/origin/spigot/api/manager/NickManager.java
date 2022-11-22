package de.claved.origin.spigot.api.manager;

import com.mojang.authlib.GameProfile;
import de.claved.origin.spigot.Origin;
import de.claved.origin.spigot.api.OriginPlayer;
import de.claved.origin.spigot.utils.UpdatePlayer;
import de.claved.origin.utils.GameProfileBuilder;
import de.claved.origin.utils.enums.Rank;
import de.claved.origin.utils.session.Session;
import de.claved.origin.utils.session.query.QueryStatement;
import de.claved.origin.utils.session.query.UpdateStatement;
import de.claved.cloud.CloudAPI;
import de.claved.origin.spigot.api.events.NickNameUpdateEvent;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class NickManager implements Listener {

    @Getter
    private static NickManager instance;

    @Getter
    private final String prefix = "§8┃ §aNick §8× §r";

    private final QueryStatement getState;
    private final UpdateStatement setState;

    private final FileConfiguration fileConfiguration;

    private final ArrayList<String> nicks = new ArrayList<>();

    private final HashMap<UUID, String> playerNickNames = new HashMap<>();
    private final HashMap<String, GameProfile> gameProfiles = new HashMap<>();

    public NickManager() {
        instance = this;
        Bukkit.getPluginManager().registerEvents(this, Origin.getInstance());
        Session session = Origin.getInstance().getSession();

        getState = session.prepareQueryStatement("SELECT nickState FROM players WHERE uuid = ?");
        setState = session.prepareUpdateStatement("UPDATE players SET nickState = ? WHERE uuid = ?");

        File file = new File("plugins/Origin", "nicknames.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        fileConfiguration = YamlConfiguration.loadConfiguration(file);

        nicks.addAll(fileConfiguration.getStringList("Nicklist"));
        nicks.forEach(nicknames -> {
            GameProfile loadedProfile = GameProfileBuilder.fromString(fileConfiguration.getString(nicknames));
            gameProfiles.put(loadedProfile.getName(), loadedProfile);
        });
    }

    public void disable() {
        instance = null;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        OriginPlayer player = OriginManager.getInstance().getPlayer(event.getPlayer());

        if (!CloudAPI.getInstance().getLocalServer().getName().toLowerCase().contains("lobby")) {
            if (player.isNicked() && player.hasPriorityAccess(Rank.PREMIUMPLUS.getPriority())) {
                Bukkit.getScheduler().runTaskAsynchronously(Origin.getInstance(), () -> nickPlayer(player));
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        OriginPlayer player = OriginManager.getInstance().getPlayer(event.getPlayer());

        if (player.getNick() != null) {
            playerNickNames.remove(player.getUniqueId());
        }
    }

    public HashMap<UUID, String> getCurrentNicks() {
        return playerNickNames;
    }

    public String getNickName(UUID uuid) {
        if (playerNickNames.get(uuid) != null) {
            return playerNickNames.get(uuid);
        }
        return null;
    }

    public GameProfile getNickGameProfile(String name) {
        return gameProfiles.get(name);
    }

    public boolean isNickNameUsed(String name) {
        return playerNickNames.containsValue(name);
    }

    public void nickPlayer(OriginPlayer player) {
        String name = getRandomNickName();
        playerNickNames.put(player.getUniqueId(), name);

        UpdatePlayer.update(player, false);

        NickNameUpdateEvent event = new NickNameUpdateEvent(player);
        Bukkit.getPluginManager().callEvent(event);

        player.sendMessage(
                prefix + "§7Dein aktueller Nickname ist nun§8: §e" + name,
                prefix + "§7Your current nickname is now§8: §e" + name
        );
    }

    public void unnickPlayer(OriginPlayer player) {
        playerNickNames.remove(player.getUniqueId());

        UpdatePlayer.update(player, false);

        NickNameUpdateEvent event = new NickNameUpdateEvent(player);
        Bukkit.getPluginManager().callEvent(event);

        player.sendMessage(
                prefix + "§7Dein Nickname wurde §cenfernt",
                prefix + "§7Your nickname was §cremoved"
        );
    }

    public String getRandomNickName() {
        List<String> filtered = nicks.stream().filter(name -> !isNickNameUsed(name)).collect(Collectors.toList());
        return filtered.get(new Random().nextInt(filtered.size()));
    }

    public boolean getNickState(UUID uuid) {
        try {
            ResultSet resultSet = getState.execute(uuid);
            if (resultSet.next()) {
                return resultSet.getBoolean("nickState");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void setNickState(UUID uuid, boolean state) {
        try {
            setState.execute(state ? 1 : 0, uuid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
