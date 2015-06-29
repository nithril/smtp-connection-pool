package org.nlab.smtp;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by nlabrot on 29/04/15.
 */
public class TestSendMailBench extends AbstractTest {

    private static final Logger LOG = LoggerFactory.getLogger(TestSendMailBench.class);

    public static final int NB_THREAD = 100;


    @Test
    @Ignore
    public void testSend() throws Exception {
        final AtomicInteger counter = new AtomicInteger();

        ExecutorService executorService = Executors.newFixedThreadPool(NB_THREAD);

        for (int i = 0; i < NB_THREAD; i++) {
            executorService.submit(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    for (int m = 0; m < 20000; m++) {
                        TestSendMailBench.this.send();
                        counter.incrementAndGet();

                        if (counter.get() % 1000 == 0) {
                            System.out.println(counter.get());
                        }
                    }
                    return null;
                }
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(1000, TimeUnit.SECONDS);
        Assert.assertEquals(NB_THREAD * 200, counter.get());
        Assert.assertEquals(8, smtpConnectionPool.getCreatedCount());
    }




}
