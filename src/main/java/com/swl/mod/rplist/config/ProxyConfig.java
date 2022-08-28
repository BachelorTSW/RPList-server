package com.swl.mod.rplist.config;

import org.springframework.context.annotation.Bean;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.HttpURLConnection;

public class ProxyConfig {

    @Bean
    public RestTemplate proxyRestTemplate() {
        return new RestTemplate(new NoRedirectSimpleClientHttpRequestFactory());
    }

    static class NoRedirectSimpleClientHttpRequestFactory extends SimpleClientHttpRequestFactory {
        @Override
        protected void prepareConnection(@Nonnull HttpURLConnection connection, @Nonnull String httpMethod) throws IOException {
            super.prepareConnection(connection, httpMethod);
            connection.setInstanceFollowRedirects(false);
        }
    }
}
