package dev.fadest.pit.storage.msql;

import java.sql.SQLException;

@FunctionalInterface
public interface ExceptionCallback {

    void accept(SQLException ex);
}
