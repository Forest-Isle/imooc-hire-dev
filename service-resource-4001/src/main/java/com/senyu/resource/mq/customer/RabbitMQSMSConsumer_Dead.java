package com.senyu.resource.mq.customer;

import com.rabbitmq.client.Channel;
import com.senyu.api.mq.RabbitMQSMSConfig_Dead;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 死信队列监听
 */
@Component
public class RabbitMQSMSConsumer_Dead {

    private static final Logger log = LoggerFactory.getLogger(RabbitMQSMSConsumer_Dead.class);

    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange(value = RabbitMQSMSConfig_Dead.SMS_EXCHANGE_DEAD, type = ExchangeTypes.TOPIC),
            value = @Queue(RabbitMQSMSConfig_Dead.SMS_QUEUE_DEAD),
            key = RabbitMQSMSConfig_Dead.ROUTING_KEY_SMS_SEND_DEAD))
    public void receive(Message message, Channel channel) throws Exception {
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();
        log.info("routing key: {}", routingKey);

        String msg = new String(message.getBody());
        log.info("msg: {}", msg);

        channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
    }
}
