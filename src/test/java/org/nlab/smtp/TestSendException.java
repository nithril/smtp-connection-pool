package org.nlab.smtp;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.junit.Assert;
import org.junit.Test;
import org.nlab.smtp.exception.MailSendException;
import org.nlab.smtp.pool.SmtpConnectionPool;
import org.nlab.smtp.transport.connection.ClosableSmtpConnection;
import org.nlab.smtp.transport.factory.SmtpConnectionFactoryBuilder;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

public class TestSendException extends AbstractTest {
  @Test
  public void testReturnedOnException() throws Exception {
    try (ClosableSmtpConnection connection = smtpConnectionPool.borrowObject()) {
      MimeMessage mimeMessage = new MimeMessage(connection.getSession());
      MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
      mimeMessageHelper.addTo("nithril@example.com");
      mimeMessageHelper.setFrom("nithril@example.com");
      mimeMessageHelper.setSubject("foo");
      mimeMessageHelper.setText("example");
      // We stop the server before we actually send the message
      stopServer();
      connection.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
      Assert.fail("The connection should fail since the server is stopped");
    } catch (MailSendException | MessagingException e) {
      // It should come here, but the connection should not be returned in the pool
    }
    Assert.assertEquals(1, smtpConnectionPool.getBorrowedCount());
    Assert.assertEquals(0, smtpConnectionPool.getDestroyedCount());
    Assert.assertEquals(1, smtpConnectionPool.getReturnedCount());
  }

  @Test
  public void testInvalidateOnException() throws Exception {
    GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
    genericObjectPoolConfig.setMaxTotal(getMaxTotalConnection());
    genericObjectPoolConfig.setTestOnBorrow(true);

    // We need to instantiate a new factory and pool to set the flag on the factory
    transportFactory = SmtpConnectionFactoryBuilder.newSmtpBuilder().port(PORT).invalidateConnectionOnException(true).build();
    smtpConnectionPool = new SmtpConnectionPool(transportFactory, genericObjectPoolConfig);

    try (ClosableSmtpConnection connection = smtpConnectionPool.borrowObject()) {
      MimeMessage mimeMessage = new MimeMessage(connection.getSession());
      MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
      mimeMessageHelper.addTo("nithril@example.com");
      mimeMessageHelper.setFrom("nithril@example.com");
      mimeMessageHelper.setSubject("foo");
      mimeMessageHelper.setText("example");
      // We stop the server before we actually send the message
      stopServer();
      connection.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
      Assert.fail("The connection should fail since the server is stopped");
    } catch (MailSendException | MessagingException e) {
      // It should come here, but the connection should not be returned in the pool
    }
    Assert.assertEquals(1, smtpConnectionPool.getBorrowedCount());
    Assert.assertEquals(1, smtpConnectionPool.getDestroyedCount());
    Assert.assertEquals(0, smtpConnectionPool.getReturnedCount());
  }
}
