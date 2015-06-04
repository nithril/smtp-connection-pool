package org.nlab.smtp.transport.connection;

import javax.mail.*;
import javax.mail.event.TransportListener;

/**
 * Created by nlabrot on 30/04/15.
 */
public interface ClosableSmtpConnection extends AutoCloseable {

    void sendMessage(Message msg, Address[] addresses) throws MessagingException;

    boolean isConnected();

    void addTransportListener(TransportListener l);

    void removeTransportListener(TransportListener l);

    void clearListeners();

    Transport getDelegate();

    Session getSession();
}
