package org.nlab.smtp.store;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import org.apache.commons.collections.CollectionUtils;

import com.dumbster.smtp.MailMessage;
import com.dumbster.smtp.MailStore;


/**
 * Created by nlabrot on 01/05/15.
 */
public class PersistentMailStore implements MailStore {

    private BlockingDeque<MailMessage> mailMessages = new LinkedBlockingDeque<>();

    @Override
    public int getEmailCount() {
        return mailMessages.size();
    }

    @Override
    public void addMessage(MailMessage message) {
        mailMessages.add(message);
    }

    @Override
    public MailMessage[] getMessages() {
        return mailMessages.toArray(new MailMessage[]{});
    }

    @Override
    public MailMessage getMessage(int index) {
        return (MailMessage)CollectionUtils.get(mailMessages , index);
    }

    @Override
    public void clearMessages() {
        mailMessages.clear();
    }

    public BlockingDeque<MailMessage> getMailMessages() {
        return mailMessages;
    }
}
