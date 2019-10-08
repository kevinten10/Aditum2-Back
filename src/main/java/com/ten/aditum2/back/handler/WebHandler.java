package com.ten.aditum2.back.handler;

import lombok.Builder;
import lombok.Getter;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Refer to @Controller hierarchy, The @handler with receive {@link ServerRequest},
 * Handler will process the parameters and forward them to the appropriate logic layer,
 * such as @Service hierarchy. And it will receive the next layer's response and wrap it as
 * {@link ServerResponse} then respond to
 * client requests.
 * <p>
 * The handler(controller) layer essentially does message forwarding, which is the bridge between
 * {@link RouterFunction} and {@code Service}.
 * It extracts the parameter values from the {@link ServerRequest} and forwards them to the business function
 * service(another thread).
 * <p>
 * All the steps in handler can be abstracted into three layers:
 * 1. The first layer obtains the parameter {@link WebHandleFunc#input} from the Server Request original Request,
 * and optionally checks the {@link WebHandleFunc#input} by passing in an optional check function.
 * 2. The second layer passes {@link WebHandleFunc#input} into the business logic processing function, believing that
 * the business logic contains blocking operations, so elastic thread pools are used for processing.
 * 3. The third layer obtains the returned output of the business logic processing, converts the output uniformly
 * to the Server Response Response result, and sends it to the parallel thread pool for Response.
 * <p>
 * As can be seen from the above, for handler only function processing is different,
 * so we can defined the interface {@link #webHandle(WebHandleFunc)}:
 * The {@link #webHandle(WebHandleFunc)}d takes three required parameters and an optional check function for
 * unified forwarding and thread switching.
 *
 * @author shihaowang
 * @date 2019/8/8
 */
@FunctionalInterface
public interface WebHandler {

    /*
     * WebHandler说明：
     * handler(controller)层本质上的工作是消息转接，它是路由层(route)和业务逻辑层(service)的桥接。
     * 它将提取ServerRequest请求中的参数值，并将其转发给业务逻辑处理函数service(其他线程)。
     * handler的所有的步骤可以抽象为以下三层：
     * 第一层为从ServerRequest原始请求中获取参数input，同时可以选择对input进行检查，传入一个可选的check函数。
     * 第二层为将input传入业务逻辑处理函数，认为业务逻辑包含阻塞操作，故使用elastic线程池进行处理。
     * 第三层为获取业务逻辑处理的返回结果output，并将output统一转换为ServerResponse响应结果，发送到parallel线程池进行响应。
     * 由以上可见，对于handler，只有函数处理上的不同，故进行抽象：
     * webHandle方法接收三个必需参数以及一个可选的检查函数，统一的进行转接处理和线程切换。
     *
     * ServerRequest  --> HANDLER --> ServiceExec(elastic)    -->  BlockingI/O
     *                                                                  ↓
     * ServerResponse <-- HANDLER <-- ServiceResult(parallel) <--  AsyncResponse
     */

    <INPUT, OUTPUT> Mono<ServerResponse> webHandle(WebHandleFunc<INPUT, OUTPUT> webHandleFunc);

    /**
     * Function operations at the handler layer are encapsulated
     * so that you can choose whether to use objects or parameters directly
     */
    @Builder
    class WebHandleFunc<INPUT, OUTPUT> {
        /**
         * The input of {@link #businessFunc} from {@link ServerRequest}
         */
        @Getter
        private Mono<INPUT> input;
        /**
         * The optional operate to check the {@link #input} data()
         */
        @Getter
        private Predicate<INPUT> checkFunc;
        /**
         * Concrete business service with blocking operations, Using {@link Schedulers#elastic()}
         */
        @Getter
        private Function<INPUT, OUTPUT> businessFunc;
        /**
         * Create {@link ServerResponse} based on the result returned by the business service,
         * Using {@link Schedulers#parallel()}
         */
        @Getter
        private Function<OUTPUT, Mono<ServerResponse>> responseFunc;
    }
}
