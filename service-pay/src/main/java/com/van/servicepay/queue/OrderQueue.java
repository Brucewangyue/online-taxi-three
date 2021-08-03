package com.van.servicepay.queue;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;

@Component
public class OrderQueue {
    @JmsListener(destination = "createOrder", containerFactory = "jmsListenerContainerFactory")
    public void receiveCreateOrderEvent(TextMessage textMessage, Session session) throws JMSException {
        try {
            System.out.println("接收消息");

//            textMessage.acknowledge();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("异常了");
            session.recover();
        }
    }
}
