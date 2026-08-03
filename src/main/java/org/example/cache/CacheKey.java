package org.example.cache;

import org.example.core.DnsRecord;

public record CacheKey(String domain, DnsRecord.RecordType type) {
}
