package org.example;

import org.example.cache.DnsCache;
import org.example.core.AdBlockResolver;
import org.example.core.DnsRecord;
import org.example.filter.DomainFilter;
import org.example.logging.CacheLogger;
import org.example.provider.BlocklistProvider;
import org.example.provider.file.FileProvider;
import org.example.server.DohServer;
import org.example.upstream.UpstreamDnsClient;

import java.net.UnknownHostException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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


        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "BlockList updater");
            thread.setDaemon(true);
            return thread;
        });

        scheduler.scheduleAtFixedRate(domainFilter::reloadBlockList, 24, 24, TimeUnit.HOURS);

        DohServer server = new DohServer(adBlockResolver);
        server.start(8080);
    }
}