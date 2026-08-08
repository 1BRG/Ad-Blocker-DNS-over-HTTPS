package org.example.core;

import org.example.core.DnsRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class DnsRecordTest {

    @Test
    void constructor_NonNegativeTtl_ShouldCreateDnsRecordObject() throws UnknownHostException {
        DnsRecord dnsRecord = new DnsRecord(InetAddress.getByName("8.8.8.8"), DnsRecord.RecordType.A, 0);

        assertNotNull(dnsRecord);
    }

    @Test
    void constructor_NegativeTtl_ShouldThrowIl() throws UnknownHostException {
        assertThrows(IllegalArgumentException.class, () -> new DnsRecord(InetAddress.getByName("8.8.8.8"), DnsRecord.RecordType.A, -1));
    }
}
