package org.nlab.smtp.transport.strategy;

import javax.mail.Address;
import javax.mail.NoSuchProviderException;
import javax.mail.Provider;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.URLName;

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
public class TransportStrategyFactory {

    public static TransportStrategy newSessiontStrategy() {
        return new TransportStrategy(){
            @Override
            public Transport getTransport(Session session) throws NoSuchProviderException {
                return session.getTransport();
            }
        };
    }

    public static TransportStrategy newProtocolStrategy(final String protocol) {
        return new TransportStrategy(){
            @Override
            public Transport getTransport(Session session) throws NoSuchProviderException {
                return session.getTransport(protocol);
            }
        };
    }

    public static TransportStrategy newUrlNameStrategy(final URLName urlName) {
        return new TransportStrategy(){
            @Override
            public Transport getTransport(Session session) throws NoSuchProviderException {
                return session.getTransport(urlName);
            }
        };
    }

    public static TransportStrategy newAddressStrategy(final Address address) {
        return new TransportStrategy(){
            @Override
            public Transport getTransport(Session session) throws NoSuchProviderException {
                return session.getTransport(address);
            }
        };
    }

    public static TransportStrategy newProviderStrategy(final Provider provider) {
        return new TransportStrategy(){
            @Override
            public Transport getTransport(Session session) throws NoSuchProviderException {
                return session.getTransport(provider);
            }
        };
    }

}
