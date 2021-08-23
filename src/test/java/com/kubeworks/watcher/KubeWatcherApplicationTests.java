package com.kubeworks.watcher;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes=KubeWatcherApplication.class)
class KubeWatcherApplicationTests {

    private final ApplicationContext c;
    private final WebApplicationContext w;

    @Autowired
    KubeWatcherApplicationTests(final ApplicationContext c, final WebApplicationContext w) {
        this.c = c; this.w = w;
    }

    @Test
    void springContextTest() {
        assertThat(c).isNotNull(); assertThat(w).isNotNull();
    }
}
