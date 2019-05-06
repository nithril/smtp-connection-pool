package org.nlab.smtp.transport.connection;

import org.nlab.smtp.exception.MailSendException;
import org.nlab.smtp.pool.ObjectPoolAware;
import org.nlab.smtp.pool.SmtpConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.event.TransportListener;
import javax.mail.internet.MimeMessage;

/**
 * Created by nlabrot on 30/04/15.
 */
public class DefaultClosableSmtpConnection implements ClosableSmtpConnection, ObjectPoolAware {

  private static final Logger LOG = LoggerFactory.getLogger(DefaultClosableSmtpConnection.class);

  private final Transport delegate;
  private SmtpConnectionPool objectPool;
  private boolean shouldInvalidateOnClose;

  private final List<TransportListener> transportListeners = new ArrayList<>();

  public DefaultClosableSmtpConnection(Transport delegate) {
    this.delegate = delegate;
  }

  @Override
  public void invalidate() {
    shouldInvalidateOnClose = true;
  }

  @Override
  public void setInvalid(boolean invalid) {
    shouldInvalidateOnClose = invalid;
  }

  public void sendMessage(MimeMessage msg, Address[] recipients) throws MessagingException {
    doSend(msg, recipients);
  }

  public void sendMessage(MimeMessage msg) throws MessagingException {
    doSend(msg, msg.getAllRecipients());
  }

  public void sendMessages(MimeMessage... msgs) throws MailSendException {
    doSend(msgs);
  }

  public void addTransportListener(TransportListener l) {
    transportListeners.add(l);
    delegate.addTransportListener(l);
  }

  public void removeTransportListener(TransportListener l) {
    transportListeners.remove(l);
    delegate.removeTransportListener(l);
  }


  public void clearListeners() {
    for (TransportListener transportListener : transportListeners) {
      delegate.removeTransportListener(transportListener);
    }
    transportListeners.clear();
  }

  public boolean isConnected() {
    return delegate.isConnected();
  }


  @Override
  public void close() {
    if(!shouldInvalidateOnClose) {
      objectPool.returnObject(this);
    } else {
      try {
        objectPool.invalidateObject(this);
      } catch(Exception e) {
        LOG.error("Failed to invalidate object in the pool", e);
      }
    }
  }

  @Override
  public void setObjectPool(SmtpConnectionPool objectPool) {
    this.objectPool = objectPool;
  }

  @Override
  public Transport getDelegate() {
    return delegate;
  }

  @Override
  public Session getSession() {
    return objectPool.getSession();
  }


  private void doSend(MimeMessage mimeMessage, Address[] recipients) throws MessagingException {

    if (mimeMessage.getSentDate() == null) {
      mimeMessage.setSentDate(new Date());
    }
    String messageId = mimeMessage.getMessageID();
    mimeMessage.saveChanges();
    if (messageId != null) {
      // Preserve explicitly specified message id...
      mimeMessage.setHeader(HEADER_MESSAGE_ID, messageId);
    }
    delegate.sendMessage(mimeMessage, recipients);
  }


  private void doSend(MimeMessage... mimeMessages) throws MailSendException {
    Map<Object, Exception> failedMessages = new LinkedHashMap<>();

    for (MimeMessage mimeMessage : mimeMessages) {

      // Send message via current transport...
      try {
        doSend(mimeMessage, mimeMessage.getAllRecipients());
      } catch (Exception ex) {
        failedMessages.put(mimeMessage, ex);
      }
    }

    if (!failedMessages.isEmpty()) {
      throw new MailSendException(failedMessages);
    }
  }

}
