package org.nlab.smtp;

import com.google.common.base.Stopwatch;
import com.icegreen.greenmail.imap.ImapHostManager;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.junit.After;
import org.junit.Before;
import org.nlab.smtp.pool.SmtpConnectionPool;
import org.nlab.smtp.transport.connection.ClosableSmtpConnection;
import org.nlab.smtp.transport.factory.SmtpConnectionFactory;
import org.nlab.smtp.transport.factory.SmtpConnectionFactoryBuilder;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.util.concurrent.TimeUnit;
import javax.mail.internet.MimeMessage;

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

  protected void waitForMessagesCount(int count) throws InterruptedException {
    Stopwatch stopwatch = Stopwatch.createStarted();
    while (getImapHostManager().getAllMessages().size() < count
        && stopwatch.elapsed(TimeUnit.MILLISECONDS) < 500) {
      Thread.sleep(50l);
    }
  }

  protected ImapHostManager getImapHostManager(){
    return greenMail.getManagers().getImapHostManager();
  }


}
