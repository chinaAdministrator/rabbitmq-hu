package com.oracle.controller;


import com.alibaba.fastjson.JSONObject;
import com.oracle.config.RabbitMqConfig;
import com.oracle.service.WeiXinPayService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * 消息接收者
 * @author pan_junbiao
 **/
@Component
@Slf4j
public class CustomReceiver {

    @Autowired
    WeiXinPayService weiXinPayService;
    @Autowired
    RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = RabbitMqConfig.DELAY_QUEUE_NAME)
    public void receive(Message msg, Channel channel) throws InterruptedException, IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try{
            System.out.println(sdf.format(new Date()) + msg);
            System.out.println("查询订单状态"+msg);
            channel.basicAck(msg.getMessageProperties().getDeliveryTag(),false);
        }catch (Exception e){
            if (msg.getMessageProperties().getRedelivered()) {
                log.error("消息已重复处理失败,拒绝再次接收...");
                channel.basicReject(msg.getMessageProperties().getDeliveryTag(), false);
            } else {
                log.error("消息即将再次返回队列处理...");
                channel.basicNack(msg.getMessageProperties().getDeliveryTag(), false, true);
            }
        }

//        Map<String, String> stringStringMap = weiXinPayService.queryStatus(msg);
//        String trade_state = stringStringMap.get("trade_state");
//        if ("CLOSED".equals(trade_state)){
//            System.out.println("订单已关闭,订单号码"+msg);
//        }
//        if ("NOTPAY".equals(trade_state)){
//            System.out.println("订单未关闭,订单号码"+msg);
//            weiXinPayService.closePay(Long.valueOf(msg));
//            System.out.println("已关闭订单,订单号码为： " + msg);
//        }
//        if ("SUCCESS".equals(trade_state)){
//            System.out.println("订单已支付,订单号码"+msg);
//        }
    }

}
