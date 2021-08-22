package org.nlab.smtp;

import com.icegreen.greenmail.store.StoredMessage;
import jakarta.mail.internet.MimeMessage;
import org.junit.Assert;
import org.junit.Test;
import org.nlab.smtp.transport.connection.ClosableSmtpConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by nlabrot on 29/04/15.
 */
public class TestSendMail extends AbstractTest {

    private static final Logger LOG = LoggerFactory.getLogger(TestSendMail.class);

    public static final int NB_THREAD = 10;


    @Test
    public void testSend() throws Exception {
        MimeMessage mimeMessage = send();
        waitForMessagesCount(1);
        List<StoredMessage> allMessages = getImapHostManager().getAllMessages();
        Assert.assertEquals(1, allMessages.size());
        Assert.assertArrayEquals(allMessages.get(0).getMimeMessage().getAllRecipients(), mimeMessage.getAllRecipients());
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
                        send();
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

        waitForMessagesCount(NB_THREAD * 200);
        Assert.assertEquals(NB_THREAD * 200, getImapHostManager().getAllMessages().size());

    }


    @Test
    public void testSendAfterServerStopStart() throws Exception {
        final AtomicInteger counter = new AtomicInteger();

        ExecutorService executorService = Executors.newFixedThreadPool(1000);

        final CountDownLatch countDownLatch = new CountDownLatch(10);


        for (int i = 0; i < NB_THREAD; i++) {
            executorService.submit(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    for (int m = 0; m < 10; m++) {
                        send();
                        counter.incrementAndGet();
                    }
                    countDownLatch.countDown();
                    return null;
                }
            });
        }

        countDownLatch.await();

        waitForMessagesCount(NB_THREAD * 10);
        Assert.assertEquals(NB_THREAD * 10, greenMail.getReceivedMessages().length);


        stopServer();
        startServer();

        for (int i = 0; i < NB_THREAD; i++) {
            executorService.submit(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    for (int m = 0; m < 10; m++) {
                        send();
                        counter.incrementAndGet();
                    }
                    return null;
                }
            });
        }

        waitForMessagesCount(NB_THREAD * 10);
        Assert.assertEquals(NB_THREAD * 10, greenMail.getReceivedMessages().length);

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);
        Assert.assertEquals(2 * NB_THREAD * 10, counter.get());

    }

    @Test
    public void testSend_Batch() throws Exception {
        try (ClosableSmtpConnection connection = smtpConnectionPool.borrowObject()) {

            MimeMessage mimeMessage1 = createMessage(connection.getSession(), "foo1@example.com", "foo@example.com", "foo", "example");
            MimeMessage mimeMessage2 = createMessage(connection.getSession(), "foo2@example.com", "foo@example.com", "foo", "example");

            connection.sendMessages(mimeMessage1, mimeMessage2);

            waitForMessagesCount(2);

            List<StoredMessage> allMessages = getImapHostManager().getAllMessages();
            Assert.assertEquals(2, allMessages.size());

            Assert.assertArrayEquals(allMessages.get(0).getMimeMessage().getAllRecipients(), mimeMessage1.getAllRecipients());
            Assert.assertArrayEquals(allMessages.get(1).getMimeMessage().getAllRecipients(), mimeMessage2.getAllRecipients());
        }
    }
}
