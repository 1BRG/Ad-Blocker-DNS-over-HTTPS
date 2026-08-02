package org.example.provider;

import java.util.Set;

public interface BlocklistProvider {
    Set<String> loadBlockedDomains();
}
