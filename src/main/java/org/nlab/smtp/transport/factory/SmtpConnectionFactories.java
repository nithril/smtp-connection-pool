package org.nlab.smtp.transport.factory;

import javax.mail.Session;
import java.util.Properties;

import static org.nlab.smtp.transport.strategy.ConnectionStrategy.newConnectionStrategy;
import static org.nlab.smtp.transport.strategy.TransportStrategy.newSessiontStrategy;


public final class SmtpConnectionFactories {

    private SmtpConnectionFactories() {
    }

    public static SmtpConnectionFactory newSmtpFactory(){
        return new SmtpConnectionFactory(Session.getInstance(new Properties()), newSessiontStrategy() , newConnectionStrategy());
    }

    public static SmtpConnectionFactory newSmtpFactory(Session session){
        return new SmtpConnectionFactory(session, newSessiontStrategy() , newConnectionStrategy());
    }


}