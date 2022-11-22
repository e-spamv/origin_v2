package de.claved.origin.spigot.api.manager;

import de.claved.origin.spigot.Origin;
import de.claved.origin.utils.session.Session;
import de.claved.origin.utils.session.query.QueryStatement;
import de.claved.origin.utils.session.query.UpdateStatement;
import lombok.Getter;

import java.net.InetSocketAddress;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;

public class LoginManager {

    @Getter
    private static LoginManager instance;

    private final QueryStatement getAddress;
    private final UpdateStatement setAddress;

    private final QueryStatement getFirstLogin;
    private final UpdateStatement setFirstLogin;

    private final QueryStatement getLastLogin;
    private final UpdateStatement setLastLogin;

    private final QueryStatement getServer;
    private final UpdateStatement setServer;

    public LoginManager() {
        instance = this;
        Session session = Origin.getInstance().getSession();

        getAddress = session.prepareQueryStatement("SELECT address FROM players WHERE uuid = ?");
        setAddress = session.prepareUpdateStatement("UPDATE players SET address = ? WHERE uuid = ?");

        getFirstLogin = session.prepareQueryStatement("SELECT firstLogin FROM players WHERE uuid = ?");
        setFirstLogin = session.prepareUpdateStatement("UPDATE players SET firstLogin = ? WHERE uuid = ?");

        getLastLogin = session.prepareQueryStatement("SELECT lastLogin FROM players WHERE uuid = ?");
        setLastLogin = session.prepareUpdateStatement("UPDATE players SET lastLogin = ? WHERE uuid = ?");

        getServer = session.prepareQueryStatement("SELECT server FROM players WHERE uuid = ?");
        setServer = session.prepareUpdateStatement("UPDATE players SET server = ? WHERE uuid = ?");
    }

    public void disable() {
        instance = this;
    }

    public String getAddress(UUID uuid) {
        try {
            ResultSet resultSet = getAddress.execute(uuid);
            if (resultSet.next()) {
                return resultSet.getString("address");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setAddress(UUID uuid, InetSocketAddress address) {
        try {
            setAddress.execute(address.getAddress().getHostAddress(), uuid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getFirstLogin(UUID uuid) {
        try {
            ResultSet resultSet = getFirstLogin.execute(uuid);
            if (resultSet.next()) {
                return resultSet.getString("firstLogin");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setFirstLogin(UUID uuid, Date date) {
        try {
            setFirstLogin.execute(date, uuid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getLastLogin(UUID uuid) {
        try {
            ResultSet resultSet = getLastLogin.execute(uuid);
            if (resultSet.next()) {
                return resultSet.getString("lastLogin");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setLastLogin(UUID uuid, Date date) {
        try {
            setLastLogin.execute(date, uuid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getServer(UUID uuid) {
        try {
            ResultSet resultSet = getServer.execute(uuid);
            if (resultSet.next()) {
                return resultSet.getString("server");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setServer(UUID uuid, String server) {
        try {
            setServer.execute(server, uuid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}