package com.oracle.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * RabbitMQ配置类
 * 延迟队列
 **/
@Configuration
public class RabbitMqConfig {

    public static final String DELAY_EXCHANGE_NAME = "delayed_exchange";
    public static final String DELAY_QUEUE_NAME = "delay_queue_name";
    public static final String DELAY_ROUTING_KEY = "delay_routing_key";

    @Bean
    public CustomExchange delayExchange()
    {
        Map<String, Object> args = new HashMap<>();
        args.put("x-delayed-type", "direct");
        return new CustomExchange(DELAY_EXCHANGE_NAME, "x-delayed-message", true, false, args);
    }

    @Bean
    public Queue queue()
    {
        return new Queue(DELAY_QUEUE_NAME, true);
    }

    @Bean
    public Binding binding(Queue queue, CustomExchange delayExchange)
    {
        return BindingBuilder.bind(queue).to(delayExchange).with(DELAY_ROUTING_KEY).noargs();
    }

}
