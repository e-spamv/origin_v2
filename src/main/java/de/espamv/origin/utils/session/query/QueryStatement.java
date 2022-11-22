package de.claved.origin.utils.session.query;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface QueryStatement {
    ResultSet execute(Object... args) throws SQLException;
}
