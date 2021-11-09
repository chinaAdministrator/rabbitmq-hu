package com.oracle.controller;

import com.alibaba.fastjson.JSONObject;
import com.oracle.service.WeiXinPayService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class DeadMailReciver {

    @Autowired
    WeiXinPayService weiXinPayService;
    @RabbitListener(queues = "deal_queue_demo4")
    public void process(Message message, @Headers Map<String, Object> headers, Channel channel) throws IOException {

        // 获取消息Id
        System.out.println(message);
        String messageId = message.getMessageProperties().getMessageId();
        String msg = new String(message.getBody(), "UTF-8");
        System.out.println("死信邮件消费者获取生产者消息msg:"+msg+",消息id"+messageId);
        // 手动ack
        Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
        // 手动签收
        channel.basicAck(deliveryTag, false);
        System.out.println("执行结束....");

    }
}
