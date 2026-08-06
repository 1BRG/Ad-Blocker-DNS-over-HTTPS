package org.example.upstream;

import org.example.core.DnsRecord;
import org.xbill.DNS.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class UpstreamDnsClient {

    private final Resolver resolver;

    public UpstreamDnsClient() {
        try {
            this.resolver = new SimpleResolver("8.8.8.8");
        } catch (UnknownHostException e) {
            throw new RuntimeException("Host could not be found", e);
        }
    }
    public List<DnsRecord> fetch(String domain, DnsRecord.RecordType type) throws UnknownHostException {
        List<DnsRecord> results = new ArrayList<>();

        try {
            String absoluteDomain = domain.endsWith(".") ? domain : domain + ".";
            Name name = Name.fromString(absoluteDomain);

            int dnsType = (type == DnsRecord.RecordType.AAAA) ? Type.AAAA : Type.A;

            org.xbill.DNS.Record queryRecord = org.xbill.DNS.Record.newRecord(name, dnsType, DClass.IN);
            Message queryMessage = Message.newQuery(queryRecord);

            Message response = resolver.send(queryMessage);

            List<org.xbill.DNS.Record> answers = response.getSection(Section.ANSWER);

            if (answers != null) {
                for (org.xbill.DNS.Record ans : answers) {
                    if (ans.getType() == dnsType) {
                        InetAddress ip = null;

                        if (ans instanceof ARecord) {
                            ip = ((ARecord) ans).getAddress();
                        } else if (ans instanceof AAAARecord) {
                            ip = ((AAAARecord) ans).getAddress();
                        }

                        if (ip != null) {
                            results.add(new DnsRecord(ip, type, (int) ans.getTTL()));
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new UnknownHostException("IP for " + domain + " not found");
        }

        return results;
    }
}
