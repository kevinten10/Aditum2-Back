package com.ten.aditum2.back;

import io.reactiverse.pgclient.PgClient;
import io.reactiverse.pgclient.PgPool;
import io.reactiverse.pgclient.PgPoolOptions;
import io.reactiverse.pgclient.PgRowSet;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.Future;

@SpringBootApplication
@MapperScan("com.ten.aditum2.back.mapper")
public class AditumWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(AditumWebApplication.class, args);

        PgPoolOptions options = new PgPoolOptions()
                .setPort(5432)
                .setHost("the-host")
                .setDatabase("the-db")
                .setUser("user")
                .setPassword("secret")
                .setMaxSize(5);
// Create the client pool
        PgPool client = PgClient.pool(options);

// A simple query
        client.query("SELECT * FROM users WHERE id='julien'", ar -> {
            if (ar.succeeded()) {
                PgRowSet result = ar.result();
                System.out.println("Got " + result.size() + " rows ");
            } else {
                System.out.println("Failure: " + ar.cause().getMessage());
            }

            // Now close the pool
            client.close();
        });

        AsynchronousSocketChannel ch = AsynchronousSocketChannel.open();
 
// 连接远程服务器，等待连接完成或者失败
        Future<Void> result = ch.connect(remote);
    }

}