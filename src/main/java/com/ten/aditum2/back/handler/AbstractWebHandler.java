package com.ten.aditum2.back.handler;

import com.alibaba.fastjson.JSON;
import com.ten.aditum2.back.model.ServiceResult;
import io.vavr.Function2;
import io.vavr.Tuple2;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

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
public abstract class AbstractWebHandler implements WebHandler {

    /**
     * The default value is set in the {@code orElse()} method when the parameter is taken
     */
    protected final String EMPTY_STRING = "";

    // -------------------------------------------------------------- web handle ☆

    /**
     * The webHandle uses {@link WebHandleFunc} encapsulation
     *
     * @param webHandleFunc Three required parameters must be set
     */
    @Override
    public <INPUT, OUTPUT> Mono<ServerResponse> webHandle(WebHandleFunc<INPUT, OUTPUT> webHandleFunc) {
        return webHandleBasicFuncWithThreadChange(
                webHandleFunc.getInput(),
                webHandleFunc.getCheckFunc() != null ? webHandleFunc.getCheckFunc() : input1 -> true,
                webHandleFunc.getBusinessFunc(),
                webHandleFunc.getResponseFunc());
    }

    /**
     * The webHandle uses direct parameter call without input check
     *
     * @param input        Mono.just(data)
     * @param businessFunc service
     * @param responseFunc service output -> {@code ServerResponse}
     */
    public <INPUT, OUTPUT> Mono<ServerResponse> webHandleWithoutCheck(Mono<INPUT> input,
                                                                      Function<INPUT, OUTPUT> businessFunc,
                                                                      Function<OUTPUT, Mono<ServerResponse>> responseFunc) {
        return webHandleBasicFuncWithThreadChange(input, input1 -> true, businessFunc, responseFunc);
    }

    /**
     * The webHandle uses direct parameter call with input check
     *
     * @param input        Mono.just(data)
     * @param checkFunc    test(INPUT inputData): boolean
     * @param businessFunc service
     * @param responseFunc service output -> {@code ServerResponse}
     */
    public <INPUT, OUTPUT> Mono<ServerResponse> webHandleWithCheck(Mono<INPUT> input,
                                                                   Predicate<INPUT> checkFunc,
                                                                   Function<INPUT, OUTPUT> businessFunc,
                                                                   Function<OUTPUT, Mono<ServerResponse>> responseFunc) {
        return webHandleBasicFuncWithThreadChange(input, checkFunc, businessFunc, responseFunc);
    }

    /**
     * Contains thread scheduling and three levels of webHandler implementation,
     * You need to inject different functions to dynamically complete the handler function
     * Note that the parameter cannot be empty, otherwise an exception will be thrown
     * At the same time, if the checkFunc fails, the thread switch does not occur,
     * and the failed result is returned directly
     * <p>
     * ServerRequest  --> HANDLER --> ServiceExec(elastic)    -->  BlockingI/O   -->＋
     * ServerResponse <-- HANDLER <-- ServiceResult(parallel) <--  AsyncResponse <--＋
     *
     * @param input        Mono.just(data).
     * @param checkFunc    test(INPUT inputData): boolean. if test fail, will return {@link #badResponse(Object, HttpStatus)}
     * @param businessFunc service with blocking i/o executed in {@link Schedulers#elastic()}
     * @param responseFunc service output -> {@code ServerResponse}. executed in {@link Schedulers#parallel()}
     * @return {@code Mono<ServerResponse>} with @Service#result
     */
    private <INPUT, OUTPUT> Mono<ServerResponse> webHandleBasicFuncWithThreadChange(
            Mono<INPUT> input,
            Predicate<INPUT> checkFunc,
            Function<INPUT, OUTPUT> businessFunc,
            Function<OUTPUT, Mono<ServerResponse>> responseFunc) {
        // TODO: 2019/8/15 how to exec #checkFunc
        // TODO: 2019/8/15 Input can't be reused when #bodyToMono, but #just can do
        return input
                .publishOn(Schedulers.elastic())
                // business
                .map(inputData -> {
                    log.info("[Service-Start] input=[{}]", inputData.toString());
                    return businessFunc.apply(inputData);
                })
                .timeout(Duration.ofSeconds(30))
                // response
                .flatMap(output -> {
                    log.info("[Service-End] output=[{}]", output.toString());
                    return responseFunc.apply(output);
                })
                // error
                .switchIfEmpty(badResponse("Bad", HttpStatus.INTERNAL_SERVER_ERROR))
                .onErrorResume(ex -> {
                    log.warn("[Service-Exception] ex=[{}]", ex.toString(), ex);
                    return badResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                })
                .log();
    }

    // -------------------------------------------------------------- response func

    protected <T> Mono<ServerResponse> okResponse(T resObj) {
        return responseWithObj(resObj, HttpStatus.OK);
    }

    protected <T> Mono<ServerResponse> okResponse(Mono<T> resObjMono) {
        return responseWithObj(resObjMono, HttpStatus.OK);
    }

    protected <T> Mono<ServerResponse> badResponse(T resObj, HttpStatus badStatus) {
        if (HttpStatus.OK.equals(badStatus)) {
            throw new IllegalArgumentException("bad response cannot use ok status");
        }
        return responseWithObj(resObj, badStatus);
    }

    protected <T> Mono<ServerResponse> badResponse(Mono<T> resObjMono, HttpStatus badStatus) {
        if (HttpStatus.OK.equals(badStatus)) {
            throw new IllegalArgumentException("bad response cannot use ok status");
        }
        return responseWithObj(resObjMono, badStatus);
    }

    /**
     * @return response with result or {@code Mono.empty()}
     * @see ResponseEntity
     */
    private <T> Mono<ServerResponse> responseWithObj(Mono<T> resObjMono, HttpStatus httpStatus) {
        return resObjMono.flatMap(resObj -> responseWithObj(resObj, httpStatus));
    }

    /**
     * The underlying implementation of all the response functions
     *
     * @return response with result or {@code Mono.empty()}
     * @see ResponseEntity
     */
    private <T> Mono<ServerResponse> responseWithObj(T resObj, HttpStatus httpStatus) {
        ResponseEntity<T> responseEntity;
        if (resObj != null) {
            responseEntity = new ResponseEntity<>(resObj, httpStatus);
            return Try.of(() -> JSON.toJSONString(responseEntity))
                    // try response
                    .map(json -> ServerResponse.status(httpStatus)
                            .contentType(MediaType.APPLICATION_JSON_UTF8)
                            .body(Mono.just(json), String.class))
                    // when throwable
                    .onFailure(throwable -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .contentType(MediaType.APPLICATION_JSON_UTF8)
                            .body(Mono.just(throwable.getMessage()), String.class))
                    // when empty
                    .getOrElse(() -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .contentType(MediaType.APPLICATION_JSON_UTF8)
                            .body(Mono.just("None"), String.class)
                            .cache());
        } else {
            return Mono.empty();
        }
    }

    // -------------------------------------------------------------- response auxiliary func

    /**
     * Respond to the result returned from {@code Tuple3<IsSuccess, ErrorMsg, SuccessQueryObj>}
     *
     * @param serviceResult {@code Tuple3<IsSuccess, ErrorMsg, SuccessQueryObj>} must be NonNull
     * @return {@code Mono<ServerResponse>} generated by response func
     */
    protected <T> Mono<ServerResponse> handleResponse(ServiceResult<T> serviceResult) {
        checkNonNull(serviceResult);
        return serviceResult.getSuccess()
                ? success(serviceResult.getExecuteResult())
                : failure(serviceResult.getErrorMsg());
    }

    private void checkNonNull(ServiceResult obj) {
        Objects.requireNonNull(obj, "obj is null, server is not responding to the request");
    }

    private <T> Mono<ServerResponse> success(T data) {
        if (data == null) {
            return okResponse("There is no return value");
        } else {
            return okResponse(data);
        }
    }

    private Mono<ServerResponse> failure(String msg) {
        if (StringUtils.isEmpty(msg)) {
            return badResponse("Unknown failure reason", HttpStatus.SERVICE_UNAVAILABLE);
        } else {
            return badResponse(msg, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // -------------------------------------------------------------- function obj

    /**
     * Consumption function of {@link ServerRequest#headers()}
     */
    private final Function2<ServerRequest, String, String> queryHeaderFunc = (request, key) -> {
        List<String> headerList = request.headers().header(key);
        return CollectionUtils.isEmpty(headerList) ? EMPTY_STRING : headerList.get(0);
    };

    /**
     * Consumption function of {@link ServerRequest#queryParams()}
     */
    private final Function2<ServerRequest, String, String> queryBodyFunc = (request, key) ->
            request.queryParam(key).orElse(EMPTY_STRING);

    /**
     * Consumption function of {@link ServerRequest#pathVariables()}
     */
    private final Function2<ServerRequest, String, String> queryPathFunc = ServerRequest::pathVariable;

    // -------------------------------------------------------------- query single param

    protected Optional<String> queryHeaderParam(ServerRequest request, String paramKey) {
        return querySingle(queryHeaderFunc, request, paramKey);
    }

    protected Optional<String> queryBodyParam(ServerRequest request, String paramKey) {
        return querySingle(queryBodyFunc, request, paramKey);
    }

    protected Optional<String> queryPathParam(ServerRequest request, String paramKey) {
        return querySingle(queryPathFunc, request, paramKey);
    }

    protected String queryHeaderParamOrElse(ServerRequest request, String paramKey, String orElse) {
        return querySingle(queryHeaderFunc, request, paramKey).orElse(orElse);
    }

    protected String queryBodyParamOrElse(ServerRequest request, String paramKey, String orElse) {
        return querySingle(queryBodyFunc, request, paramKey).orElse(orElse);
    }

    protected String queryPathParamOrElse(ServerRequest request, String paramKey, String orElse) {
        return querySingle(queryPathFunc, request, paramKey).orElse(orElse);
    }

    /**
     * Query single value from request
     */
    private Optional<String> querySingle(Function2<ServerRequest, String, String> query,
                                         ServerRequest request,
                                         String paramKey) {
        String value = query.apply(request, paramKey);
        return Optional.ofNullable(value);
    }

    // -------------------------------------------------------------- query params

    protected Tuple2<Boolean, Map<String, String>> queryHeaderParams(ServerRequest request, String... paramKeys) {
        return queryParams(queryHeaderFunc, request, paramKeys);
    }

    protected Tuple2<Boolean, Map<String, String>> queryBodyParams(ServerRequest request, String... paramKeys) {
        return queryParams(queryBodyFunc, request, paramKeys);
    }

    protected Tuple2<Boolean, Map<String, String>> queryPathParams(ServerRequest request, String... paramKeys) {
        return queryParams(queryPathFunc, request, paramKeys);
    }

    /**
     * Query more than one value from request,
     * if all parameters are not queried, the first parameter is {@code false}
     *
     * @return {@code Tuple2<IsAllContains, Map<paramKey, value>>}
     */
    private Tuple2<Boolean, Map<String, String>> queryParams(Function2<ServerRequest, String, String> query,
                                                             ServerRequest request,
                                                             String... paramKeys) {
        final boolean[] isAllContains = {true};
        Map<String, String> allValues = new HashMap<>(paramKeys.length << 1);
        Arrays.stream(paramKeys).forEach(key -> {
            String value = query.apply(request, key);
            if (StringUtils.isEmpty(value)) {
                isAllContains[0] = false;
            } else {
                allValues.put(key, value);
            }
        });
        return new Tuple2<>(isAllContains[0], allValues);
    }
}
