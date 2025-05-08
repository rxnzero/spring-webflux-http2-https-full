package com.example.http2.client;

import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.client5.http.async.methods.SimpleRequestBuilder;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http2.HttpVersionPolicy;

import java.util.concurrent.CountDownLatch;

public class Http2TestClient {

    public static void main(String[] args) throws Exception {
        CloseableHttpAsyncClient client = HttpAsyncClients.custom()
                .setVersionPolicy(HttpVersionPolicy.FORCE_HTTP_2)
                .build();

        client.start();

        SimpleHttpRequest request = SimpleRequestBuilder.get("https://localhost:8443/hello").build();

        CountDownLatch latch = new CountDownLatch(1);

        client.execute(request, new FutureCallback<SimpleHttpResponse>() {
            @Override
            public void completed(SimpleHttpResponse response) {
                System.out.println("Response: " + response.getCode());
                System.out.println(response.getBodyText());
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
