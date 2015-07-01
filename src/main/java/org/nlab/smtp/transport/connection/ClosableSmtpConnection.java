package org.nlab.smtp.transport.connection;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.event.TransportListener;
import javax.mail.internet.MimeMessage;

import org.nlab.smtp.exception.MailSendException;

/**
 * Created by nlabrot on 30/04/15.
 */
public interface ClosableSmtpConnection extends AutoCloseable {

    String HEADER_MESSAGE_ID = "Message-ID";

    /**
     * Send a message to a list of recipients
     * @param msg
     * @param recipients
     * @throws MessagingException
     * @throws MailSendException
     */
    void sendMessage(MimeMessage msg, Address[] recipients) throws MessagingException, MailSendException;

    /**
     * Send a message. The list of recipients are taken from {@link MimeMessage#getAllRecipients()}
     * @param msg MimeMessage
     * @throws MessagingException
     */
    void sendMessage(MimeMessage msg) throws MessagingException;

    /**
     * Send the given array of JavaMail MIME messages in batch. Do not stop the batch when a message could not be sent
     * {@link MailSendException#getFailedMessages()} will contain the failed messages
     * @param msgs Array of MimeMessage
     * @throws MailSendException in case of failure when sending a message
     */
    void sendMessages(MimeMessage... msgs) throws MailSendException;

    /**
     * Test if the current connection is connected
     * @return
     */
    boolean isConnected();

    /**
     * Add a new {@link TransportListener}
     * @param l
     */
    void addTransportListener(TransportListener l);

    /**
     * Remove the provided {@link TransportListener}
     * @param l
     */
    void removeTransportListener(TransportListener l);

    /**
     * Clear the list of {@link TransportListener}
     */
    void clearListeners();

    /**
     *
     * @return the {@link Transport} associated to this connection
     */
    Transport getDelegate();

    /**
     *
     * @return the {@link Session}
     */
    Session getSession();
}
