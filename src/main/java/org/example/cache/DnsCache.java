package org.example.cache;

import org.example.core.DnsRecord;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DnsCache {
    private final Map<CacheKey, List<DnsRecord>> cache = new ConcurrentHashMap<>();

    public List<DnsRecord> getRecords(String domain, DnsRecord.RecordType type)
    {
        CacheKey cacheKey = new CacheKey(domain, type);
        List<DnsRecord> records = cache.get(cacheKey);
        if(records != null)
        {
            if(records.getFirst().isExpired())
            {
                cache.remove(cacheKey);
                return null;
            }
            return records;
        }
        return null;
    }

    public void putRecords(String domain, DnsRecord.RecordType type, List<DnsRecord> records) {
        CacheKey cacheKey = new CacheKey(domain, type);
        cache.put(cacheKey, records);
    }
}
