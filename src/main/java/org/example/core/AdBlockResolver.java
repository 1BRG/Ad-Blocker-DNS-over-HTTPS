package org.example.core;

import org.example.cache.DnsCache;
import org.example.filter.DomainFilter;
import org.example.upstream.UpstreamDnsClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class AdBlockResolver {
    private final DnsCache dnsCache;
    private final DomainFilter domainFilter;
    private final UpstreamDnsClient upstreamDnsClient;
    public AdBlockResolver(DomainFilter domainFilter, DnsCache dnsCache, UpstreamDnsClient upstreamDnsClient) {
        this.domainFilter = domainFilter;
        this.dnsCache = dnsCache;
        this.upstreamDnsClient = upstreamDnsClient;
    }

    public List<DnsRecord> resolveDomain(String domain, DnsRecord.RecordType type) throws UnknownHostException, RuntimeException {
        if (domainFilter.isBlocked(domain)) {
            return generateSinkhole(type);
        }
        List<DnsRecord> records = dnsCache.getRecords(domain, type);
        if (records != null) {
            return records;
        }

        records = upstreamDnsClient.fetch(domain, type);
        dnsCache.putRecords(domain, type, records);
        return records;
    }

    private List<DnsRecord> generateSinkhole(DnsRecord.RecordType type) throws UnknownHostException {
        if (type == DnsRecord.RecordType.A)
            return List.of(new DnsRecord(InetAddress.getByName("0.0.0.0"), DnsRecord.RecordType.A, 3600));
        else return List.of(new DnsRecord(InetAddress.getByName("::"), DnsRecord.RecordType.AAAA, 3600));
    }
}