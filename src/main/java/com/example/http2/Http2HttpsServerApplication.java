package com.example.http2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

@SpringBootApplication
@RestController
public class Http2HttpsServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(Http2HttpsServerApplication.class, args);
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello from Spring WebFlux over HTTPS (HTTP/2)";
    }
}