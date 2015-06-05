package org.nlab.smtp.transport.strategy;

import javax.mail.MessagingException;
import javax.mail.Transport;

/**
 * {@link Transport} supports actually 4 types of connections which are handled by this connection strategy
 * <ol>
 *     <li>{@link Transport#connect()} =&gt; {@link  #newConnectionStrategy()} </li>
 *     <li>{@link Transport#connect(String, String)} ()} =&gt; {@link  #newConnectionStrategy(String, String)} </li>
 *     <li>{@link Transport#connect(String, String, String)} ()} =&gt; {@link  #newConnectionStrategy(String, String, String)} </li>
 *     <li>{@link Transport#connect(String, int, String, String)} ()} =&gt; {@link  #newConnectionStrategy(String, int, String, String)} </li>
 * </ol>
 *
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

    static ConnectionStrategy newConnectionStrategy(String host, String username, String password) {
        return t -> t.connect(host, username, password);
    }

    static ConnectionStrategy newConnectionStrategy(String host, int port, String username, String password) {
        return t -> t.connect(host, port, username, password);
    }
}
