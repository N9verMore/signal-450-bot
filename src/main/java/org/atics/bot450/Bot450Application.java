package org.atics.bot450;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Bot450Application {

    public static void main(String[] args) {
        SpringApplication.run(Bot450Application.class, args);
    }

}
