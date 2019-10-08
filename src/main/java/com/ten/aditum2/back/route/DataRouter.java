package com.ten.aditum2.back.route;

import com.ctrip.ibu.market.hub.schedule.filter.WebBuriedPointFilter;
import com.ctrip.ibu.market.hub.schedule.handler.AppContextHandler;
import com.ten.aditum2.back.filter.WebBuriedPointFilter;
import com.ten.aditum2.back.handler.AppContextHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

/**
 * AppContext routing
 *
 * @author shihaowang
 * @date 2019/8/8
 */
@Configuration
public class DataRouter {

    /**
     * AppContext routing with filter
     *
     * @param applicationContextHandler handler
     * @param webBuriedPointFilter      filter
     */
    @Bean
    @Autowired
    public RouterFunction<ServerResponse> appRoute(final AppContextHandler applicationContextHandler,
                                                   final WebBuriedPointFilter webBuriedPointFilter) {
        return RouterFunctions
                .route(
                        GET("/contexts").and(accept(APPLICATION_JSON)),
                        applicationContextHandler::handleQuery)
                .andRoute(
                        POST("/contexts").and(accept(APPLICATION_JSON)).and(contentType(APPLICATION_JSON)),
                        applicationContextHandler::handleCreate)
                .andRoute(
                        PUT("/contexts").and(accept(APPLICATION_JSON)).and(contentType(APPLICATION_JSON)),
                        applicationContextHandler::handleUpdate)
                .andRoute(
                        DELETE("/contexts").and(accept(APPLICATION_JSON)).and(contentType(APPLICATION_JSON)),
                        applicationContextHandler::handleDelete)
                /*
                 * filter
                 */
                .filter(webBuriedPointFilter);
    }
}
