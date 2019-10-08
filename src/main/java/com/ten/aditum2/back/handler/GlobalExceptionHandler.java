package com.ten.aditum2.back.handler;

import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Global uncaught exception handler
 *
 * @author shihaowang
 * @date 2019/8/28
 */
@Component
@Order(-2)
public class GlobalExceptionHandler extends AbstractErrorWebExceptionHandler {

    /**
     * Problem solving {@literal <a href="https://xbuba.com/questions/52497901"/>}
     */
    public GlobalExceptionHandler(ErrorAttributes errorAttributes,
                                  ResourceProperties resourceProperties,
                                  ApplicationContext applicationContext,
                                  ServerCodecConfigurer configurer) {
        super(errorAttributes, resourceProperties, applicationContext);
        this.setMessageWriters(configurer.getWriters());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(final ServerRequest request) {
        final Map<String, Object> errorPropertiesMap = getErrorAttributes(request, true);
        return ServerResponse.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(BodyInserters.fromObject(errorPropertiesMap));
    }
}
