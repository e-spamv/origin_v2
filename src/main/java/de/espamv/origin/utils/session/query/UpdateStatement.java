package de.claved.origin.utils.session.query;

import java.sql.SQLException;

public interface UpdateStatement {
    int execute(Object... args) throws SQLException;
}
