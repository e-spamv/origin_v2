package de.claved.origin.bungee.api.manager;

import com.google.common.collect.Lists;
import de.claved.origin.bungee.Origin;
import de.claved.origin.bungee.api.OriginPlayer;
import de.claved.origin.utils.enums.Language;
import de.claved.origin.utils.session.Session;
import de.claved.origin.utils.session.query.QueryStatement;
import de.claved.origin.utils.session.query.UpdateStatement;
import lombok.Getter;

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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        OriginPlayer player = OriginManager.getInstance().getPlayer(uuid);
        if (player != null) {
            OriginManager.getInstance().sendPluginChannel(player, "updateLanguage");
        }
    }

    public Language fromIp(String ipAddress) {
        String countryCode = CountryManager.getInstance().getCountryCode(ipAddress);
        if (Lists.newArrayList("de", "at", "ch", "lu").contains(countryCode.toLowerCase())) {
            return Language.GERMAN;
        } else {
            return Language.ENGLISH;
        }
    }
}
