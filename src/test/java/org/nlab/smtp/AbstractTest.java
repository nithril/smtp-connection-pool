package org.nlab.smtp;

import com.dumbster.smtp.ServerOptions;
import com.dumbster.smtp.SmtpServer;
import com.dumbster.smtp.SmtpServerFactory;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.junit.After;
import org.junit.Before;
import org.nlab.smtp.pool.SmtpConnectionPool;
import org.nlab.smtp.store.PersistentMailStore;
import org.nlab.smtp.transport.ClosableSmtpConnection;
import org.nlab.smtp.transport.SmtpConnectionFactory;
import org.nlab.smtp.transport.SmtpConnectionFactoryBuilder;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.internet.MimeMessage;

/**
 * Created by nlabrot on 01/05/15.
 */
public class AbstractTest {

    public static final int PORT = 2525;
    public static final int MAX_CONNECTION = 8;

    protected SmtpConnectionPool smtpConnectionPool;
    protected SmtpConnectionFactory transportFactory;

    protected SmtpServer server;

    static {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel" , "debug");
    }


    public int getMaxTotalConnection(){
        return MAX_CONNECTION;
    }


    @Before
    public void init() {
        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setMaxTotal(getMaxTotalConnection());

        transportFactory = SmtpConnectionFactoryBuilder.newSmtpBuilder().port(PORT).build();
        smtpConnectionPool = new SmtpConnectionPool(transportFactory, genericObjectPoolConfig);

        startServer();
    }

    @After
    public void release() {
        stopServer();
        smtpConnectionPool.close();
    }

    protected void startServer() {
        ServerOptions serverOptions = new ServerOptions();
        serverOptions.port = PORT;
        serverOptions.mailStore = new PersistentMailStore();
        server = SmtpServerFactory.startServer(serverOptions);
    }

    protected void stopServer() {
        server.stop();
    }


    protected void send() throws Exception {
        try (ClosableSmtpConnection connection = smtpConnectionPool.borrowObject()) {
            MimeMessage mimeMessage = new MimeMessage(connection.getSession());
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false);
            mimeMessageHelper.addTo("nithril@example.com");
            mimeMessageHelper.setFrom("nithril@example.com");
            mimeMessageHelper.setSubject("foo");
            mimeMessageHelper.setText("example", false);
            connection.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
        }
    }
}
