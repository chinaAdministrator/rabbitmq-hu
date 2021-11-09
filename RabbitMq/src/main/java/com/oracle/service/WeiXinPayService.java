package com.oracle.service;

import java.util.Map;

public interface WeiXinPayService {

    /**
     * 关闭支付
     * @param orderId
     * @return
     * @throws Exception
     */
    Map<String,String> closePay(Long orderId) ;


    /**
     * 创建二维码参数
     * @param params
     * @return
     */
    Map<String ,String> createNative(String outradeno,String totalfee);

    /**
     * 查询订单的支付状态
     * @param out_trade_no
     * @return
     */
    Map<String ,String> queryStatus(String out_trade_no);

    /**
     *
     * @param transactionID 是微信系统为每一笔支付交易分配的订单号，通过这个订单号可以标识这笔交易，它由支付订单API支付成功时返回的数据里面获取到。建议优先使用
     * @param outTradeNo 商户系统内部的订单号,transaction_id 、out_trade_no 二选一，如果同时存在优先级：transaction_id>out_trade_no
     * @param outRefundNo 商户系统内部的退款单号，商户系统内部唯一，同一退款单号多次请求只退一笔
     * @param totalFee 订单总金额，单位为分
     * @param refundFee 退款总金额，单位为分
     * @return
     */
    Map<String,String> doRefund(String transactionID,String outTradeNo,String outRefundNo,String totalFee,String refundFee);
}
