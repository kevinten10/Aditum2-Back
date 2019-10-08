package com.ten.aditum2.back.route;

import com.alibaba.fastjson.JSON;
import io.vavr.Lazy;
import io.vavr.Tuple2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

/**
 * Test routing
 *
 * @author shihaowang
 * @date 2019/8/8
 */
@Configuration
public class TestRouter {
    private Lazy<BigInteger> lazy = Lazy.of(() -> BigInteger
            .valueOf(1024 * ThreadLocalRandom.current().nextInt(10, 100))
            .pow(1024));

    /**
     * Test routing
     */
    @Bean
    public RouterFunction<ServerResponse> testRoute() {
        return RouterFunctions
                .route(
                        GET("/welcome"),
                        request -> ServerResponse.ok().body(Mono.just("welcome_" + lazy.get()), String.class))
                .andRoute(
                        GET("/to"),
                        request -> ServerResponse.ok().body(Flux.range(1, 100).log(), Integer.class))
                .andRoute(
                        GET("/aditum"),
                        request -> ServerResponse.ok().contentType(MediaType.APPLICATION_STREAM_JSON).body(
                                Flux.interval(Duration.ofMillis(500))
                                        .map(l -> JSON.toJSONString(new Tuple2<>(System.currentTimeMillis(),
                                                ThreadLocalRandom.current().nextInt(100, 125))))
                                        .log(),
                                String.class));
    }
}
