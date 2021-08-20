package com.kubeworks.watcher;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.ApplicationPidFileWriter;

@SpringBootApplication
public class KubeWatcherApplication {

    public static void main(final String[] args) {
        new SpringApplicationBuilder(KubeWatcherApplication.class).listeners(new ApplicationPidFileWriter()).run(args);
    }
}
