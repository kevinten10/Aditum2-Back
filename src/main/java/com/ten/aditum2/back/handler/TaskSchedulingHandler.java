package com.ten.aditum2.back.handler;

import com.ctrip.ibu.market.hub.schedule.model.dto.ServiceResult;
import com.ctrip.ibu.market.hub.schedule.model.entity.ScheduleTaskRecord;
import com.ctrip.ibu.market.hub.schedule.model.vo.ScheduleAppContextVo;
import com.ctrip.ibu.market.hub.schedule.model.vo.ScheduleCycleTaskVo;
import com.ctrip.ibu.market.hub.schedule.model.vo.ScheduleDelayTaskVo;
import com.ctrip.ibu.market.hub.schedule.model.vo.ScheduleTaskMetaVo;
import com.ctrip.ibu.market.hub.schedule.service.core.TaskSchedulingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import javax.annotation.Nonnull;
import java.util.List;
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
public class TaskSchedulingHandler extends AbstractWebHandler {

    private final TaskSchedulingService taskSchedulingService;

    public TaskSchedulingHandler(TaskSchedulingService taskSchedulingService) {
        this.taskSchedulingService = taskSchedulingService;
    }

    // -------------------------------------------------------------- query

    /**
     * Query task scheduling config
     *
     * @apiNote GET /tasks/task/{taskId}
     */
    public Mono<ServerResponse> handleQueryTask(ServerRequest request) {
        return webHandle(WebHandleFunc.<String, ServiceResult<? extends ScheduleTaskMetaVo>>builder()
                .input(Mono.just(queryBodyParamOrElse(request, "taskId", EMPTY_STRING)))
                .checkFunc(taskId -> !StringUtils.isEmpty(taskId))
                .businessFunc(taskSchedulingService::queryTaskScheduling)
                .responseFunc(this::handleResponse)
                .build());
    }

    /**
     * Query app's task scheduling config list
     *
     * @apiNote GET /tasks/app/{appId}
     */
    public Mono<ServerResponse> handleQueryApps(ServerRequest request) {
        return webHandle(WebHandleFunc.<String, ServiceResult<ScheduleAppContextVo>>builder()
                .input(Mono.just(queryBodyParamOrElse(request, "taskId", EMPTY_STRING)))
                .checkFunc(taskId -> !StringUtils.isEmpty(taskId))
                .businessFunc(taskSchedulingService::queryTaskSchedulingListByApp)
                .responseFunc(this::handleResponse)
                .build());
    }

    /**
     * Query task history scheduling records
     *
     * @apiNote GET /tasks/record/{taskId}
     */
    public Mono<ServerResponse> handleQueryRecord(ServerRequest request) {
        return webHandle(WebHandleFunc.<String, ServiceResult<List<ScheduleTaskRecord>>>builder()
                .input(Mono.just(queryBodyParamOrElse(request, "taskId", EMPTY_STRING)))
                .checkFunc(taskId -> !StringUtils.isEmpty(taskId))
                .businessFunc(taskSchedulingService::queryTaskSchedulingRecord)
                .responseFunc(this::handleResponse)
                .build());
    }

    // -------------------------------------------------------------- create

    /**
     * Create delay task scheduling config
     *
     * @apiNote POST /tasks/delay
     */
    public Mono<ServerResponse> handleCreateDelay(ServerRequest request) {
        return webHandle(WebHandleFunc.<ScheduleDelayTaskVo, ServiceResult<ScheduleDelayTaskVo>>builder()
                .input(request.bodyToMono(ScheduleDelayTaskVo.class))
                .checkFunc(Objects::nonNull)
                .businessFunc(taskSchedulingService::createDelayTaskScheduling)
                .responseFunc(this::handleResponse)
                .build());
    }

    /**
     * Create cycle task scheduling config
     *
     * @apiNote POST /tasks/cycle
     */
    public Mono<ServerResponse> handleCreateCycle(ServerRequest request) {
        return webHandle(WebHandleFunc.<ScheduleCycleTaskVo, ServiceResult<ScheduleCycleTaskVo>>builder()
                .input(request.bodyToMono(ScheduleCycleTaskVo.class))
                .checkFunc(Objects::nonNull)
                .businessFunc(taskSchedulingService::createCycleTaskScheduling)
                .responseFunc(this::handleResponse)
                .build());
    }

    // -------------------------------------------------------------- update

    /**
     * Update task scheduling config
     *
     * @apiNote PUT /tasks/delay
     */
    public Mono<ServerResponse> handleUpdateDelayTask(ServerRequest request) {
        return webHandle(WebHandleFunc.<ScheduleDelayTaskVo, ServiceResult<Void>>builder()
                .input(request.bodyToMono(ScheduleDelayTaskVo.class))
                .checkFunc(Objects::nonNull)
                .businessFunc(taskSchedulingService::updateDelayTaskScheduling)
                .responseFunc(this::handleResponse)
                .build());
    }

    /**
     * Update task scheduling config
     *
     * @apiNote PUT /tasks/cycle
     */
    public Mono<ServerResponse> handleUpdateCycleTask(@Nonnull ServerRequest request) {
        return webHandle(WebHandleFunc.<ScheduleCycleTaskVo, ServiceResult<Void>>builder()
                .input(request.bodyToMono(ScheduleCycleTaskVo.class))
                .checkFunc(Objects::nonNull)
                .businessFunc(taskSchedulingService::updateCycleTaskScheduling)
                .responseFunc(this::handleResponse)
                .build());
    }

    // -------------------------------------------------------------- delete

    /**
     * Delete task scheduling config
     *
     * @apiNote DELETE /tasks/task/{taskId}
     */
    public Mono<ServerResponse> handleDeleteTask(ServerRequest request) {
        return webHandle(WebHandleFunc.<ScheduleTaskMetaVo, ServiceResult<Void>>builder()
                .input(request.bodyToMono(ScheduleTaskMetaVo.class))
                .checkFunc(Objects::nonNull)
                .businessFunc(taskSchedulingService::deleteTaskScheduling)
                .responseFunc(this::handleResponse)
                .build());
    }

    // -------------------------------------------------------------- operate

    /**
     * Suspend task execution and update configuration
     *
     * @apiNote PUT /tasks/operate/pause/{taskId}
     */
    public Mono<ServerResponse> handlePauseTask(ServerRequest request) {
        return webHandle(WebHandleFunc.<ScheduleTaskMetaVo, ServiceResult<Void>>builder()
                .input(request.bodyToMono(ScheduleTaskMetaVo.class))
                .checkFunc(Objects::nonNull)
                .businessFunc(taskSchedulingService::pauseTaskScheduling)
                .responseFunc(this::handleResponse)
                .build());
    }

    /**
     * Active task execution and update configuration
     *
     * @apiNote PUT /tasks/operate/active/{taskId}
     */
    public Mono<ServerResponse> handleActiveTask(ServerRequest request) {
        return webHandle(WebHandleFunc.<ScheduleTaskMetaVo, ServiceResult<Void>>builder()
                .input(request.bodyToMono(ScheduleTaskMetaVo.class))
                .checkFunc(Objects::nonNull)
                .businessFunc(taskSchedulingService::activeTaskScheduling)
                .responseFunc(this::handleResponse)
                .build());
    }

}
