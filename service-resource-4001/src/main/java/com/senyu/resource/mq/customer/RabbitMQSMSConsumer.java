package com.senyu.resource.mq.customer;

import com.rabbitmq.client.Channel;
import com.senyu.api.mq.RabbitMQSMSConfig;
import com.senyu.common.utils.SMSUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQSMSConsumer {

    private static final Logger log = LoggerFactory.getLogger(RabbitMQSMSConsumer.class);

    @Autowired
    private SMSUtil smsUtil;

//    @RabbitListener(bindings = @QueueBinding(exchange = @Exchange(value = RabbitMQSMSConfig.SMS_EXCHANGE, type = ExchangeTypes.TOPIC),
//                                    value = @Queue(RabbitMQSMSConfig.SMS_QUEUE),
//                                    key = RabbitMQSMSConfig.ROUTING_KEY_SMS_SEND_LOGIN))
//    public void watchQueue(String playLoad, Message message) {
//
//        String routingKey = message.getMessageProperties().getReceivedRoutingKey();
//        if (routingKey.equals(RabbitMQSMSConfig.ROUTING_KEY_SMS_SEND_LOGIN)) {
//            SMSContentQO smsContentQO = GsonUtils.stringToBean(playLoad, SMSContentQO.class);
//            try {
//                smsUtil.sendEmail(smsContentQO.getMobile(), smsContentQO.getContent());
//            } catch (MessagingException e) {
//                throw new RuntimeException(e);
//            }
//        }
//    }

    @RabbitListener(bindings = @QueueBinding(exchange = @Exchange(value = RabbitMQSMSConfig.SMS_EXCHANGE, type = ExchangeTypes.TOPIC),
            value = @Queue(RabbitMQSMSConfig.SMS_QUEUE),
            key = RabbitMQSMSConfig.ROUTING_KEY_SMS_SEND_LOGIN))
    public void watchQueue(Message message, Channel channel) throws Exception {

        try {
            String routingKey = message.getMessageProperties().getReceivedRoutingKey();
            byte[] body = message.getBody();
            String msg = new String(body);
            log.info("routingKey: {}, body: {}", routingKey, msg);

            /**
             * deliveryTag: 消息投递的标签
             * multiple: 批量确认所有消费者获得的消息
             */
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
        } catch (Exception e) {
            e.printStackTrace();
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
        }
    }
}
