package org.nlab.smtp;

import jakarta.mail.Message;
import jakarta.mail.internet.MimeMessage;

import java.nio.charset.StandardCharsets;

/**
 * Created by Valentin Popov valentin@archiva.ru on 30.07.2021.
 */
public class MimeMessageHelper {

    private MimeMessage mimeMessage;

    public MimeMessageHelper(MimeMessage mimeMessage) {
        this.mimeMessage = mimeMessage;
    }

    public MimeMessageHelper addTo(String to) throws Exception {
        mimeMessage.addRecipients(Message.RecipientType.TO, to);
        mimeMessage.saveChanges();
        return this;
    }

    public MimeMessageHelper setFrom(String from) throws Exception {
        mimeMessage.setFrom(from);
        mimeMessage.saveChanges();
        return this;
    }

    public MimeMessageHelper setSubject(String subject) throws Exception {
        mimeMessage.setSubject(subject);
        mimeMessage.saveChanges();
        return this;
    }

    public MimeMessageHelper setText(String setText) throws Exception {
        mimeMessage.setText(setText, StandardCharsets.UTF_8.name());
        mimeMessage.saveChanges();
        return this;
    }
}
