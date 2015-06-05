package org.nlab.smtp.transport.factory;

import org.nlab.smtp.transport.strategy.ConnectionStrategy;
import org.nlab.smtp.transport.strategy.TransportStrategy;

import javax.mail.Session;
import java.util.Properties;

import static org.nlab.smtp.transport.strategy.ConnectionStrategy.newConnectionStrategy;
import static org.nlab.smtp.transport.strategy.TransportStrategy.newSessiontStrategy;

/**
 * {@link SmtpConnectionFactory} factory
 */
public final class SmtpConnectionFactories {

    private SmtpConnectionFactories() {
    }

    /**
     * Initialize the {@link SmtpConnectionFactory} with a
     * {@link Session} initialized to {@code Session.getInstance(new Properties())},
     * {@link TransportStrategy#newSessiontStrategy},
     * {@link ConnectionStrategy#newConnectionStrategy}
     * @return
     */
    public static SmtpConnectionFactory newSmtpFactory(){
        return new SmtpConnectionFactory(Session.getInstance(new Properties()), newSessiontStrategy() , newConnectionStrategy());
    }

    /**
     * Initialize the {@link SmtpConnectionFactory} using the provided
     * {@link Session} and
     * {@link TransportStrategy#newSessiontStrategy},
     * {@link ConnectionStrategy#newConnectionStrategy}
     * @param session
     * @return
     */
    public static SmtpConnectionFactory newSmtpFactory(Session session){
        return new SmtpConnectionFactory(session, newSessiontStrategy() , newConnectionStrategy());
    }


}