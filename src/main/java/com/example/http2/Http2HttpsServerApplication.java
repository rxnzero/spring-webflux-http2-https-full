package com.example.http2;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@SpringBootApplication
@RestController
public class Http2HttpsServerApplication {
	
	private static final Logger log = LoggerFactory.getLogger(Http2HttpsServerApplication.class);
	
    public static void main(String[] args) {
        SpringApplication.run(Http2HttpsServerApplication.class, args);
    }

    // GET 요청: 쿼리 파라미터 출력 및 응답
    @GetMapping("/hello")
    public Mono<Map<String, Object>> hello(@RequestParam Map<String, String> params) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Hello from Spring WebFlux over HTTPS (HTTP/2) GET");

        if (params.isEmpty()) {
            response.put("params", "no-param");
        } else {
            response.put("params", params);
        }
        return Mono.just(response);
    }
    
    // POST 요청: JSON 형식으로 body 응답
    @PostMapping(value = "/hello", consumes = "application/x-www-form-urlencoded", produces = "application/json")
    public Mono<Map<String, Object>> helloPost(@RequestParam Map<String, String> formParams) {
    	log.info("POST Params: " + formParams);
        return Mono.just(Map.of(
                "message", "Hello from Spring WebFlux over HTTPS (HTTP/2) POST",
                "params", formParams
        ));
    }
    
    @PostMapping(value = "/hello", consumes = "text/plain")
    public Mono<String> helloPostText(@RequestBody Mono<String> body) {
        return body.map(data -> {
        	log.info("Received plain text: " + data);
            return "Hello from Spring WebFlux over HTTPS (HTTP/2) POST";
        });
    }

    @PostMapping(value = "/hello", consumes = "application/json", produces = "application/json")
    public Mono<HelloResponse> helloPostJson(@RequestBody Mono<HelloRequest> body) {
        return body.map(data -> {
        	log.info("Received JSON message: " + data.getMessage());
            return new HelloResponse("Received JSON message: " + data.getMessage());
        });
    }
}