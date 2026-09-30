package com.mosersystems.zefix.autoconfigure;

import com.mosersystems.zefix.ZefixClient;
import com.mosersystems.zefix.ZefixProperties;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;

class ZefixAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(ZefixAutoConfiguration.class));

    @Test
    void createsClientWithDefaults() {
        contextRunner
                .withPropertyValues(
                        "zefix.username=user",
                        "zefix.password=secret")
                .run(context -> {
                    assertThat(context).hasSingleBean(ZefixClient.class);
                    ZefixProperties properties = context.getBean(ZefixProperties.class);
                    assertThat(properties.enabled()).isTrue();
                    assertThat(properties.baseUrl()).isEqualTo(ZefixProperties.DEFAULT_BASE_URL);
                    assertThat(properties.username()).isEqualTo("user");
                    assertThat(properties.password()).isEqualTo("secret");
                });
    }

    @Test
    void bindsBaseUrl() {
        contextRunner
                .withPropertyValues("zefix.base-url=" + ZefixProperties.INTEGRATION_BASE_URL)
                .run(context -> assertThat(context.getBean(ZefixProperties.class).baseUrl())
                        .isEqualTo(ZefixProperties.INTEGRATION_BASE_URL));
    }

    @Test
    void usesApplicationRestClientBuilder() {
        contextRunner
                .withBean(RestClient.Builder.class, RestClient::builder)
                .run(context -> assertThat(context).hasSingleBean(ZefixClient.class));
    }

    @Test
    void backsOffWhenDisabled() {
        contextRunner
                .withPropertyValues("zefix.enabled=false")
                .run(context -> assertThat(context).doesNotHaveBean(ZefixClient.class));
    }

    @Test
    void backsOffWhenUserDefinesClient() {
        ZefixClient custom = ZefixClient.create(
                new ZefixProperties(true, ZefixProperties.DEFAULT_BASE_URL, null, null), RestClient.builder());
        contextRunner
                .withBean(ZefixClient.class, () -> custom)
                .run(context -> assertThat(context.getBean(ZefixClient.class)).isSameAs(custom));
    }
}
