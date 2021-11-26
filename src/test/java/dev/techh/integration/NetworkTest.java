package dev.techh.integration;

import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class NetworkTest {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @BeforeEach
    public void setUp() {
        MDC.put("traceId",  this.getClass().getSimpleName() + "-" + UUID.randomUUID());
    }

   // @Test
    public void shouldReachTimeoutLimit() throws IOException, InterruptedException, ExecutionException {
        var client = HttpClient.newHttpClient();

        var request = HttpRequest.newBuilder(
                        URI.create("https://hookb.in/r19mYr1BwVfqk2XXkPmr"))
                .header("accept", "application/json")
                .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        LOG.info("" + response.body().length());

        var request2 = HttpRequest.newBuilder(
                        URI.create("https://hookb.in/r19mYr1BwVfqk2XXkPmr?req=2"))
                .header("accept", "application/json")
                .build();

        CompletableFuture<HttpResponse<String>> future = client.sendAsync(request2, HttpResponse.BodyHandlers.ofString());
        LOG.info("" + future.get().body().length());

    }

}
