package org.example.zhonglundemo2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableAsync
@EnableScheduling
public class Zhonglundemo2Application {
    public static void main(String[] args) {
        SpringApplication.run(Zhonglundemo2Application.class, args);
    }
}
