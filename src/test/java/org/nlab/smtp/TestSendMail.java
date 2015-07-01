package org.nlab.smtp;

import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;
import org.nlab.smtp.transport.connection.ClosableSmtpConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.MimeMessageHelper;

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
        final AtomicInteger counter = new AtomicInteger();

        ExecutorService executorService = Executors.newFixedThreadPool(NB_THREAD);

        for (int i = 0; i < NB_THREAD; i++) {
            executorService.submit(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    for (int m = 0; m < 200; m++) {
                        TestSendMail.this.send();
                        counter.incrementAndGet();
                    }
                    return null;
                }
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);
        Assert.assertEquals(NB_THREAD * 200, counter.get());
        Assert.assertEquals(8, smtpConnectionPool.getCreatedCount());
    }


    @Test
    public void testSendAfterDeconnection() throws Exception {
        final AtomicInteger counter = new AtomicInteger();

        ExecutorService executorService = Executors.newFixedThreadPool(1000);

        final CountDownLatch countDownLatch = new CountDownLatch(10);


        for (int i = 0; i < NB_THREAD; i++) {
            executorService.submit(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    for (int m = 0; m < 10; m++) {
                        TestSendMail.this.send();
                        counter.incrementAndGet();
                    }
                    countDownLatch.countDown();
                    return null;
                }
            });
        }

        countDownLatch.await();

        stopServer();
        Thread.sleep(5000);
        startServer();

        for (int i = 0; i < NB_THREAD; i++) {
            executorService.submit(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    for (int m = 0; m < 10; m++) {
                        TestSendMail.this.send();
                        counter.incrementAndGet();
                    }
                    return null;
                }
            });
        }


        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);
        Assert.assertEquals(2 * NB_THREAD * 10, counter.get());
        Assert.assertEquals(2 * MAX_CONNECTION, smtpConnectionPool.getCreatedCount());
    }

    @Test
    public void testSend_Batch() throws Exception {

        try (ClosableSmtpConnection connection = smtpConnectionPool.borrowObject()) {

            List<MimeMessage> messages = new ArrayList<>();

            MimeMessage mimeMessage1 = new MimeMessage(connection.getSession());
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage1, false);
            mimeMessageHelper.addTo("foo@example.com");
            mimeMessageHelper.setFrom("foo@example.com");
            mimeMessageHelper.setSubject("foo");
            mimeMessageHelper.setText("example", false);
            messages.add(mimeMessage1);

            MimeMessage mimeMessage2 = new MimeMessage(connection.getSession());
            mimeMessageHelper = new MimeMessageHelper(mimeMessage2, false);
            mimeMessageHelper.addTo("foo@example.com");
            mimeMessageHelper.setFrom("foo@example.com");
            mimeMessageHelper.setSubject("foo");
            mimeMessageHelper.setText("example", false);
            messages.add(mimeMessage2);

            connection.sendMessages(mimeMessage1, mimeMessage2);

            Assert.assertNotNull(persistentMailStore.getMailMessages().poll(10, TimeUnit.SECONDS));
            Assert.assertNotNull(persistentMailStore.getMailMessages().poll(10, TimeUnit.SECONDS));

            Assert.assertEquals(0, server.getEmailCount());
        }
    }
}
