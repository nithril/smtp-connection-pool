package org.nlab.smtp.store;

import com.dumbster.smtp.MailMessage;
import com.dumbster.smtp.MailStore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by nlabrot on 01/05/15.
 */
public class PersistentMailStore implements MailStore {

    private List<MailMessage> mailMessages = Collections.synchronizedList(new ArrayList<MailMessage>());

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
        return mailMessages.get(index);
    }

    @Override
    public void clearMessages() {
        mailMessages.clear();
    }
}
