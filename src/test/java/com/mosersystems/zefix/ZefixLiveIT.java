package com.mosersystems.zefix;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Smoke test against the real API. Runs only if credentials are provided:
 * {@code ZEFIX_USERNAME=... ZEFIX_PASSWORD=... mvn test -Dtest=ZefixLiveIT}.
 * Set {@code ZEFIX_BASE_URL} to use another environment, e.g. the integration environment.
 */
@EnabledIfEnvironmentVariable(named = "ZEFIX_USERNAME", matches = ".+")
class ZefixLiveIT {

    @Test
    void getLegalForms() {
        String baseUrl = System.getenv().getOrDefault("ZEFIX_BASE_URL", ZefixProperties.DEFAULT_BASE_URL);
        ZefixClient client = ZefixClient.create(
                new ZefixProperties(true, baseUrl, System.getenv("ZEFIX_USERNAME"), System.getenv("ZEFIX_PASSWORD")),
                RestClient.builder());

        assertFalse(client.getLegalForms().isEmpty());
    }
}
