package org.nlab.smtp.transport;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
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
}
