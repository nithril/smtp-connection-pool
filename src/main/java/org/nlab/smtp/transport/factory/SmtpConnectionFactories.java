package org.nlab.smtp.transport.factory;

import jakarta.mail.Session;

import java.util.Properties;

import static org.nlab.smtp.transport.strategy.ConnectionStrategyFactory.newConnectionStrategy;
import static org.nlab.smtp.transport.strategy.TransportStrategyFactory.newSessiontStrategy;

/**
 * {@link SmtpConnectionFactory} factory
 */
public final class SmtpConnectionFactories {

    private SmtpConnectionFactories() {
    }

    /**
     * Initialize the {@link SmtpConnectionFactory} with a
     * {@link Session} initialized to {@code Session.getInstance(new Properties())},
     * {@link org.nlab.smtp.transport.strategy.TransportStrategyFactory#newSessiontStrategy},
     * {@link org.nlab.smtp.transport.strategy.ConnectionStrategyFactory#newConnectionStrategy}
     *
     * @return
     */
    public static SmtpConnectionFactory newSmtpFactory() {
        return new SmtpConnectionFactory(Session.getInstance(new Properties()), newSessiontStrategy(), newConnectionStrategy(), false);
    }

    /**
     * Initialize the {@link SmtpConnectionFactory} using the provided
     * {@link Session} and
     * {@link org.nlab.smtp.transport.strategy.TransportStrategyFactory#newSessiontStrategy},
     * {@link org.nlab.smtp.transport.strategy.ConnectionStrategyFactory#newConnectionStrategy}
     *
     * @param session
     * @return
     */
    public static SmtpConnectionFactory newSmtpFactory(Session session) {
        return new SmtpConnectionFactory(session, newSessiontStrategy(), newConnectionStrategy(), false);
    }


}