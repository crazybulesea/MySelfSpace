package com.lu.dfw;

import com.lu.dfw.config.Const;
import com.lu.dfw.define.DataConfigManager;
import com.lu.dfw.loggic.TaskFactory;
import com.lu.dfw.netty.BootNettyServer;
import com.lu.dfw.thread.ThreadManager;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.Async;

//@EnableAsync
@SpringBootApplication
public class DfwApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(DfwApplication.class, args);
    }

    @Async
    @Override
    public void run(String... args) throws Exception {
        DataConfigManager.init();
        ThreadManager.init();
        TaskFactory.init();
        BootNettyServer bean = new BootNettyServer();
        bean.bind(Const.Port);
    }
}