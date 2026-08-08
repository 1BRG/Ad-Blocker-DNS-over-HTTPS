package org.example.filter;

import org.example.core.DnsRecord;
import org.example.provider.BlocklistProvider;

import java.util.List;
import java.util.Set;

public class DomainFilter {
    private final BlocklistProvider blocklistProvider;
    volatile private Set<String> blockList;

    public DomainFilter(BlocklistProvider blocklistProvider)
    {
        this.blockList = blocklistProvider.loadBlockedDomains();
        this.blocklistProvider = blocklistProvider;
    }

    public boolean isBlocked(String domain)
    {
        return blockList.contains(domain);
    }

    public void reloadBlockList()
    {
        try {
            Set<String> newBlockList = blocklistProvider.loadBlockedDomains();

            this.blockList = newBlockList;
        }
        catch (Exception ignored)
        {
        }

    }

}
