package cache;

import org.example.cache.DoHCache;
import org.example.core.DnsRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class DoHCacheTest {

    private String domain;
    private DoHCache dohCache;
    @BeforeEach
    void setUp()
    {
        domain = "google.com";
        dohCache = new DoHCache();
    }

    @Test
    void getRecords_NotCachedDomain_ShouldReturnNull()
    {
        List<DnsRecord> result = dohCache.getRecords(domain);

        assertNull(result);
    }
    @Test
    void getRecords_DomainAlreadyInCacheAndNotExpired_ShouldReturnDnsRecords() throws UnknownHostException {
        List<DnsRecord> records = List.of(new DnsRecord(InetAddress.getByName("8.8.8.8"), DnsRecord.RecordType.A, 5), new DnsRecord(InetAddress.getByName("2001:4860:4860::8888"), DnsRecord.RecordType.AAAA, 5));
        dohCache.putRecords(domain, records);

        List<DnsRecord> result = dohCache.getRecords(domain);

        assertEquals(records, result);
    }

    @Test
    void getRecords_DomainIsAlreadyInCacheButExpired_ShouldReturnNull() throws UnknownHostException {
        List<DnsRecord> records = List.of(new DnsRecord(InetAddress.getByName("8.8.8.8"), DnsRecord.RecordType.A, 0), new DnsRecord(InetAddress.getByName("2001:4860:4860::8888"), DnsRecord.RecordType.AAAA, 0));
        dohCache.putRecords(domain, records);

        List<DnsRecord> result = dohCache.getRecords(domain);

        assertNull(result);
    }

}
