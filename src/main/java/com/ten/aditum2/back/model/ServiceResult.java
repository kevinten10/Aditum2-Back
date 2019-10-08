package com.ten.aditum2.back.model;

import io.vavr.Tuple1;
import io.vavr.Tuple2;
import io.vavr.Tuple3;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author shihaowang
 * @date 2019/8/15
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
public class ServiceResult<T> {

    private final Boolean success;
    private final String errorMsg;
    private final T executeResult;

    // -------------------------------------------------------------- from tuple

    public static <T> ServiceResult<T> of(Tuple1<Boolean> tuple1) {
        return new ServiceResult<>(tuple1._1, null, null);
    }

    public static <T> ServiceResult<T> of(Tuple2<Boolean, String> tuple2) {
        return new ServiceResult<>(tuple2._1, tuple2._2, null);
    }

    public static <Obj> ServiceResult<Obj> of(Tuple3<Boolean, String, Obj> tuple3) {
        return new ServiceResult<>(tuple3._1, tuple3._2, tuple3._3);
    }

    // -------------------------------------------------------------- generator

    private static final Boolean SUCCESS = true;
    private static final Boolean FAILURE = false;

    public static <Obj> ServiceResult<Obj> failure(String errorMsg) {
        return new ServiceResult<>(FAILURE, errorMsg, null);
    }

    public static <Obj> ServiceResult<Obj> failure(String errorMsg, Obj errorDescription) {
        return new ServiceResult<>(FAILURE, errorMsg, errorDescription);
    }

    public static ServiceResult<Void> success() {
        return new ServiceResult<>(SUCCESS, null, null);
    }

    public static <Obj> ServiceResult<Obj> success(Obj successResult) {
        return new ServiceResult<>(SUCCESS, null, successResult);
    }

}
