package com.ten.aditum2.back.handler;

import com.ctrip.ibu.market.hub.schedule.model.dto.ServiceResult;
import com.ctrip.ibu.market.hub.schedule.model.vo.ScheduleAppContextVo;
import com.ctrip.ibu.market.hub.schedule.service.core.AppContextService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * Refer to @Controller hierarchy, The @handler with receive {@link ServerRequest},
 * Handler will process the parameters and forward them to the appropriate logic layer,
 * such as @Service hierarchy. And it will receive the next layer's response and wrap it as
 * {@link ServerResponse} then respond to
 * client requests.
 *
 * @author shihaowang
 * @date 2019/8/8
 */
@Slf4j
@Component
public class AppContextHandler extends AbstractWebHandler {

    private final AppContextService appContextService;

    public AppContextHandler(AppContextService appContextService) {
        this.appContextService = appContextService;
    }

    /**
     * @param request appId   :header param
     * @see AppContextService#queryApplicationContext(String)
     */
    public Mono<ServerResponse> handleQuery(ServerRequest request) {
        return webHandle(WebHandleFunc.<String, ServiceResult<ScheduleAppContextVo>>builder()
                .input(Mono.just(queryBodyParamOrElse(request, "appId", EMPTY_STRING)))
                .checkFunc(appId -> !StringUtils.isEmpty(appId))
                .businessFunc(appContextService::queryApplicationContext)
                .responseFunc(this::handleResponse)
                .build());
    }

    /**
     * @param request contextMeta :body
     * @see AppContextService#createApplicationContext(ScheduleAppContextVo)
     */
    public Mono<ServerResponse> handleCreate(ServerRequest request) {
        return webHandle(WebHandleFunc.<ScheduleAppContextVo, ServiceResult<ScheduleAppContextVo>>builder()
                .input(request.bodyToMono(ScheduleAppContextVo.class))
                .checkFunc(Objects::nonNull)
                .businessFunc(appContextService::createApplicationContext)
                .responseFunc(this::handleResponse)
                .build());
    }

    /**
     * @param request contextMeta :body
     * @see AppContextService#updateApplicationContext(ScheduleAppContextVo)
     */
    public Mono<ServerResponse> handleUpdate(ServerRequest request) {
        return webHandle(WebHandleFunc.<ScheduleAppContextVo, ServiceResult<Void>>builder()
                .input(request.bodyToMono(ScheduleAppContextVo.class))
                .checkFunc(Objects::nonNull)
                .businessFunc(appContextService::updateApplicationContext)
                .responseFunc(this::handleResponse)
                .build());
    }

    /**
     * @param request contextMeta :header param
     * @see AppContextService#deleteApplicationContext(ScheduleAppContextVo)
     */
    public Mono<ServerResponse> handleDelete(ServerRequest request) {
        return webHandle(WebHandleFunc.<ScheduleAppContextVo, ServiceResult<Void>>builder()
                .input(request.bodyToMono(ScheduleAppContextVo.class))
                .checkFunc(Objects::nonNull)
                .businessFunc(appContextService::deleteApplicationContext)
                .responseFunc(this::handleResponse)
                .build());
    }

}
