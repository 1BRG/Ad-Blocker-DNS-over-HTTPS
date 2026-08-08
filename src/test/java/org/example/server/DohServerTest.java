package org.example.server;

import io.javalin.http.Context;
import org.example.core.AdBlockResolver;
import org.junit.jupiter.api.Test;
import org.xbill.DNS.*;

import static org.mockito.Mockito.*;

class DohServerTest {

    @Test
    void handleDohRequest_WrongContentType_ShouldReturn415() {
        AdBlockResolver mockResolver = mock(AdBlockResolver.class);
        Context mockCtx = mock(Context.class);

        when(mockCtx.contentType()).thenReturn("application/json");
        when(mockCtx.status(anyInt())).thenReturn(mockCtx);

        DohServer server = new DohServer(mockResolver);
        server.handleDohRequest(mockCtx, true);

        verify(mockCtx).status(415);
        verify(mockCtx).result("Unsupported Media Type");

        verifyNoInteractions(mockResolver);
    }
}