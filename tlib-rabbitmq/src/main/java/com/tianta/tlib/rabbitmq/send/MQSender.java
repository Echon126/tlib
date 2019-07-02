package com.tianta.tlib.rabbitmq.send;

import com.alibaba.fastjson.JSON;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

import java.nio.charset.Charset;

public class MQSender {

    public MQSender(AmqpTemplate template) {
        this.template = template;
    }

    private AmqpTemplate template;

    /**
     * 新加mq发送方法
     * 内部采用fastJson序列化并以UTF-8转成byte[]发送
     * 接收方需要使用fastJson进行反序列化
     * 例如
     * <pre>{@code
     *      String code = new String(content, Charset.forName("UTF-8"));
     *      String resultMsg = JSON.parseObject(code, String.class);
     * }</pre>
     *
     * @param routingKey 路由规则
     * @param object     发送内容
     * @author yf
     * @version 1.0
     */
    public void send(String routingKey, Object object) {
        if (object == null) {
            throw new NullPointerException("发送数据为空");
        }
        String jsonString = JSON.toJSONString(object);
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        Message message = new Message(jsonString.getBytes(Charset.forName("UTF-8")), messageProperties);
        template.convertAndSend(routingKey, message);
    }

    /**
     * 新加mq发送方法
     * 内部采用fastJson序列化并以UTF-8转成byte[]发送
     * 接收方需要使用fastJson进行反序列化
     * 例如
     * <pre>{@code
     *      String code = new String(content, Charset.forName("UTF-8"));
     *      String resultMsg = JSON.parseObject(code, String.class);
     * }</pre>
     *
     * @param exchange   交换机名称
     * @param routingKey 路由规则
     * @param object     发送内容
     * @author yf
     * @version 1.0
     */
    public void send(String exchange, String routingKey, Object object) {
        if (object == null) {
            throw new NullPointerException("发送数据为空");
        }
        String jsonString = JSON.toJSONString(object);
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        Message message = new Message(jsonString.getBytes(Charset.forName("UTF-8")), messageProperties);
        template.convertAndSend(exchange, routingKey, message);
    }
}
