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
  private boolean invalidateConnectionOnException;
  private boolean invalidateConnectionOnClose;

  private final List<TransportListener> transportListeners = new ArrayList<>();

  public DefaultClosableSmtpConnection(Transport delegate, boolean invalidateConnectionOnException) {
    this.delegate = delegate;
    this.invalidateConnectionOnException = invalidateConnectionOnException;
  }

  @Override
  public void invalidate() {
    invalidateConnectionOnClose = true;
  }

  @Override
  public void setInvalidateConnectionOnClose(boolean invalidateConnectionOnClose) {
    this.invalidateConnectionOnClose = invalidateConnectionOnClose;
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
    if (!invalidateConnectionOnClose) {
      objectPool.returnObject(this);
    } else {
      try {
        objectPool.invalidateObject(this);
      } catch (Exception e) {
        LOG.error("Failed to invalidate object in the pool", e);
      }
    }
  }

  @Override
  public void setObjectPool(SmtpConnectionPool objectPool) {
    this.objectPool = objectPool;
  }

  @Override
  public SmtpConnectionPool getObjectPool() {
    return objectPool;
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

    try {
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
    } catch (Exception e) {
      // TODO: An exception can be sent because the recipient is invalid, ie. not because the connection is invalid
      // TODO: Invalidate based on the MessagingException subclass / cause: IOException
      if (invalidateConnectionOnException) {
        invalidate();
      }
      throw e;
    }
  }


  private void doSend(MimeMessage... mimeMessages) throws MailSendException {
    Map<Object, Exception> failedMessages = new LinkedHashMap<>();

    for (MimeMessage mimeMessage : mimeMessages) {

      // Send message via current transport...
      try {
        // doSend takes care to invalidate the connection if needed
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
