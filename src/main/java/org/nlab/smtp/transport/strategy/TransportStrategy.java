package org.nlab.smtp.transport.strategy;

import javax.mail.*;

/**
 * Connection strategy that abstract {@link Session#getTransport}
 *
 *
 * Created by nlabrot on 04/06/15.
 */
public interface TransportStrategy {

    Transport getTransport(Session session) throws NoSuchProviderException;

}
