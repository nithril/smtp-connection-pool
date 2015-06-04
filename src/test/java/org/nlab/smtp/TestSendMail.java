package org.nlab.smtp;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by nlabrot on 29/04/15.
 */
public class TestSendMail extends AbstractTest {

    private static final Logger LOG = LoggerFactory.getLogger(TestSendMail.class);

    public static final int NB_THREAD = 10;


    @Test
    public void testSend() throws Exception {
        send();
    }


    @Test
    public void testConcurrentSend() throws Exception {
        AtomicInteger counter = new AtomicInteger();

        ExecutorService executorService = Executors.newFixedThreadPool(NB_THREAD);

        for (int i = 0; i < NB_THREAD; i++) {
            executorService.submit(() -> {
                for (int m = 0; m < 200; m++) {
                    send();
                    counter.incrementAndGet();
                }
                return null;
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);
        Assert.assertEquals(NB_THREAD * 200, counter.get());
        Assert.assertEquals(8, smtpConnectionPool.getCreatedCount());
    }


    @Test
    public void testSendAfterDeconnection() throws Exception {
        AtomicInteger counter = new AtomicInteger();

        ExecutorService executorService = Executors.newFixedThreadPool(1000);

        CountDownLatch countDownLatch = new CountDownLatch(10);


        for (int i = 0; i < NB_THREAD; i++) {
            executorService.submit(() -> {
                for (int m = 0; m < 10; m++) {
                    send();
                    counter.incrementAndGet();
                }
                countDownLatch.countDown();
                return null;
            });
        }

        countDownLatch.await();

        stopServer();
        Thread.sleep(5000);
        startServer();

        for (int i = 0; i < NB_THREAD; i++) {
            executorService.submit(() -> {
                for (int m = 0; m < 10; m++) {
                    send();
                    counter.incrementAndGet();
                }
                return null;
            });
        }


        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);
        Assert.assertEquals(2 * NB_THREAD * 10, counter.get());
        Assert.assertEquals(2 * MAX_CONNECTION, smtpConnectionPool.getCreatedCount());
    }

}
