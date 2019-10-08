package com.ten.aditum2.back.route;

import com.ctrip.ibu.market.hub.schedule.filter.WebBuriedPointFilter;
import com.ctrip.ibu.market.hub.schedule.handler.TaskSchedulingHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

/**
 * Task routing
 *
 * @author shihaowang
 * @date 2019/8/8
 */
@Configuration
public class TableRouter {

    /**
     * Task routing with filter
     *
     * @param taskSchedulingHandler handler
     * @param webBuriedPointFilter  filter
     */
    @Bean
    @Autowired
    public RouterFunction<ServerResponse> taskRoute(final TaskSchedulingHandler taskSchedulingHandler,
                                                    final WebBuriedPointFilter webBuriedPointFilter) {
        return RouterFunctions
                // get
                .route(
                        GET("/tasks/task").and(contentType(APPLICATION_JSON)),
                        taskSchedulingHandler::handleQueryTask)
                .andRoute(
                        GET("/tasks/app").and(contentType(APPLICATION_JSON)),
                        taskSchedulingHandler::handleQueryApps)
                .andRoute(
                        GET("/tasks/record").and(contentType(APPLICATION_JSON)),
                        taskSchedulingHandler::handleQueryRecord)
                // post
                .andRoute(
                        POST("/tasks/delay").and(accept(APPLICATION_JSON)).and(contentType(APPLICATION_JSON)),
                        taskSchedulingHandler::handleCreateDelay)
                .andRoute(
                        POST("/tasks/cycle").and(accept(APPLICATION_JSON)).and(contentType(APPLICATION_JSON)),
                        taskSchedulingHandler::handleCreateCycle)
                // put
                .andRoute(
                        PUT("/tasks/delay").and(accept(APPLICATION_JSON)).and(contentType(APPLICATION_JSON)),
                        taskSchedulingHandler::handleUpdateDelayTask)
                .andRoute(
                        PUT("/tasks/cycle").and(accept(APPLICATION_JSON)).and(contentType(APPLICATION_JSON)),
                        taskSchedulingHandler::handleUpdateCycleTask)
                .andRoute(
                        PUT("/tasks/operate/pause").and(accept(APPLICATION_JSON)).and(contentType(APPLICATION_JSON)),
                        taskSchedulingHandler::handlePauseTask)
                .andRoute(
                        PUT("/tasks/operate/active").and(accept(APPLICATION_JSON)).and(contentType(APPLICATION_JSON)),
                        taskSchedulingHandler::handleActiveTask)
                // delete
                .andRoute(
                        DELETE("/tasks/task").and(accept(APPLICATION_JSON)).and(contentType(APPLICATION_JSON)),
                        taskSchedulingHandler::handleDeleteTask)
                /*
                 * filter
                 */
                .filter(webBuriedPointFilter);
    }
}
