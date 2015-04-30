package org.nlab.smtp.transport;

import javax.mail.Authenticator;
import javax.mail.Session;
import javax.mail.event.TransportListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * A part of the code of this class is taken from the Spring <a href="http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/mail/javamail/JavaMailSenderImpl.html">JavaMailSenderImpl class</a>.
 */
public class SmtpConnectionFactoryBuilder {

    Session session = Session.getDefaultInstance(new Properties());
    String protocol = "smtp";
    String host = "localhost";
    int port = 25;
    String username;
    String password;

    List<TransportListener> defaultTransportListeners = Collections.emptyList();

    private SmtpConnectionFactoryBuilder() {
    }

    public static SmtpConnectionFactoryBuilder newSmtpBuilder() {
        return new SmtpConnectionFactoryBuilder();
    }

    public SmtpConnectionFactoryBuilder session(Properties properties) {
        this.session = Session.getInstance(properties);
        return this;
    }

    public SmtpConnectionFactoryBuilder session(Properties properties, Authenticator authenticator) {
        this.session = Session.getInstance(properties, authenticator);
        return this;
    }

    public SmtpConnectionFactoryBuilder protocol(String protocol) {
        this.protocol = protocol;
        return this;
    }

    public SmtpConnectionFactoryBuilder host(String host) {
        this.host = host;
        return this;
    }

    public SmtpConnectionFactoryBuilder port(int port) {
        this.port = port;
        return this;
    }

    public SmtpConnectionFactoryBuilder username(String username) {
        this.username = username;
        return this;
    }

    public SmtpConnectionFactoryBuilder password(String password) {
        this.password = password;
        return this;
    }

    public SmtpConnectionFactoryBuilder defaultTransportListeners(TransportListener... listeners){
        defaultTransportListeners = Arrays.asList(listeners);
        return this;
    }

    public SmtpConnectionFactory build() {
        return new SmtpConnectionFactory(this);
    }


}