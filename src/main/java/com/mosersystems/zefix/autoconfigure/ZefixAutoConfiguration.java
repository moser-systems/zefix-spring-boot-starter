package com.mosersystems.zefix.autoconfigure;

import com.mosersystems.zefix.ZefixClient;
import com.mosersystems.zefix.ZefixProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

/**
 * Auto-configures a {@link ZefixClient}, unless {@code zefix.enabled=false}.
 */
@AutoConfiguration
@EnableConfigurationProperties(ZefixProperties.class)
@ConditionalOnProperty(prefix = "zefix", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ZefixAutoConfiguration {

    /**
     * Creates the Zefix client.
     *
     * @param properties         Zefix configuration
     * @param restClientBuilders optional application-provided RestClient builder
     * @return the client
     */
    @Bean
    @ConditionalOnMissingBean
    public ZefixClient zefixClient(ZefixProperties properties, ObjectProvider<RestClient.Builder> restClientBuilders) {
        return ZefixClient.create(properties, restClientBuilders.getIfAvailable(RestClient::builder));
    }
}
