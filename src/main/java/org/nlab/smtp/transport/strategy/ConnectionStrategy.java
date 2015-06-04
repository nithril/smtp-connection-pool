package org.nlab.smtp.transport.strategy;

import javax.mail.MessagingException;
import javax.mail.Transport;

/**
 * Created by nlabrot on 04/06/15.
 */
@FunctionalInterface
public interface ConnectionStrategy {

    void connect(Transport transport) throws MessagingException;

    static ConnectionStrategy newConnectionStrategy() {
        return t -> t.connect();
    }

    static ConnectionStrategy newConnectionStrategy(String username, String password) {
        return t -> t.connect(username, password);
    }

    static ConnectionStrategy newConnectionStrategy(String host, int port, String username, String password) {
        return t -> t.connect(host, port, username, password);
    }
}
