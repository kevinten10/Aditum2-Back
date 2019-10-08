package com.ten.aditum2.back.route;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

/**
 * Healthy check routing
 *
 * @author shihaowang
 * @date 2019/8/8
 */
@Configuration
public class HealthRouter {

    /**
     * Healthy routing
     */
    @Bean
    public RouterFunction<ServerResponse> healthRoute() {
        return RouterFunctions
                .route(GET("/health"), request -> ServerResponse.ok().build())
                .andRoute(GET("/version"), request ->
                        ServerResponse.ok().contentType(APPLICATION_JSON).body(Mono.just("aditum 2.0.0"), String.class));
    }
}
