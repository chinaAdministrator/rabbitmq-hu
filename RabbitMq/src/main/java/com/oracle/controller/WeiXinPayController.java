package com.oracle.controller;

import com.github.wxpay.sdk.WXPayUtil;
import com.oracle.Enum.Result;
import com.oracle.config.QRcodeService;
import com.oracle.config.RecodeUtil;
import com.oracle.service.WeiXinPayService;
import com.oracle.utils.UUIDUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/weixin/pay")
@CrossOrigin
public class WeiXinPayController {

    @Autowired(required = false)
    private WeiXinPayService weiXinPayService;

    @Autowired
    CustomSender customSender;
    @Autowired
    private QRcodeService qRcodeService;


    @GetMapping("/closePay/{orderId}")
    public Result<Map<String,String>> closePay(@PathVariable("orderId") Long orderId) {
        Result<Map<String,String>> result = new Result<>();
        Map<String, String> map = weiXinPayService.closePay(orderId);
        result.setCMT("v","关闭微信支付成功",map);
        return result;
    }


    /**
     * 获取微信的回调信息(回调,获取支付信息)
     *
     * @return
     */
    @RequestMapping("/notify/url")
    public Map<String, String> notifyUrl(HttpServletRequest request) throws Exception {
        // 获取微信回调信息
        ServletInputStream inputStream = request.getInputStream();
        // 网络传输的字节流操作（内存操作）
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // 定义缓冲去
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, len);
        }
        byteArrayOutputStream.flush();
        byteArrayOutputStream.close();
        inputStream.close();
        // 获取数据
        String strXML = new String(byteArrayOutputStream.toByteArray(), "UTF-8");
        System.out.println(strXML);
        Map<String, String> map = WXPayUtil.xmlToMap(strXML);
        System.out.println("回调数据:" + map);
        // 将map转JSON
//        String jsonString = JSON.toJSONString(map);
        // 将参数发送到mq
//        rabbitTemplate.convertAndSend("exchange.order", "queue.order", jsonString);
        System.out.println("------------------------------------");
        return map;
    }

    /**
     * 生成支付的二维码
     *
     * @param  : 封装数据（订单，金额，用户名，交换机，路由）
     * @return
     */
    @RequestMapping("/create/native")
    public void createNavite(String outradeno, String totalfee, HttpServletResponse response) throws Exception {
        Result<Map<String,String>> result = new Result<>();
        List<String> showContent = new ArrayList<>();
        Integer uuid = UUIDUtils.getUUIDInOrderId();
        Map<String, String> map = weiXinPayService.createNative(String.valueOf(uuid),totalfee);
        System.out.println("生成的订单为："+uuid);
//        if (map.get("code_url")!=null){
//            String path = qRcodeService.encode(showContent, map.get("code_url"), new FileUtil());
//        }

         if (map.get("code_url")!=null){
             RecodeUtil.createCode(map.get("code_url"),response);
             customSender.sendMsg(String.valueOf(uuid));
        }
//        result.setCMT("v","创建支付二维码成功",map);
//        return result;
    }


    /**
     * 查询支付状态
     *
     * @param outtradeno
     * @return
     */
    @RequestMapping("/status/query")
    public Result<Map<String,String>> queryStatus(String outtradeno) {
        Result<Map<String,String>> result = new Result<>();
        Map<String, String> map = weiXinPayService.queryStatus(outtradeno);
        result.setCMT("v","查询支付状态成功",map);
        return result;
    }

    /**
     *
     * @param transactionID 是微信系统为每一笔支付交易分配的订单号，通过这个订单号可以标识这笔交易，它由支付订单API支付成功时返回的数据里面获取到。建议优先使用
     * @param outTradeNo 商户系统内部的订单号,transaction_id 、out_trade_no 二选一，如果同时存在优先级：transaction_id>out_trade_no
     * @param outRefundNo 商户系统内部的退款单号，商户系统内部唯一，同一退款单号多次请求只退一笔
     * @param totalFee 订单总金额，单位为分
     * @param refundFee 退款总金额，单位为分
     * @return
     */
    @RequestMapping("/refound")
    public Result<Map<String,String>> refound(String transactionID, String outTradeNo, String outRefundNo, String totalFee, String refundFee){
        Result<Map<String,String>> result = new Result<>();
        Map<String, String> map = weiXinPayService.doRefund(transactionID, outTradeNo, outRefundNo, totalFee, refundFee);
        result.setCMT("v","退款成功",map);
        return result;
    }
}
