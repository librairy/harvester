package org.librairy.harvester.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.*;

/**
 * Created by cbadenes on 24/02/16.
 */
@Component
public class ParallelExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(ParallelExecutor.class);


    private static final Integer NUM_THREADS = 50;

    ExecutorService pool;

    @PostConstruct
    public void setup(){
        pool = Executors.newFixedThreadPool(NUM_THREADS);
    }

    public void execute(Runnable task){
        pool.execute(task);
    }
}
