package org.nlab.smtp.transport.factory;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.nlab.smtp.transport.connection.ClosableSmtpConnection;
import org.nlab.smtp.transport.connection.DefaultClosableSmtpConnection;
import org.nlab.smtp.transport.strategy.ConnectionStrategy;
import org.nlab.smtp.transport.strategy.TransportStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.event.TransportListener;

/**
 * A part of the code of this class is taken from the Spring <a href="http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/mail/javamail/JavaMailSenderImpl.html">JavaMailSenderImpl class</a>.
 */
public class SmtpConnectionFactory implements PooledObjectFactory<ClosableSmtpConnection> {

  private static final Logger LOG = LoggerFactory.getLogger(SmtpConnectionFactory.class);

  protected final Session session;

  protected final TransportStrategy transportFactory;
  protected final ConnectionStrategy connectionStrategy;

  protected Collection<TransportListener> defaultTransportListeners;

  public SmtpConnectionFactory(Session session, TransportStrategy transportStrategy, ConnectionStrategy connectionStrategy, Collection<TransportListener> defaultTransportListeners) {
    this.session = session;
    this.transportFactory = transportStrategy;
    this.connectionStrategy = connectionStrategy;
    this.defaultTransportListeners = new ArrayList<>(defaultTransportListeners);
  }

  public SmtpConnectionFactory(Session session, TransportStrategy transportFactory, ConnectionStrategy connectionStrategy) {
    this(session, transportFactory, connectionStrategy, Collections.<TransportListener>emptyList());
  }


  @Override
  public PooledObject<ClosableSmtpConnection> makeObject() throws Exception {
    LOG.debug("makeObject");

    Transport transport = transportFactory.getTransport(session);
    connectionStrategy.connect(transport);

    DefaultClosableSmtpConnection closableSmtpTransport = new DefaultClosableSmtpConnection(transport);
    initDefaultListeners(closableSmtpTransport);

    return new DefaultPooledObject(closableSmtpTransport);
  }

  @Override
  public void destroyObject(PooledObject<ClosableSmtpConnection> pooledObject) throws Exception {
    try {
      if (LOG.isDebugEnabled()) {
        LOG.debug("destroyObject [{}]", pooledObject.getObject().isConnected());
      }
      pooledObject.getObject().clearListeners();
      pooledObject.getObject().getDelegate().close();
    } catch (Exception e) {
      LOG.warn(e.getMessage(), e);
    }
  }

  @Override
  public boolean validateObject(PooledObject<ClosableSmtpConnection> pooledObject) {
    boolean connected = pooledObject.getObject().isConnected();
    LOG.debug("Is connected [{}]", connected);
    return connected;
  }


  @Override
  public void activateObject(PooledObject<ClosableSmtpConnection> pooledObject) throws Exception {
    initDefaultListeners(pooledObject.getObject());
  }

  @Override
  public void passivateObject(PooledObject<ClosableSmtpConnection> pooledObject) throws Exception {
    if (LOG.isDebugEnabled()) {
      LOG.debug("passivateObject [{}]", pooledObject.getObject().isConnected());
    }
    pooledObject.getObject().clearListeners();
  }


  public void setDefaultListeners(Collection<TransportListener> listeners) {
    defaultTransportListeners = new CopyOnWriteArrayList<>(Objects.requireNonNull(listeners));
  }

  public List<TransportListener> getDefaultListeners() {
    return new ArrayList<>(defaultTransportListeners);
  }

  public Session getSession() {
    return session;
  }

  public TransportStrategy getTransportFactory() {
    return transportFactory;
  }

  public ConnectionStrategy getConnectionStrategy() {
    return connectionStrategy;
  }

  private void initDefaultListeners(ClosableSmtpConnection smtpTransport) {
    for (TransportListener transportListener : defaultTransportListeners) {
      smtpTransport.addTransportListener(transportListener);
    }
  }
}