package com.mosersystems.zefix;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Configuration properties for the Zefix client, bound to the {@code zefix.*} prefix.
 *
 * @param enabled  whether a {@link ZefixClient} bean is auto-configured
 * @param baseUrl  base URL of the ZefixPublicREST API
 * @param username username for HTTP basic authentication; no authentication is sent if empty
 * @param password password for HTTP basic authentication
 */
@ConfigurationProperties(prefix = "zefix")
public record ZefixProperties(
        @DefaultValue("true") boolean enabled,
        @DefaultValue(ZefixProperties.DEFAULT_BASE_URL) String baseUrl,
        String username,
        String password
) {
    /** Base URL of the production ZefixPublicREST API. */
    public static final String DEFAULT_BASE_URL = "https://www.zefix.admin.ch/ZefixPublicREST";

    /** Base URL of the ZefixPublicREST integration (test) environment. */
    public static final String INTEGRATION_BASE_URL = "https://www.zefixintg.admin.ch/ZefixPublicREST";
}
