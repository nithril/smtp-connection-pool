package org.nlab.smtp.transport.strategy;

import javax.mail.MessagingException;
import javax.mail.Transport;

/**
 * Connection strategy that abstract {@link Transport#connect}
 *
 * Created by nlabrot on 04/06/15.
 */
public interface ConnectionStrategy {

    void connect(Transport transport) throws MessagingException;

}
