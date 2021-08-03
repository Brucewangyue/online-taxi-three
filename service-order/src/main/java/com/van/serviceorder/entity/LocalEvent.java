package com.van.serviceorder.entity;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * local-event
 *
 * @author
 */
@Data
public class LocalEvent implements Serializable {
    private String id;

    private String eventType;

    private String content;

    /**
     * 1：new 2:send 3:accept 4: done
     */
    private Byte status;

    private Date createDate;

    /**
     * 这里应该修改为使用消息时间
     */
    private Date updateDate;

    private static final long serialVersionUID = 1L;
}