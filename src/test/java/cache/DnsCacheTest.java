package cache;

import org.example.cache.DnsCache;
import org.example.core.DnsRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class DnsCacheTest {

    private String domain;
    private DnsCache dohCache;
    @BeforeEach
    void setUp()
    {
        domain = "google.com";
        dohCache = new DnsCache();
    }

    @Test
    void getRecords_NotCachedDomain_ShouldReturnNull()
    {
        List<DnsRecord> result = dohCache.getRecords(domain, DnsRecord.RecordType.A);

        assertNull(result);
    }

    @Test
    void getRecords_DomainAlreadyInCacheAndNotExpired_ShouldReturnDnsRecords() throws UnknownHostException {
        List<DnsRecord> records = List.of(new DnsRecord(InetAddress.getByName("8.8.8.8"), DnsRecord.RecordType.A, 5), new DnsRecord(InetAddress.getByName("9.9.9.9"), DnsRecord.RecordType.A, 5));
        dohCache.putRecords(domain, DnsRecord.RecordType.A, records);

        List<DnsRecord> result = dohCache.getRecords(domain, DnsRecord.RecordType.A);

        assertEquals(records, result);
    }

    @Test
    void getRecords_DomainIsAlreadyInCacheButExpired_ShouldReturnNull() throws UnknownHostException {
        List<DnsRecord> records = List.of(new DnsRecord(InetAddress.getByName("8.8.8.8"), DnsRecord.RecordType.A, 0), new DnsRecord(InetAddress.getByName("9.9.9.9"), DnsRecord.RecordType.AAAA, 0));
        dohCache.putRecords(domain, DnsRecord.RecordType.A, records);

        List<DnsRecord> result = dohCache.getRecords(domain, DnsRecord.RecordType.A);

        assertNull(result);
    }

}
