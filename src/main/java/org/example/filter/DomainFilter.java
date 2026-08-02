package org.example.filter;

import org.example.provider.BlocklistProvider;

import java.util.Set;

public class DomainFilter {
    private BlocklistProvider blocklistProvider;
    private final Set<String> blockList;

    public DomainFilter(BlocklistProvider blocklistProvider)
    {
        this.blockList = blocklistProvider.loadBlockedDomains();
        this.blocklistProvider = blocklistProvider;
    }

    public boolean isBlocked(String domain)
    {
        return blockList.contains(domain);
    }

}
