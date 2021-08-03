package com.van.serviceorder.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.van.internalcommon.dto.ResponseResult;
import com.van.internalcommon.dto.event.OrderCreateEvent;
import com.van.serviceorder.dao.LocalEventDao;
import com.van.serviceorder.entity.LocalEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class OrderService {
    @Autowired
    LocalEventDao localEventDao;

    public ResponseResult create() throws JsonProcessingException {

//        List<LocalEvent> localEvents = localEventDao.selectAllUnSendEvent();
//
//        for (LocalEvent localEvent : localEvents){
//            localEventDao.up
//        }

        // 统一事务中，新增订单、新增本地事务表

        // todo 新增订单
        String eventId = UUID.randomUUID().toString().replace("-", "");

        OrderCreateEvent orderCreateEvent = new OrderCreateEvent();
        orderCreateEvent.setEventId(eventId);
        orderCreateEvent.setOrderId(1);
        orderCreateEvent.setOrderNumber("123123182371283");
        orderCreateEvent.setCreateUserName("Vansam");

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(orderCreateEvent);

        LocalEvent localEvent = new LocalEvent();
        localEvent.setId(eventId);
        localEvent.setContent(json);
        localEvent.setCreateDate(new Date());
        localEvent.setStatus((byte)1);
        localEvent.setEventType("order_create");

        localEventDao.insert(localEvent);

        return ResponseResult.success();
    }
}
