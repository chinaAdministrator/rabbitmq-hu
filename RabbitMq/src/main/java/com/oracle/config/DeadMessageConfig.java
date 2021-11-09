package com.oracle.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class DeadMessageConfig {


    //邮件队列名称
    final static String queue = "queue_demo4";

    //邮件交换机名称
    final static String exchangeName = "deom4Exchange";

    // routingKey
    final static String routingKey  = "keyDemo3";

    //死信消息队列名称
    final static String deal_queue = "deal_queue_demo4";

    //死信交换机名称
    final static String deal_exchangeName = "deal_deom4Exchange";

    //死信 routingKey
    final static String dead_RoutingKey  = "dead_routing_key";

    //死信队列 交换机标识符
    public static final String DEAD_LETTER_QUEUE_KEY = "x-dead-letter-exchange";

    //死信队列交换机绑定键标识符
    public static final String DEAD_LETTER_ROUTING_KEY = "x-dead-letter-routing-key";

    @Autowired
    private CachingConnectionFactory connectionFactory;

    //定义邮件队列(邮件队列 绑定一个死信交换机,并指定routing_key)
    @Bean
    public Queue queueDemo3() {
        // 将普通队列绑定到死信队列交换机上
        Map<String, Object> args = new HashMap<>(2);
        args.put(DEAD_LETTER_QUEUE_KEY, deal_exchangeName);
        args.put(DEAD_LETTER_ROUTING_KEY, dead_RoutingKey);
        return new Queue(DeadMessageConfig.queue, true, false, false, args);
    }

    //声明一个direct类型的交换机
    @Bean
    DirectExchange exchangeDemo3() {
        return new DirectExchange(DeadMessageConfig.exchangeName);
    }

    //绑定邮件Queue队列到交换机,并且指定routingKey
    @Bean
    Binding bindingDirectExchangeDemo3(   ) {
        return BindingBuilder.bind(queueDemo3()).to(exchangeDemo3()).with(routingKey);
    }

    //创建配置死信邮件队列
    @Bean
    public Queue deadQueue() {
        Queue queue = new Queue(deal_queue, true);
        return queue;
    }

    //创建死信交换机
    @Bean
    public DirectExchange deadExchange() {
        return new DirectExchange(deal_exchangeName);
    }

    //死信队列与死信交换机绑定
    @Bean
    public Binding bindingDeadExchange() {
        return BindingBuilder.bind(deadQueue()).to(deadExchange()).with(dead_RoutingKey);
    }


}
