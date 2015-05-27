package org.nlab.smtp.transport;

import org.nlab.smtp.pool.ObjectPoolAware;
import org.nlab.smtp.pool.SmtpConnectionPool;

import javax.mail.*;
import javax.mail.event.TransportListener;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by nlabrot on 30/04/15.
 */
public class DefaultClosableSmtpConnection implements ClosableSmtpConnection, ObjectPoolAware<ClosableSmtpConnection> {

    private final Transport delegate;
    private SmtpConnectionPool objectPool;

    private final LinkedBlockingQueue<TransportListener> transportListeners = new LinkedBlockingQueue<>();

    public DefaultClosableSmtpConnection(Transport delegate) {
        this.delegate = delegate;
    }

    public void sendMessage(Message msg, Address[] addresses)
            throws MessagingException{
        delegate.sendMessage(msg, addresses);
    }

    public void addTransportListener(TransportListener l){
        transportListeners.add(l);
        delegate.addTransportListener(l);
    }

    public void removeTransportListener(TransportListener l){
        transportListeners.remove(l);
        delegate.removeTransportListener(l);
    }

    public void clearListeners(){
        for (TransportListener transportListener : transportListeners) {
            delegate.removeTransportListener(transportListener);
        }
        transportListeners.clear();
    }

    public boolean isConnected(){
        return delegate.isConnected();
    }


    @Override
    public void close() throws Exception {
        objectPool.returnObject(this);
    }

    @Override
    public SmtpConnectionPool getObjectPool() {
        return objectPool;
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
}
