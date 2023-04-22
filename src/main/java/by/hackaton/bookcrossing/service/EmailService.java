package by.hackaton.bookcrossing.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;

@Service
public class EmailService {

    private static final String CONFIRM_EMAIL_TEMPLATE = "classpath:templates/ConfirmEmail.html";
    private static final String RESET_PASSWORD_TEMPLATE = "classpath:templates/ResetPassword.html";
    private ResourceLoader resourceLoader;

    @Autowired
    private JavaMailSender emailSender;

    public EmailService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public void sendFeedbackMessage(String from, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("a.maiseyenak@gmail.com");
        message.setTo("a.maiseyenak@gmail.com");
        message.setSubject(subject);
        message.setText("Message from: " + from + "\n\n" + text);
        emailSender.send(message);
    }

    public void confirmEmailMessage(String to, String link) {
        sendMessage(to, "Рэгістрацыя", CONFIRM_EMAIL_TEMPLATE, link);
    }

    public void resetPasswordMessage(String to, String link) {
        sendMessage(to, "Аднаўленне паролю", RESET_PASSWORD_TEMPLATE, link);
    }

    private void sendMessage(String to, String subject, String type, String link) {
        MimeMessagePreparator preparator = mimeMessage -> {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
            message.setFrom("a.maiseyenak@gmail.com");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(getTemplate(type).replace("{{link}}", link), true);
        };
        emailSender.send(preparator);
    }

    private String getTemplate(String type) {
        try {
            return FileCopyUtils.copyToString(new InputStreamReader(resourceLoader.getResource(type).getInputStream()));

        } catch (IOException ex) {
            ex.getMessage();
        }
        return "";
    }
}
