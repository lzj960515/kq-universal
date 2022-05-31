package com.kqinfo.universal.retry.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * 邮件工具
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
@Slf4j
public class MailUtil {

    @Resource
    private JavaMailSender javaMailSender;
    @Resource
    private MailProperties mailProperties;

    @Async
    public void sendEmail(String subject, String text, String[] to) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper;
        try {
            helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(mailProperties.getUsername());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);
            javaMailSender.send(mimeMessage);
            log.info("邮件发送成功：{}, 内容：{}", mailProperties.getUsername(), text);
        } catch (MessagingException e) {
            log.error("发送邮件失败 {}", e.getMessage(), e);
        }
    }

}
