package com.senyu.consumer.mq;

import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 构建简单的生产者, 发送消息
 */
public class FooConsumer {

    private static final Logger log = LoggerFactory.getLogger(FooConsumer.class);

    public static void main(String[] args) throws IOException, TimeoutException {
        // 1. 创建连接工厂以及相关的参数配置
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("23.95.16.6");
        factory.setPort(5672);
        factory.setVirtualHost("/");
        factory.setUsername("guest");
        factory.setPassword("guest");
        // 2. 通过工厂创建连接 Connection
        Connection connection = factory.newConnection();
        // 3. 创建管道
        Channel channel = connection.createChannel();
        // 4. 创建队列 Queue
        channel.queueDeclare("hello", true, false, false, null);
        // 5. 对队列进行监听消费
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                log.info("consumer tag: {}, body: {}", consumerTag, new String(body));
                log.info("envelope: {}", envelope);
                log.info("properties: {}", properties);
            }
        };
        channel.basicConsume("hello", true, consumer);
    }

}
