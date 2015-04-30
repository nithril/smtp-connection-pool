package org.nlab.smtp.transport;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.event.TransportListener;
import java.util.Deque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * A part of the code of this class is taken from the Spring <a href="http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/mail/javamail/JavaMailSenderImpl.html">JavaMailSenderImpl class</a>.
 */
public class SmtpConnectionFactory implements PooledObjectFactory<ClosableSmtpConnection> {

    private static final Logger LOG = LoggerFactory.getLogger(SmtpConnectionFactory.class);

    protected final Session session;
    protected final String protocol;
    protected final String host;
    private final int port;
    private final String username;
    private final String password;

    private Deque<TransportListener> defaultTransportListeners;

    protected SmtpConnectionFactory(SmtpConnectionFactoryBuilder builder) {
        this.session = builder.session;
        this.protocol = builder.protocol;
        this.host = builder.host;
        this.port = builder.port;
        this.username = builder.username != null && builder.username.length() > 0 ? builder.username : null;
        this.password = builder.password != null && builder.password.length() > 0 ? builder.password : null;
        this.defaultTransportListeners = new LinkedBlockingDeque<>(builder.defaultTransportListeners);
    }

    public SmtpConnectionFactory(Session session, String protocol, String host, int port, String username, String password, Deque<TransportListener> defaultTransportListeners) {
        this.session = session;
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.username = username != null && username.length() > 0 ? username : null;
        this.password = password != null && password.length() > 0 ? password : null;
        this.defaultTransportListeners = defaultTransportListeners;
    }


    @Override
    public PooledObject<ClosableSmtpConnection> makeObject() throws Exception {
        LOG.debug("makeObject");

        Transport transport = getTransport(getSession());
        transport.connect(getHost(), getPort(), username, password);

        DefaultClosableSmtpConnection closableSmtpTransport = new DefaultClosableSmtpConnection(transport);
        initDefaultListeners(closableSmtpTransport);

        return new DefaultPooledObject(closableSmtpTransport);
    }

    @Override
    public void destroyObject(PooledObject<ClosableSmtpConnection> pooledObject) throws Exception {
        try {
            LOG.debug("destroyObject [{}]", pooledObject.getObject().isConnected());
            clearListeners(pooledObject.getObject());
            pooledObject.getObject().getDelegate().close();
        }catch (Exception e){
            LOG.warn(e.getMessage() , e);
        }
    }

    @Override
    public boolean validateObject(PooledObject<ClosableSmtpConnection> pooledObject) {
        LOG.debug("Is connected [{}]", pooledObject.getObject().isConnected());
        return pooledObject.getObject().isConnected();
    }

    @Override
    public void activateObject(PooledObject<ClosableSmtpConnection> pooledObject) throws Exception {
        LOG.debug("activateObject [{}]", pooledObject.getObject().isConnected());
        if (!pooledObject.getObject().isConnected()){
            throw new Exception("Transport is not connected");
        }
        initDefaultListeners(pooledObject.getObject());
    }

    @Override
    public void passivateObject(PooledObject<ClosableSmtpConnection> pooledObject) throws Exception {
        LOG.debug("passivateObject [{}]", pooledObject.getObject().isConnected());
        clearListeners(pooledObject.getObject());
    }

    protected Transport getTransport(Session session) throws NoSuchProviderException {
        return session.getTransport(protocol);
    }


    private void clearListeners(ClosableSmtpConnection transport){
        transport.clearListeners();
    }

    private void initDefaultListeners(ClosableSmtpConnection smtpTransport) {
        for (TransportListener transportListener : defaultTransportListeners) {
            smtpTransport.addTransportListener(transportListener);
        }
    }

    public void addTransportListener(TransportListener l){
        defaultTransportListeners.add(l);
    }

    public void removeTransportListener(TransportListener l){
        defaultTransportListeners.remove(l);
    }

    public void clearListeners(){
        defaultTransportListeners.clear();
    }

    public Session getSession() {
        return session;
    }


    public String getProtocol() {
        return protocol;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}