package org.example;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class BlackList {
    private final Set<String> blockedDomanin = ConcurrentHashMap.newKeySet();
    private final DoHCache dohCache = new DoHCache();
}
