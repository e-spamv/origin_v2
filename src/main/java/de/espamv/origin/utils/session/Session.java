package de.claved.origin.utils.session;

import de.claved.origin.utils.session.query.QueryStatement;
import de.claved.origin.utils.session.query.UpdateStatement;
import lombok.Getter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Session {

    @Getter
    private Connection connection;

    private final String host;
    private final Integer port;
    private final String database;
    private final String user;
    private final String password;
    private final Boolean autoReconnet;

    public Session(String host, int port, String database, String user, String password, boolean autoReconnet) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.user = user;
        this.password = password;
        this.autoReconnet = autoReconnet;
    }

    public void disable() {
        close();
    }

    public void connect() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=" + autoReconnet, user, password);
            System.out.println("[Origin] The connection to the database was opened.");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("[Origin] Error while connecting to the database.");
        }
    }

    public void close() {
        try {
            connection.close();
            System.out.println("[Origin] The connection to the database was closed.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("[Origin] Error while disconnecting from the database.");
        }
    }

    public QueryStatement prepareQueryStatement(String query) {
        return args -> {
            Statement statement = connection.createStatement();
            String queryString = query;
            for (Object object : args) {
                queryString = queryString.replaceFirst("\\?", "'" + object.toString() + "'");
            }
            return statement.executeQuery(queryString);
        };
    }

    public UpdateStatement prepareUpdateStatement(String query) {
        return args -> {
            Statement statement = connection.createStatement();
            String queryString = query;
            for (Object object : args) {
                queryString = queryString.replaceFirst("\\?", "'" + object.toString() + "'");
            }
            return statement.executeUpdate(queryString);
        };
    }
}
