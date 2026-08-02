package org.example.provider.db;

import org.example.config.DBManager;
import org.example.provider.BlocklistProvider;

import java.util.HashSet;
import java.util.Set;

public class DataBaseProvider implements BlocklistProvider {
    private final DBManager dbManager;

    public  DataBaseProvider(DBManager dbManager)
    {
        this.dbManager = dbManager;
    }

    @Override
    public Set<String> loadBlockedDomains()
    {
        Set<String> blockedDomains = new HashSet<>();
        // TODO query for domains loading when database is available
        return blockedDomains;
    }
}
