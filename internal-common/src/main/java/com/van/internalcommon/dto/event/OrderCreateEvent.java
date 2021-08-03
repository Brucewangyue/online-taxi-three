package com.van.internalcommon.dto.event;

import lombok.Data;

@Data
public class OrderCreateEvent {
    private String eventId;
    private int orderId;
    private String orderNumber;
    private String createUserName;
}
