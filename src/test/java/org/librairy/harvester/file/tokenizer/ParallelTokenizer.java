/*
 * Copyright (c) 2016. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */

package org.librairy.harvester.file.tokenizer;

import org.librairy.harvester.file.tokenizer.stanford.StanfordTokenizerEN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created on 08/04/16:
 *
 * @author cbadenes
 */
public class ParallelTokenizer {

    private static final Logger LOG = LoggerFactory.getLogger(ParallelTokenizer.class);

    public void test(){
        StanfordTokenizerEN tokenizer = new StanfordTokenizerEN();


        Runnable task = new Runnable() {
            @Override
            public void run() {

                File file = new File("/Users/cbadenes/Documents/OEG/Projects/MINETUR/TopicModelling-2016/patentes-TIC" +
                        "-norteamericanas/uspto/2005/txt/06904571.txt");

                try {
                    String content = new String(Files.readAllBytes(file.toPath()));

                    Instant b = Instant.now();
                    LOG.info("["+Thread.currentThread().getName()+"] start ...");
                    tokenizer.tokenize(content);
                    Instant e = Instant.now();
                    Duration timeElapsed = Duration.between(b, e);
                    LOG.info("["+Thread.currentThread().getName()+"] elapsed time: "
                            +timeElapsed.toMillis() +" msecs");
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };


        int cpus = Runtime.getRuntime().availableProcessors();
        //int maxThreads = cpus * 2;
        int maxThreads = 1;
        maxThreads = (maxThreads > 0 ? maxThreads : 1);

        ExecutorService executorService =
                new ThreadPoolExecutor(
                        maxThreads, // core thread pool size
                        maxThreads, // maximum thread pool size
                        1, // time to wait before resizing pool
                        TimeUnit.MINUTES,
                        new ArrayBlockingQueue<Runnable>(maxThreads, true),
                        new ThreadPoolExecutor.CallerRunsPolicy());

        for(int i=0;i<5;i++){
            executorService.submit(task);
        }

        executorService.shutdown();

        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                // pool didn't terminate after the first try
                executorService.shutdownNow();
            }


            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                // pool didn't terminate after the second try
            }
        } catch (InterruptedException ex) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }

    }

    public static void main(String[] args){


        ParallelTokenizer test = new ParallelTokenizer();
        Instant b = Instant.now();
        test.test();
        Instant e = Instant.now();
        Duration timeElapsed = Duration.between(b, e);
        System.out.println(" Total time: " +timeElapsed.toMillis() +" msecs");
        System.out.println(Runtime.getRuntime().availableProcessors());

    }

}
