package org.example.core;
import java.net.InetAddress;

public record DnsRecord(InetAddress ipAddress, RecordType type, int ttl, long createdAt) {
    public enum RecordType
    {
        A,
        AAAA;
    }
    public DnsRecord(InetAddress ipAddress, RecordType type, int ttl)
    {
        this(ipAddress, type, validateTtl(ttl), System.currentTimeMillis());
    }

    private static int validateTtl(int ttl) {
        if (ttl < 0){
            throw new IllegalArgumentException("TTL should not be less than 0");
        }
        return ttl;
    }

    public boolean isExpired()
    {
        return System.currentTimeMillis() - createdAt >= ttl * 1000L;
    }
}
