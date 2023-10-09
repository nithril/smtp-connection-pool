package org.nlab.smtp.transport.strategy;

import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;

/**
 * Connection strategy that abstract {@link Session#getTransport}
 * <p>
 * <p>
 * Created by nlabrot on 04/06/15.
 */
public interface TransportStrategy {

    Transport getTransport(Session session) throws NoSuchProviderException;

}
