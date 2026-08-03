package org.example;

import org.example.cache.DnsCache;
import org.example.core.AdBlockResolver;
import org.example.core.DnsRecord;
import org.example.filter.DomainFilter;
import org.example.logging.CacheLogger;
import org.example.provider.BlocklistProvider;
import org.example.provider.file.FileProvider;
import org.example.upstream.UpstreamDnsClient;

import java.net.UnknownHostException;
import java.nio.file.Path;
import java.util.Arrays;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        Path blockListPath = Path.of("BlockedDomains/blocked_domains.txt");
        BlocklistProvider blocklistProvider = new FileProvider(blockListPath);

        Path logsPath = Path.of("Logs/logs.txt");
        CacheLogger cacheLogger = new CacheLogger(logsPath, 100);

        DomainFilter domainFilter = new DomainFilter(blocklistProvider);
        DnsCache dnsCache = new DnsCache(cacheLogger);
        UpstreamDnsClient upstreamDnsClient = new UpstreamDnsClient();
        AdBlockResolver adBlockResolver = new AdBlockResolver(domainFilter, dnsCache, upstreamDnsClient);
        try {
            adBlockResolver.resolveDomain("google.com", DnsRecord.RecordType.A);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}