package org.example.core;

import org.example.cache.DnsCache;
import org.example.filter.DomainFilter;
import org.example.upstream.UpstreamDnsClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdBlockResolver {
    private final DnsCache dnsCache;
    private final DomainFilter domainFilter;
    private final UpstreamDnsClient upstreamDnsClient;

    private static final List<DnsRecord> SINKHOLE_A;
    private static final List<DnsRecord> SINKHOLE_AAAA;

    static {
        try {
            SINKHOLE_A = List.of(new DnsRecord(InetAddress.getByName("0.0.0.0"), DnsRecord.RecordType.A, 3600));
            SINKHOLE_AAAA = List.of(new DnsRecord(InetAddress.getByName("::"), DnsRecord.RecordType.AAAA, 3600));
        } catch (UnknownHostException e) {
            throw new ExceptionInInitializerError("Failed to initialize static sinkhole IPs");
        }
    }
    public AdBlockResolver(DomainFilter domainFilter, DnsCache dnsCache, UpstreamDnsClient upstreamDnsClient) {
        this.domainFilter = domainFilter;
        this.dnsCache = dnsCache;
        this.upstreamDnsClient = upstreamDnsClient;
    }

    public List<DnsRecord> resolveDomain(String domain, DnsRecord.RecordType type) {
        if (domainFilter.isBlocked(domain)) {
            return generateSinkhole(type);
        }
        List<DnsRecord> records = dnsCache.getRecords(domain, type);
        if (records != null) {
            return records;
        }

        try {
            records = upstreamDnsClient.fetch(domain, type);
        } catch (Exception e) {
            return Collections.emptyList();
        }

        dnsCache.putRecords(domain, type, records);
        return records;
    }

    private List<DnsRecord> generateSinkhole(DnsRecord.RecordType type) {
        return (type == DnsRecord.RecordType.A) ? SINKHOLE_A : SINKHOLE_AAAA;
    }
}