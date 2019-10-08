package com.ten.aditum2.back.handler;

import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.Map;

/**
 * Global uncaught exception attributes
 *
 * @author shihaowang
 * @date 2019/8/28
 */
@Component
public class GlobalExceptionAttributes extends DefaultErrorAttributes {

    public GlobalExceptionAttributes() {
        super(false);
    }

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, boolean includeStackTrace) {
        return super.getErrorAttributes(request, false);
    }
}
