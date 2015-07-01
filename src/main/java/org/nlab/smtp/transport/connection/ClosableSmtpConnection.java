package org.nlab.smtp.transport.connection;

import org.nlab.smtp.exception.MailSendException;

import javax.mail.*;
import javax.mail.event.TransportListener;
import javax.mail.internet.MimeMessage;

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
     * @param msg
     * @throws MessagingException
     */
    void sendMessage(MimeMessage msg) throws MessagingException;

    /**
     * Send the given array of JavaMail MIME messages in batch. Do not stop the batch when a message could not be sent
     * {@link MailSendException#getFailedMessages()} will contain the failed messages
     * @param msgs
     * @throws MailSendException in case of failure when sending a message
     */
    void sendMessages(MimeMessage... msgs) throws MailSendException;

    /**
     *
     * @return
     */
    boolean isConnected();

    /**
     *
     * @param l
     */
    void addTransportListener(TransportListener l);

    /**
     *
     * @param l
     */
    void removeTransportListener(TransportListener l);

    /**
     *
     */
    void clearListeners();

    /**
     *
     * @return
     */
    Transport getDelegate();

    /**
     *
     * @return
     */
    Session getSession();
}
