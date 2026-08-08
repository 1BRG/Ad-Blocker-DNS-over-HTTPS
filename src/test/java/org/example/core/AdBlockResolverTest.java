package org.example.core;

import org.example.cache.DnsCache;
import org.example.core.AdBlockResolver;
import org.example.core.DnsRecord;
import org.example.filter.DomainFilter;
import org.example.upstream.UpstreamDnsClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.net.InetAddress;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdBlockResolverTest {

    private DomainFilter domainFilter;
    private DnsCache dnsCache;
    private UpstreamDnsClient upstreamDnsClient;
    private AdBlockResolver adBlockResolver;

    @BeforeEach
    void setUp() {
        domainFilter = mock(DomainFilter.class);
        dnsCache = mock(DnsCache.class);
        upstreamDnsClient = mock(UpstreamDnsClient.class);

        adBlockResolver = new AdBlockResolver(domainFilter, dnsCache, upstreamDnsClient);
    }

    @Test
    void resolveDomain_WhenDomainIsBlocked_ShouldReturnSinkholeIp() throws Exception {

        String domain = "ads.com";
        DnsRecord.RecordType type = DnsRecord.RecordType.A;
        when(domainFilter.isBlocked(domain)).thenReturn(true);

        List<DnsRecord> result = adBlockResolver.resolveDomain(domain, type);

        assertEquals(1, result.size());
        assertEquals("0.0.0.0", result.getFirst().ipAddress().getHostAddress());

        verifyNoInteractions(dnsCache);
        verifyNoInteractions(upstreamDnsClient);
    }

    @Test
    void resolveDomain_WhenDomainInCache_ShouldReturnCachedRecord() throws Exception {
        String domain = "google.com";
        DnsRecord.RecordType type = DnsRecord.RecordType.A;

        List<DnsRecord> cachedData = List.of(new DnsRecord(InetAddress.getByName("8.8.8.8"), type, 300));

        when(domainFilter.isBlocked(domain)).thenReturn(false);
        when(dnsCache.getRecords(domain, type)).thenReturn(cachedData);

        List<DnsRecord> result = adBlockResolver.resolveDomain(domain, type);

        assertEquals("8.8.8.8", result.getFirst().ipAddress().getHostAddress());
        verifyNoInteractions(upstreamDnsClient);
    }

    @Test
    void resolveDomain_WhenDomainNotBlockedAndNotInCache_ShouldFetchAndCache() throws Exception {
        String domain = "youtube.com";
        DnsRecord.RecordType type = DnsRecord.RecordType.AAAA;

        List<DnsRecord> upstreamResponse = List.of(new DnsRecord(InetAddress.getByName("2001:db8::1"), type, 300));

        when(domainFilter.isBlocked(domain)).thenReturn(false);
        when(dnsCache.getRecords(domain, type)).thenReturn(null);
        when(upstreamDnsClient.fetch(domain, type)).thenReturn(upstreamResponse);

        List<DnsRecord> result = adBlockResolver.resolveDomain(domain, type);

        assertEquals(1, result.size());
        assertEquals("2001:db8:0:0:0:0:0:1", result.getFirst().ipAddress().getHostAddress());

        verify(dnsCache).putRecords(domain, type, upstreamResponse);
    }
}