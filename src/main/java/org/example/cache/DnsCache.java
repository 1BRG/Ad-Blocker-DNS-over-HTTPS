package org.example.cache;

import org.example.core.DnsRecord;
import org.example.logging.CacheLogger;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class DnsCache {
    private final Map<CacheKey, List<DnsRecord>> cache = new ConcurrentHashMap<>();
    CacheLogger logger;
    public DnsCache(CacheLogger logger)
    {
        this.logger = logger;
    }
    public List<DnsRecord> getRecords(String domain, DnsRecord.RecordType type)
    {
        CacheKey cacheKey = new CacheKey(domain, type);
        List<DnsRecord> records = cache.get(cacheKey);
        if(records != null)
        {
            if(records.getFirst().isExpired())
            {
                logger.log(String.format("[EXPIRED] %s (%s).", domain, type));
                cache.remove(cacheKey);
                return null;
            }
            logger.log(String.format("[HIT] %s (%s). Returning %d records.", domain, type, records.size()));
            return records;
        }
        logger.log(String.format("[MISS] %s (%s).", domain, type));
        return null;
    }

    public void putRecords(String domain, DnsRecord.RecordType type, List<DnsRecord> records) {
        CacheKey cacheKey = new CacheKey(domain, type);
        logger.log(String.format("[PUT] %s (%s) with %d records.", domain, type, records.size()));
        cache.put(cacheKey, records);
    }
}
