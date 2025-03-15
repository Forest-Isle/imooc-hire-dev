package com.senyu.common.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
public class SMSUtil {

    @Autowired
    private JavaMailSender mailSender;

    private final String senderEmail = "3113952763@qq.com"; // 发件人邮箱

    // 发送邮件
    public void sendEmail(String to, String code) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(senderEmail);
        helper.setTo(to);
        helper.setSubject("验证码");
        helper.setText("您的验证码是：" + code + "，有效期 5 分钟，请勿泄露。");
        mailSender.send(message);
    }
}
