package de.claved.origin.spigot.api.manager;

import de.claved.origin.spigot.Origin;
import de.claved.origin.spigot.api.OriginPlayer;
import de.claved.origin.utils.enums.Language;
import de.claved.origin.utils.session.Session;
import de.claved.origin.utils.session.query.QueryStatement;
import de.claved.origin.utils.session.query.UpdateStatement;
import de.claved.origin.spigot.api.events.LanguageUpdateEvent;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class LanguageManager {

    @Getter
    private static LanguageManager instance;

    private final QueryStatement getLanguage;
    private final UpdateStatement setLanguage;

    public LanguageManager() {
        instance = this;
        Session session = Origin.getInstance().getSession();

        getLanguage = session.prepareQueryStatement("SELECT language FROM players WHERE uuid = ?");
        setLanguage = session.prepareUpdateStatement("UPDATE players SET language = ? WHERE uuid = ?");
    }

    public void disable() {
        instance = this;
    }

    public Language getLanguage(UUID uuid) {
        try {
            ResultSet resultSet = getLanguage.execute(uuid);
            if (resultSet.next()) {
                return Language.valueOf(resultSet.getString("language"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Language.ENGLISH;
    }

    public void setLanguage(UUID uuid, Language language) {
        try {
            setLanguage.execute(language.name(), uuid);

            OriginPlayer player = OriginManager.getInstance().getPlayer(uuid);
            if (player.isOnline()) {
                OriginManager.getInstance().sendPluginChannel(player, "updateLanguage");
                LanguageUpdateEvent languageUpdateEvent = new LanguageUpdateEvent(player);
                Bukkit.getPluginManager().callEvent(languageUpdateEvent);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
