package com.clnews;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @program: cl-news
 * @author: 李学亮    email: 18222027300@163.com
 * @create: 2019-03-16 21:43
 **/
@SpringBootApplication
@EnableScheduling
@MapperScan(basePackages = {"com.clnews.dao"})
public class ClNewsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClNewsApplication.class, args);
    }

}
