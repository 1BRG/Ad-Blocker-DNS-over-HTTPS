package org.example;

import org.example.cache.DoHCache;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class BlockList {
    private final Set<String> blockedDomanin = ConcurrentHashMap.newKeySet();
    private final DoHCache dohCache = new DoHCache();
}
