package com.example.http2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@SpringBootApplication
@RestController
public class Http2HttpsServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(Http2HttpsServerApplication.class, args);
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello from Spring WebFlux over HTTPS (HTTP/2) GET";
    }
    
    @PostMapping(value = "/hello", consumes = "text/plain")
    public Mono<String> helloPostText(@RequestBody Mono<String> body) {
        return body.map(data -> {
            System.out.println("Received plain text: " + data);
            return "Hello from Spring WebFlux over HTTPS (HTTP/2) POST";
        });
    }

    @PostMapping(value = "/hello", consumes = "application/json", produces = "application/json")
    public Mono<HelloResponse> helloPostJson(@RequestBody Mono<HelloRequest> body) {
        return body.map(data -> {
            System.out.println("Received JSON message: " + data.getMessage());
            return new HelloResponse("Received JSON message: " + data.getMessage());
        });
    }
}