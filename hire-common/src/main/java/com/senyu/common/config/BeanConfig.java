package com.senyu.common.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
@ComponentScan(basePackages = "com.senyu.common")
public class BeanConfig {

    Logger logger = LoggerFactory.getLogger(BeanConfig.class);

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.qq.com"); // 邮箱服务器
        mailSender.setPort(587);
        mailSender.setUsername("15816098262@qq.com");
        mailSender.setPassword("bkapksfmzkaqdfja");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true"); // 开启 debug 方便排查问题

        return mailSender;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);

        // 设置 ConfirmCallback
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                logger.info("消息成功发送到交换机！");
            } else {
                logger.error("消息发送到交换机失败，原因：{}", cause);
            }
        });

        // 设置 ReturnCallback（如果开启了 `mandatory`）
        rabbitTemplate.setReturnsCallback(returned -> {
            logger.error("消息路由到队列失败，Exchange: {}, RoutingKey: {}, ReplyText: {}",
                    returned.getExchange(), returned.getRoutingKey(), returned.getReplyText());
        });

        return rabbitTemplate;
    }

}
