package org.nlab.smtp;

import com.google.common.base.Stopwatch;
import com.icegreen.greenmail.imap.ImapHostManager;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.junit.After;
import org.junit.Before;
import org.nlab.smtp.pool.SmtpConnectionPool;
import org.nlab.smtp.transport.connection.ClosableSmtpConnection;
import org.nlab.smtp.transport.factory.SmtpConnectionFactory;
import org.nlab.smtp.transport.factory.SmtpConnectionFactoryBuilder;

import java.util.concurrent.TimeUnit;

/**
 * Created by nlabrot on 01/05/15.
 */
public class AbstractTest {

    public static final int PORT = 3025;
    public static final int MAX_CONNECTION = 8;

    protected SmtpConnectionPool smtpConnectionPool;
    protected SmtpConnectionFactory transportFactory;

    static {
        //System.setProperty("org.slf4j.simpleLogger.defaultLogLevel" , "debug");
    }

    protected GreenMail greenMail;


    public int getMaxTotalConnection() {
        return MAX_CONNECTION;
    }


    @Before
    public void init() {
        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setMaxTotal(getMaxTotalConnection());
        genericObjectPoolConfig.setTestOnBorrow(true);

        genericObjectPoolConfig.setMinIdle(0);
        genericObjectPoolConfig.setTimeBetweenEvictionRunsMillis(1000);


        transportFactory = SmtpConnectionFactoryBuilder.newSmtpBuilder().port(PORT).build();
        smtpConnectionPool = new SmtpConnectionPool(transportFactory, genericObjectPoolConfig);


        startServer();
    }

    @After
    public void release() {
        smtpConnectionPool.close();
        stopServer();
    }

    protected void startServer() {
        greenMail = new GreenMail(ServerSetupTest.SMTP);
        greenMail.start();
    }

    protected void stopServer() {
        greenMail.stop();
    }

    protected MimeMessage send() throws Exception {
        try (ClosableSmtpConnection connection = smtpConnectionPool.borrowObject()) {
            MimeMessage mimeMessage = createMessage(connection.getSession(), "nithril@example.com", "nithril@example.com", "foo", "example");
            connection.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
            return mimeMessage;
        }
    }

    protected MimeMessage createMessage(Session session, String to, String from, String subject, String text) throws Exception {
        MimeMessage message = new MimeMessage(session);
        message.addRecipients(Message.RecipientType.TO, to);
        message.setFrom(from);
        message.setSubject(subject);
        message.setText(text);
        return message;
    }

    protected void waitForMessagesCount(int count) throws InterruptedException {
        Stopwatch stopwatch = Stopwatch.createStarted();
        while (getImapHostManager().getAllMessages().size() < count
                && stopwatch.elapsed(TimeUnit.MILLISECONDS) < 500) {
            Thread.sleep(50l);
        }
    }

    protected ImapHostManager getImapHostManager() {
        return greenMail.getManagers().getImapHostManager();
    }
}
