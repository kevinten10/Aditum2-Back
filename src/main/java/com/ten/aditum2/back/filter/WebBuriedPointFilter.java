package com.ten.aditum2.back.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

/**
 * Represents a function that filters a {@linkplain HandlerFunction handler function}.
 *
 * @author shihaowang
 * @date 2019/8/7
 * @see RouterFunction#filter(HandlerFilterFunction)
 */
@Slf4j
@Component
public class WebBuriedPointFilter implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    /**
     * TODO 请求埋点
     */
    @Override
    public Mono<ServerResponse> filter(ServerRequest request, HandlerFunction<ServerResponse> next) {
        log.info("[Request] path=[{}] method=[{}] uri=[{}]", request.path(), request.method(), request.uri().toString());
        return next.handle(request);
    }
}
