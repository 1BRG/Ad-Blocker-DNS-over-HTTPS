package org.example.cache;

import org.example.core.DnsRecord;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DoHCache {
    private final Map<String, List<DnsRecord>> cache = new ConcurrentHashMap<>();

    public List<DnsRecord> getRecords(String domain)
    {
        List<DnsRecord> records = cache.get(domain);
        if(records != null)
        {
            if(records.getFirst().isExpired())
            {
                cache.remove(domain);
                return null;
            }
            return records;
        }
        return null;
    }

    public void putRecords(String domain, List<DnsRecord> records) {
        cache.put(domain, records);
    }
}
