package com.ten.aditum2.back.route.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * Base webflux reactive controller
 *
 * @author shihaowang
 * @date 2019/10/7
 */
@Slf4j
@RestController
public abstract class ReactiveController<VO> {

    public static final int NO_DELETED = 0;
    public static final int IS_DELETED = 1;

    /* Reactive Restful Interface */

    @RequestMapping(method = RequestMethod.GET)
    public abstract Mono<ServerResponse> get(@RequestBody VO vo);

    @RequestMapping(method = RequestMethod.POST)
    public abstract Mono<ServerResponse> post(@RequestBody VO vo);

    @RequestMapping(method = RequestMethod.PUT)
    public abstract Mono<ServerResponse> update(@RequestBody VO vo);

    @RequestMapping(method = RequestMethod.DELETE)
    public abstract Mono<ServerResponse> delete(@RequestBody VO vo);
}
