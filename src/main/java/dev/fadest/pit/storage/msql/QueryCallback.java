package dev.fadest.pit.storage.msql;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;

@FunctionalInterface
public interface QueryCallback {

    void accept(CachedRowSet set) throws SQLException;
}