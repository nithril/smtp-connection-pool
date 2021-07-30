package org.nlab.smtp.transport.strategy;

import jakarta.mail.MessagingException;
import jakarta.mail.Transport;

/**
 * Connection strategy that abstract {@link Transport#connect}
 * <p>
 * Created by nlabrot on 04/06/15.
 */
public interface ConnectionStrategy {

  void connect(Transport transport) throws MessagingException;

}
