package cache;

import org.example.cache.DnsCache;
import org.example.core.DnsRecord;
import org.example.logging.CacheLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DnsCacheTest {

    private String domain = "google.com";

    @Mock
    private CacheLogger logger;

    @InjectMocks
    private DnsCache dohCache;

    @Test
    void getRecords_NotCachedDomain_ShouldReturnNull()
    {
        List<DnsRecord> result = dohCache.getRecords(domain, DnsRecord.RecordType.A);

        assertNull(result);
        String expectedLog = String.format("[MISS] %s (%s).", domain, DnsRecord.RecordType.A);
        verify(logger, times(1)).log(expectedLog);
    }

    @Test
    void getRecords_DomainAlreadyInCacheAndNotExpired_ShouldReturnDnsRecords() throws UnknownHostException {
        List<DnsRecord> records = List.of(new DnsRecord(InetAddress.getByName("8.8.8.8"), DnsRecord.RecordType.A, 5), new DnsRecord(InetAddress.getByName("9.9.9.9"), DnsRecord.RecordType.A, 5));
        dohCache.putRecords(domain, DnsRecord.RecordType.A, records);

        List<DnsRecord> result = dohCache.getRecords(domain, DnsRecord.RecordType.A);

        assertEquals(records, result);
        String expectedLog = String.format("[HIT] %s (%s). Returning %d records.", domain, DnsRecord.RecordType.A, records.size());
    }

    @Test
    void getRecords_DomainIsAlreadyInCacheButExpired_ShouldReturnNull() throws UnknownHostException {
        List<DnsRecord> records = List.of(new DnsRecord(InetAddress.getByName("8.8.8.8"), DnsRecord.RecordType.A, 0), new DnsRecord(InetAddress.getByName("9.9.9.9"), DnsRecord.RecordType.AAAA, 0));
        dohCache.putRecords(domain, DnsRecord.RecordType.A, records);

        List<DnsRecord> result = dohCache.getRecords(domain, DnsRecord.RecordType.A);

        assertNull(result);
        String expectedLog = String.format("[EXPIRED] %s (%s).", domain, DnsRecord.RecordType.A);
    }

}
