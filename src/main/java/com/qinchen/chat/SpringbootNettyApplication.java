package com.qinchen.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class SpringbootNettyApplication extends SpringBootServletInitializer{

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(SpringbootNettyApplication.class);
    }

    public static void main(String[] args) {
        /*SpringApplication application = new SpringApplication(SpringbootNettyApplication.class);
        application.setBannerMode(Banner.Mode.OFF);
        application.run(args);*/
        SpringApplication.run(SpringbootNettyApplication.class,args);
    }


    /*public static void main(String[] args) {
        SpringApplication.run(SpringbootNettyApplication.class,args);

    }*/


}
