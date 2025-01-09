package com.OnlineConsultancyApp.runnables;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ThreadManager {

    @Autowired
    CheckTimesLeftRunnable checkTimesLeftRunnable;


    @PostConstruct
    public void startThreads() {
        Thread thread = new Thread(checkTimesLeftRunnable);
        thread.start();
    }
}

