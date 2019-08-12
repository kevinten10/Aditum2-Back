package com.ten.aditum2.back.util;

import java.util.UUID;

public class UidGenerator {

    /**
     * 基于UUID 生成分布式全局唯一ID
     */
    public static String generateUid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

}
