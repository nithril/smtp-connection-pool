package org.nlab.smtp.transport.strategy;

import javax.mail.*;

/**
 * Created by nlabrot on 04/06/15.
 */
@FunctionalInterface
public interface TransportStrategy {

    Transport getTransport(Session session) throws NoSuchProviderException;


    static TransportStrategy newSessiontStrategy() {
        return session -> session.getTransport();
    }

    static TransportStrategy newProtocolStrategy(String protocol) {
        return session -> session.getTransport(protocol);
    }

    static TransportStrategy newUrlNameStrategy(URLName urlName) {
        return session -> session.getTransport(urlName);
    }

    static TransportStrategy newAddressStrategy(Address address) {
        return session -> session.getTransport(address);
    }

    static TransportStrategy newProviderStrategy(Provider provider) {
        return session -> session.getTransport(provider);
    }
}
