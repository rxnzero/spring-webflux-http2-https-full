package com.example.http2.client;

import java.util.concurrent.CountDownLatch;

import javax.net.ssl.SSLContext;

import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.DefaultClientTlsStrategy;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http2.HttpVersionPolicy;
import org.apache.hc.core5.reactor.IOReactorConfig;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.util.Timeout;

public class Http2OnlyClient {
    public static void main(String[] args) throws Exception {

        SSLContext sslContext = SSLContextBuilder.create()
                .loadTrustMaterial(TrustAllStrategy.INSTANCE)
                .build();

        IOReactorConfig ioReactorConfig = IOReactorConfig.custom()
                .setSoTimeout(Timeout.ofSeconds(5))
                .build();

        CloseableHttpAsyncClient client = HttpAsyncClients.custom()
                .setIOReactorConfig(ioReactorConfig)
                .setVersionPolicy(HttpVersionPolicy.FORCE_HTTP_2)
                .setConnectionManager(PoolingAsyncClientConnectionManagerBuilder.create()
                        .setTlsStrategy(new DefaultClientTlsStrategy(sslContext, NoopHostnameVerifier.INSTANCE))
                        .build())
                .build();

        client.start();

        SimpleHttpRequest request = SimpleHttpRequest.create("GET", "https://localhost:8443/hello");

        CountDownLatch latch = new CountDownLatch(1);

        client.execute(request, new FutureCallback<SimpleHttpResponse>() {
            @Override
            public void completed(SimpleHttpResponse response) {
                System.out.println("Response code: " + response.getCode());
                System.out.println("Response body: " + response.getBodyText());
                latch.countDown();
            }

            @Override
            public void failed(Exception ex) {
                System.err.println("@@@@@ Request failed: " + ex.getMessage());
                latch.countDown();
            }

            @Override
            public void cancelled() {
                System.err.println("@@@@@ Request cancelled");
                latch.countDown();
            }
        });

        latch.await();
        client.close();
    }
}
