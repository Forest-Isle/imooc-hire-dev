package com.senyu.producer.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 构建简单的生产者, 发送消息
 */
public class FooProducer {

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
        // 5. 向队列发送消息
        /*
        *   routingKey: 路由key，映射路径，如果没有交换机, 则路由key和队列名保持一致
        * */
        channel.basicPublish("", "hello", null, ("Hello World").getBytes());

        // 6. 释放资源
        channel.close();
        connection.close();
    }

}
