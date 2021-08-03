package com.van.serviceorder.task;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.van.serviceorder.dao.LocalEventDao;
import com.van.serviceorder.entity.LocalEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class OrderTask {
    @Autowired
    LocalEventDao localEventDao;

    @Autowired
    JmsTemplate jmsTemplate;

    @Scheduled(cron = "0/5 * * * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void createTask() throws JsonProcessingException {
        System.out.println("定时任务:createTask");

        List<LocalEvent> localEvents = localEventDao.selectAllUnSendEvent();
        for (LocalEvent localEvent : localEvents) {
            // 修改事务事件状态
            localEventDao.updateStatus(localEvent.getId(), 2);
            // 发送消息
            String json = new ObjectMapper().writeValueAsString(localEvent);
            jmsTemplate.convertAndSend("createOrder", json);
        }
    }
}
