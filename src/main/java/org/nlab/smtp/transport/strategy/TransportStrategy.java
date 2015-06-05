package org.nlab.smtp.transport.strategy;

import javax.mail.*;

/**
 * {@link Session} supports actually 4 types of get transport which are handled by this transport strategy
 * <ol>
 *     <li>{@link Session#getTransport()} =&gt; {@link  #newSessiontStrategy()} </li>
 *     <li>{@link Session#getTransport(String)} )} =&gt; {@link  #newProtocolStrategy(String)} </li>
 *     <li>{@link Session#getTransport(URLName)} ()} =&gt; {@link  #newUrlNameStrategy(URLName)} </li>
 *     <li>{@link Session#getTransport(Address)} =&gt; {@link  #newUrlNameStrategy(URLName)} </li>
 *     <li>{@link Session#getTransport(Provider)} =&gt; {@link  #newProviderStrategy(Provider)} </li>
 * </ol>
 *
 *
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
