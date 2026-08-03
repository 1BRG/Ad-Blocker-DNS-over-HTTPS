package org.example.upstream;

import org.example.core.DnsRecord;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class UpstreamDnsClient {
    public List<DnsRecord> fetch(String domain, DnsRecord.RecordType type) throws UnknownHostException {
        List<DnsRecord> results = new ArrayList<>();

        try {
            InetAddress[] addresses = InetAddress.getAllByName(domain);
            for (InetAddress ip : addresses) {
                if (type == DnsRecord.RecordType.A && ip.getAddress().length == 4) {
                    results.add(new DnsRecord(ip, type, 300));
                } else if (type == DnsRecord.RecordType.AAAA && ip.getAddress().length == 16) {
                    results.add(new DnsRecord(ip, type, 300));
                }
            }
        } catch (Exception e) {
            throw new UnknownHostException("IP for " + domain + " not found");
        }

        return results;
    }
}
