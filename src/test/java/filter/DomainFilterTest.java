package filter;

import org.example.filter.DomainFilter;
import org.example.provider.BlocklistProvider;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.net.UnknownHostException;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class DomainFilterTest{

    @Test
    void isBlocked_BlockListIsEmptyAndQueryADomain_ShouldReturnFalse() {
        BlocklistProvider blocklistProvider = Mockito.mock();
        when(blocklistProvider.loadBlockedDomains()).thenReturn(new HashSet<>());

        DomainFilter domainFilter = new DomainFilter(blocklistProvider);

        assertFalse(domainFilter.isBlocked("google.com"));
    }

    @Test
    void isBlocked_DomainIsNotInBlockList_ShouldReturnFalse() {
        BlocklistProvider blocklistProvider = Mockito.mock();
        HashSet<String> blockedDomains = new HashSet<>();
        blockedDomains.add("abc.com");
        blockedDomains.add("xyz.com");
        when(blocklistProvider.loadBlockedDomains()).thenReturn(blockedDomains);

        DomainFilter domainFilter = new DomainFilter(blocklistProvider);

        assertFalse(domainFilter.isBlocked("google.com"));
    }

    @Test
    void isBlocked_DomainIsInBlockList_ShouldReturnTrue()
    {
        BlocklistProvider blocklistProvider = Mockito.mock();
        HashSet<String> blockedDomains = new HashSet<>();
        blockedDomains.add("abc.com");
        blockedDomains.add("xyz.com");
        blockedDomains.add("google.com");
        when(blocklistProvider.loadBlockedDomains()).thenReturn(blockedDomains);

        DomainFilter domainFilter = new DomainFilter(blocklistProvider);

        assertTrue(domainFilter.isBlocked("google.com"));
    }
}
